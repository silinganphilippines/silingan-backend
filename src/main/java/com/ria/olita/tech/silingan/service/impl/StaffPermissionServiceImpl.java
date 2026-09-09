package com.ria.olita.tech.silingan.service.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ria.olita.tech.silingan.dto.req.AssignPermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse.PermissionInfo;
import com.ria.olita.tech.silingan.dto.res.CommunityStaffMemberResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityStaffStatus;
import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.dto.res.StaffPermissionResponse;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunity;
import com.ria.olita.tech.silingan.entity.UserCommunityPermission;
import com.ria.olita.tech.silingan.entity.UserCommunityStaffRole;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;
import com.ria.olita.tech.silingan.exception.NotFoundException;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityPermissionRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityStaffRoleRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.service.KeycloakService;
import com.ria.olita.tech.silingan.service.StaffPermissionService;
import com.ria.olita.tech.silingan.service.CommunityRbacService;
import com.ria.olita.tech.silingan.security.scope.StaffGrantGuard;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffPermissionServiceImpl implements StaffPermissionService {

	private final UserCommunityPermissionRepository permissionRepository;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;
	private final UserCommunityRepository userCommunityRepository;
	private final UserCommunityStaffRoleRepository userCommunityStaffRoleRepository;
	private final KeycloakService keycloakService;
	private final StaffGrantGuard staffGrantGuard;
	private final CommunityRbacService communityRbacService;

	private Set<PermissionEnum> callerPermissions(UUID communityId) {
		return communityRbacService.resolveEffectivePermissions(staffGrantGuard.requireCurrentUserId(), communityId);
	}

	private void assertTargetIsMember(UUID userId, UUID communityId) {
		userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)
			.orElseThrow(() -> new NotFoundException("User is not a member of the community"));
	}

	@Override
	@Transactional
	public StaffPermissionResponse assignPermissions(UUID communityId, AssignPermissionsRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new NotFoundException("Community not found"));

		assertTargetIsMember(request.userId(), communityId);
		staffGrantGuard.assertNotSelf(request.userId(), "change permissions");
		staffGrantGuard.assertCanGrant(communityId, request.permissions(), () -> callerPermissions(communityId));

		for (PermissionEnum permission : request.permissions()) {
			if (!permissionRepository.existsByUserIdAndCommunityIdAndPermission(
					request.userId(), communityId, permission)) {
				UserCommunityPermission ucp = UserCommunityPermission.builder()
					.user(user)
					.community(community)
					.permission(permission)
					.build();
				permissionRepository.save(ucp);
			}
		}
		return buildResponse(user, communityId);
	}

	@Override
	@Transactional
	public StaffPermissionResponse updatePermissions(UUID communityId, UUID userId, Set<PermissionEnum> permissions) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("User not found"));
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new NotFoundException("Community not found"));

		assertTargetIsMember(userId, communityId);
		staffGrantGuard.assertNotSelf(userId, "change permissions");
		staffGrantGuard.assertCanGrant(communityId, permissions, () -> callerPermissions(communityId));

		permissionRepository.deleteAllByUserIdAndCommunityId(userId, communityId);

		for (PermissionEnum permission : permissions) {
			UserCommunityPermission ucp = UserCommunityPermission.builder()
				.user(user)
				.community(community)
				.permission(permission)
				.build();
			permissionRepository.save(ucp);
		}

		return buildResponse(user, communityId);
	}

	@Override
	@Transactional
	public void revokeAllPermissions(UUID communityId, UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("User not found");
		}
		if (!communityRepository.existsById(communityId)) {
			throw new NotFoundException("Community not found");
		}
		permissionRepository.deleteAllByUserIdAndCommunityId(userId, communityId);
	}

	@Override
	@Transactional
	public void revokePermission(UUID communityId, UUID userId, PermissionEnum permission) {
		List<UserCommunityPermission> permissions = permissionRepository
			.findByUserIdAndCommunityId(userId, communityId);
		
		permissions.stream()
			.filter(p -> p.getPermission() == permission)
			.findFirst()
			.ifPresent(permissionRepository::delete);
	}

	@Override
	@Transactional(readOnly = true)
	public StaffPermissionResponse getStaffPermissions(UUID communityId, UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("User not found"));
		return buildResponse(user, communityId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StaffPermissionResponse> getAllStaffWithPermissions(UUID communityId) {
		List<UUID> userIds = permissionRepository.findUserIdsWithPermissionsInCommunity(communityId);
		
		return userIds.stream()
			.map(userId -> {
				User user = userRepository.findById(userId).orElse(null);
				if (user == null) return null;
				return buildResponse(user, communityId);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommunityStaffMemberResponse> getCommunityStaffDirectory(
			UUID communityId,
			String searchTerm,
			StaffRoleCode role,
			CommunityStaffStatus status) {
		if (!communityRepository.existsById(communityId)) {
			throw new NotFoundException("Community not found");
		}

		String normalizedSearchTerm = normalizeSearchTerm(searchTerm);
		Set<SilinganRealmRole> staffMembershipRoles = EnumSet.of(SilinganRealmRole.STAFF, SilinganRealmRole.COMMUNITY_ADMIN);

		List<UserCommunity> staffMembers = userCommunityRepository.findStaffByCommunityWithFilters(
			communityId,
			staffMembershipRoles,
			normalizedSearchTerm
		);

		Map<UUID, StaffRoleCode> assignedRoleByUserId = resolveAssignedRoleByUserId(communityId, staffMembers);

		return staffMembers
			.stream()
			.map(member -> toStaffDirectoryResponse(member, resolveDirectoryRole(member, assignedRoleByUserId)))
			.filter(member -> role == null || member.role() == role)
			.filter(member -> status == null || member.status() == status)
			.toList();
	}

	@Override
	public AvailablePermissionsResponse getAvailablePermissions() {
		List<PermissionInfo> permissions = Arrays.stream(PermissionEnum.values())
			.map(p -> {
				String[] parts = p.getValue().split(":");
				String domain = parts[0];
				String action = parts[1];
				String description = buildDescription(domain, action);
				return new PermissionInfo(p, domain, action, description);
			})
			.collect(Collectors.toList());

		return new AvailablePermissionsResponse(permissions);
	}

	private StaffPermissionResponse buildResponse(User user, UUID communityId) {
		List<PermissionEnum> perms = permissionRepository
			.findPermissionsByUserIdAndCommunityId(user.getId(), communityId);
		
		return new StaffPermissionResponse(
			user.getId(),
			user.getUsername(),
			user.getFirstName(),
			user.getLastName(),
			communityId,
			EnumSet.copyOf(perms.isEmpty() ? EnumSet.noneOf(PermissionEnum.class) : perms)
		);
	}

	private String buildDescription(String domain, String action) {
		String actionDesc = "view".equals(action) ? "View" : "Manage";
		return actionDesc + " " + capitalize(domain);
	}

	private String capitalize(String str) {
		if (str == null || str.isEmpty()) return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	private String normalizeSearchTerm(String searchTerm) {
		if (searchTerm == null || searchTerm.isBlank()) {
			return null;
		}
		return searchTerm.trim();
	}

	private CommunityStaffMemberResponse toStaffDirectoryResponse(UserCommunity userCommunity, StaffRoleCode roleCode) {
		User user = userCommunity.getUser();
		CommunityStaffStatus status = keycloakService.isUserEnabled(user.getKeycloakUserId())
			? CommunityStaffStatus.ACTIVE
			: CommunityStaffStatus.INACTIVE;

		return new CommunityStaffMemberResponse(
			user.getId(),
			buildFullName(user),
			roleCode,
			user.getEmail(),
			user.getMobileNumber(),
			status
		);
	}

	private Map<UUID, StaffRoleCode> resolveAssignedRoleByUserId(UUID communityId, List<UserCommunity> staffMembers) {
		if (staffMembers.isEmpty()) {
			return Map.of();
		}

		List<UUID> userIds = staffMembers.stream()
			.map(member -> member.getUser().getId())
			.toList();

		List<UserCommunityStaffRole> assignments = userCommunityStaffRoleRepository
			.findByCommunityIdAndUserIdInAndActiveTrue(communityId, userIds);

		Map<UUID, StaffRoleCode> assignedRoleByUserId = new HashMap<>();
		for (UserCommunityStaffRole assignment : assignments) {
			assignedRoleByUserId.put(assignment.getUserId(), assignment.getStaffRole().getCode());
		}
		return assignedRoleByUserId;
	}

	private StaffRoleCode resolveDirectoryRole(UserCommunity userCommunity, Map<UUID, StaffRoleCode> assignedRoleByUserId) {
		StaffRoleCode assignedRole = assignedRoleByUserId.get(userCommunity.getUser().getId());
		if (assignedRole != null) {
			return assignedRole;
		}
		if (userCommunity.getRole() == SilinganRealmRole.COMMUNITY_ADMIN) {
			return StaffRoleCode.COMMUNITY_ADMIN;
		}
		return null;
	}

	private String buildFullName(User user) {
		String fullName = (Objects.toString(user.getFirstName(), "") + " " + Objects.toString(user.getLastName(), "")).trim();
		return fullName.isEmpty() ? user.getUsername() : fullName;
	}
}
