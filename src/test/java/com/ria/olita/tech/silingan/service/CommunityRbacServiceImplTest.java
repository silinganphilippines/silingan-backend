package com.ria.olita.tech.silingan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.ria.olita.tech.silingan.dto.req.AssignStaffRoleRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateCommunityRolePermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.CurrentUserCapabilitiesResponse;
import com.ria.olita.tech.silingan.dto.res.EffectivePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.StaffRoleAssignmentResponse;
import com.ria.olita.tech.silingan.dto.res.StaffRoleTemplateResponse;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.CommunityRolePermissionOverride;
import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.entity.StaffRole;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunity;
import com.ria.olita.tech.silingan.entity.UserCommunityStaffRole;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.PermissionOverrideEffect;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;
import com.ria.olita.tech.silingan.exception.ForbiddenException;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.CommunityRolePermissionOverrideRepository;
import com.ria.olita.tech.silingan.repository.StaffRolePermissionRepository;
import com.ria.olita.tech.silingan.repository.StaffRoleRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityPermissionRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityStaffRoleRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.security.context.UserContext;
import com.ria.olita.tech.silingan.security.scope.CommunityScopeGuard;
import com.ria.olita.tech.silingan.security.scope.StaffGrantGuard;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;
import com.ria.olita.tech.silingan.service.impl.CommunityRbacServiceImpl;

class CommunityRbacServiceImplTest {

	@AfterEach
	void tearDown() {
		UserContextHolder.clear();
	}

	private static void givenPlatformAdminCaller() {
		UserContextHolder.set(UserContext.builder()
			.userId(UUID.randomUUID().toString())
			.roles(List.of(SilinganRealmRole.PLATFORM_ADMIN))
			.build());
	}

	@Test
	void shouldResolveEffectivePermissionsUsingRoleDefaultsOverridesAndDirectGrants() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID userId = UUID.randomUUID();
		UUID communityId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();

		StaffRole role = StaffRole.builder().id(roleId).code(StaffRoleCode.PMO_STAFF).build();
		UserCommunityStaffRole assignment = UserCommunityStaffRole.builder()
			.userId(userId)
			.communityId(communityId)
			.staffRole(role)
			.active(true)
			.build();

		when(assignmentRepository.findByUserIdAndCommunityIdAndActiveTrue(userId, communityId))
			.thenReturn(Optional.of(assignment));
		when(staffRolePermissionRepository.findPermissionsByStaffRoleId(roleId))
			.thenReturn(List.of(PermissionEnum.DASHBOARD_VIEW, PermissionEnum.REPORT_VIEW));
		when(overrideRepository.findByCommunityIdAndStaffRoleId(communityId, roleId))
			.thenReturn(List.of(
				CommunityRolePermissionOverride.builder()
					.permission(PermissionEnum.DASHBOARD_VIEW)
					.effect(PermissionOverrideEffect.DENY)
					.build(),
				CommunityRolePermissionOverride.builder()
					.permission(PermissionEnum.DOCUMENT_VIEW)
					.effect(PermissionOverrideEffect.ALLOW)
					.build()
			));
		when(directPermissionRepository.findPermissionsByUserIdAndCommunityId(userId, communityId))
			.thenReturn(List.of(PermissionEnum.ANNOUNCEMENT_MANAGE));

