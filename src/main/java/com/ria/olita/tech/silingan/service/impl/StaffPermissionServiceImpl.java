package com.ria.olita.tech.silingan.service.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ria.olita.tech.silingan.dto.req.AssignPermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse.PermissionInfo;
import com.ria.olita.tech.silingan.dto.res.StaffPermissionResponse;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunityPermission;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.exception.NotFoundException;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityPermissionRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.service.StaffPermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffPermissionServiceImpl implements StaffPermissionService {

	private final UserCommunityPermissionRepository permissionRepository;
	private final UserRepository userRepository;
	private final CommunityRepository communityRepository;

	@Override
	@Transactional
	public StaffPermissionResponse assignPermissions(UUID communityId, AssignPermissionsRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new NotFoundException("User not found"));
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new NotFoundException("Community not found"));

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
}