		assertThat(service.resolveEffectivePermissions(userId, communityId))
			.containsExactlyInAnyOrder(
				PermissionEnum.REPORT_VIEW,
				PermissionEnum.DOCUMENT_VIEW,
				PermissionEnum.ANNOUNCEMENT_MANAGE
			);
	}

	@Test
	void shouldUpdateCommunityRolePermissionsThroughOverrides() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID communityId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();
		StaffRole role = StaffRole.builder()
			.id(roleId)
			.code(StaffRoleCode.PMO_STAFF)
			.name("PMO Staff")
			.description("Operations")
			.build();

		when(communityRepository.existsById(communityId)).thenReturn(true);
		when(staffRoleRepository.findByCode(StaffRoleCode.PMO_STAFF)).thenReturn(Optional.of(role));
		when(staffRolePermissionRepository.findPermissionsByStaffRoleId(roleId))
			.thenReturn(List.of(PermissionEnum.DASHBOARD_VIEW, PermissionEnum.REPORT_VIEW));

		givenPlatformAdminCaller();

		StaffRoleTemplateResponse response = service.updateCommunityRolePermissions(
			communityId,
			StaffRoleCode.PMO_STAFF,
			new UpdateCommunityRolePermissionsRequest(EnumSet.of(PermissionEnum.REPORT_VIEW, PermissionEnum.DOCUMENT_VIEW))
		);

		assertThat(response.permissions()).containsExactlyInAnyOrder(PermissionEnum.REPORT_VIEW, PermissionEnum.DOCUMENT_VIEW);
		assertThat(response.customized()).isTrue();

		verify(overrideRepository).deleteByCommunityIdAndStaffRoleId(communityId, roleId);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<CommunityRolePermissionOverride>> captor = ArgumentCaptor.forClass((Class<List<CommunityRolePermissionOverride>>) (Class<?>) List.class);
		verify(overrideRepository).saveAll(captor.capture());

		assertThat(captor.getValue())
			.extracting(CommunityRolePermissionOverride::getPermission, CommunityRolePermissionOverride::getEffect)
			.containsExactlyInAnyOrder(
				org.assertj.core.groups.Tuple.tuple(PermissionEnum.DASHBOARD_VIEW, PermissionOverrideEffect.DENY),
				org.assertj.core.groups.Tuple.tuple(PermissionEnum.DOCUMENT_VIEW, PermissionOverrideEffect.ALLOW)
			);
	}

	@Test
	void shouldAssignRoleToCommunityStaffMember() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID userId = UUID.randomUUID();
		UUID communityId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();

		User user = User.builder().id(userId).build();
		Community community = Community.builder().id(communityId).build();
		StaffRole role = StaffRole.builder().id(roleId).code(StaffRoleCode.SECURITY_ADMIN).build();
		UserCommunity membership = UserCommunity.builder().user(user).community(community).role(SilinganRealmRole.STAFF).build();

		when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)).thenReturn(Optional.of(membership));
		when(staffRoleRepository.findByCode(StaffRoleCode.SECURITY_ADMIN)).thenReturn(Optional.of(role));
		when(assignmentRepository.findByUserIdAndCommunityId(userId, communityId)).thenReturn(Optional.empty());
		when(assignmentRepository.save(any(UserCommunityStaffRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

		givenPlatformAdminCaller();

		StaffRoleAssignmentResponse response = service.assignStaffRole(
			communityId,
			userId,
			new AssignStaffRoleRequest(StaffRoleCode.SECURITY_ADMIN, true)
		);

		assertThat(response.userId()).isEqualTo(userId);
		assertThat(response.communityId()).isEqualTo(communityId);
		assertThat(response.roleCode()).isEqualTo(StaffRoleCode.SECURITY_ADMIN);
		assertThat(response.active()).isTrue();
		assertThat(response.assignedAt()).isNotNull();
	}

	@Test
	void shouldRejectAssigningRoleToNonStaffMember() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID userId = UUID.randomUUID();
		UUID communityId = UUID.randomUUID();
		User user = User.builder().id(userId).build();
		Community community = Community.builder().id(communityId).build();
		UserCommunity membership = UserCommunity.builder().user(user).community(community).role(SilinganRealmRole.RESIDENT).build();

		when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userCommunityRepository.findByUserIdAndCommunityId(userId, communityId)).thenReturn(Optional.of(membership));

		givenPlatformAdminCaller();

		assertThatThrownBy(() -> service.assignStaffRole(
			communityId,
			userId,
			new AssignStaffRoleRequest(StaffRoleCode.PMO_STAFF, true)
		)).isInstanceOf(ForbiddenException.class);
	}

	@Test
	void shouldReturnCommunityRoleTemplateWithOverrideApplied() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID communityId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();
		StaffRole role = StaffRole.builder()
			.id(roleId)
			.code(StaffRoleCode.PMO_STAFF)
			.name("PMO Staff")
			.description("Operations")
			.build();

		when(communityRepository.existsById(communityId)).thenReturn(true);
		when(staffRoleRepository.findAllByOrderByNameAsc()).thenReturn(List.of(role));
		when(staffRolePermissionRepository.findPermissionsByStaffRoleId(roleId))
			.thenReturn(List.of(PermissionEnum.ANNOUNCEMENT_VIEW, PermissionEnum.REPORT_VIEW));
		when(overrideRepository.findByCommunityIdAndStaffRoleId(communityId, roleId))
			.thenReturn(List.of(
				CommunityRolePermissionOverride.builder()
					.permission(PermissionEnum.REPORT_VIEW)
					.effect(PermissionOverrideEffect.DENY)
					.build(),
				CommunityRolePermissionOverride.builder()
					.permission(PermissionEnum.DOCUMENT_VIEW)
					.effect(PermissionOverrideEffect.ALLOW)
					.build()
			));

		List<StaffRoleTemplateResponse> roles = service.getCommunityRoleTemplates(communityId);

		assertThat(roles).hasSize(1);
		assertThat(roles.getFirst().permissions())
			.containsExactlyInAnyOrder(PermissionEnum.ANNOUNCEMENT_VIEW, PermissionEnum.DOCUMENT_VIEW);
		assertThat(roles.getFirst().customized()).isTrue();
	}

	@Test
	void shouldReturnAllCapabilitiesForPlatformAdmin() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID communityId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		when(communityRepository.existsById(communityId)).thenReturn(true);

		UserContextHolder.set(UserContext.builder()
			.userId(userId.toString())
			.communityId(communityId.toString())
			.roles(List.of(SilinganRealmRole.PLATFORM_ADMIN))
			.build());

		CurrentUserCapabilitiesResponse response = service.getCurrentUserCapabilities(communityId);

		assertThat(response.userId()).isEqualTo(userId);
		assertThat(response.permissions()).containsExactlyInAnyOrder(PermissionEnum.values());
	}

	@Test
	void shouldReturnUnassignedRoleWhenNoStaffRoleExists() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID communityId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		when(communityRepository.existsById(communityId)).thenReturn(true);
		when(userRepository.existsById(userId)).thenReturn(true);
		when(assignmentRepository.findByUserIdAndCommunityId(userId, communityId)).thenReturn(Optional.empty());

		StaffRoleAssignmentResponse response = service.getStaffRoleAssignment(communityId, userId);

		assertThat(response.userId()).isEqualTo(userId);
		assertThat(response.communityId()).isEqualTo(communityId);
		assertThat(response.roleCode()).isNull();
		assertThat(response.active()).isFalse();
	}

	@Test
	void shouldReturnDirectPermissionsWhenNoActiveStaffRoleAssignment() {
		StaffRoleRepository staffRoleRepository = Mockito.mock(StaffRoleRepository.class);
		StaffRolePermissionRepository staffRolePermissionRepository = Mockito.mock(StaffRolePermissionRepository.class);
		CommunityRolePermissionOverrideRepository overrideRepository = Mockito.mock(CommunityRolePermissionOverrideRepository.class);
		UserCommunityStaffRoleRepository assignmentRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		UserCommunityPermissionRepository directPermissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);

		CommunityRbacServiceImpl service = new CommunityRbacServiceImpl(
			staffRoleRepository,
			staffRolePermissionRepository,
			overrideRepository,
			assignmentRepository,
			directPermissionRepository,
			userCommunityRepository,
			userRepository,
			communityRepository,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository))
		);

		UUID communityId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		when(communityRepository.existsById(communityId)).thenReturn(true);
		when(userRepository.existsById(userId)).thenReturn(true);
		when(assignmentRepository.findByUserIdAndCommunityIdAndActiveTrue(userId, communityId)).thenReturn(Optional.empty());
		when(directPermissionRepository.findPermissionsByUserIdAndCommunityId(userId, communityId))
			.thenReturn(List.of(PermissionEnum.DIRECTORY_VIEW, PermissionEnum.REPORT_VIEW));

		EffectivePermissionsResponse response = service.getEffectivePermissions(communityId, userId);

		assertThat(response.roleCode()).isNull();
		assertThat(response.rolePermissions()).isEmpty();
		assertThat(response.directPermissions())
			.containsExactlyInAnyOrder(PermissionEnum.DIRECTORY_VIEW, PermissionEnum.REPORT_VIEW);
		assertThat(response.effectivePermissions())
			.containsExactlyInAnyOrder(PermissionEnum.DIRECTORY_VIEW, PermissionEnum.REPORT_VIEW);
	}
}
