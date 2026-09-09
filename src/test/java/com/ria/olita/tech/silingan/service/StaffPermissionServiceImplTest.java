package com.ria.olita.tech.silingan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.ria.olita.tech.silingan.dto.res.CommunityStaffMemberResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityStaffStatus;
import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.entity.StaffRole;
import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.entity.UserCommunity;
import com.ria.olita.tech.silingan.entity.UserCommunityStaffRole;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;
import com.ria.olita.tech.silingan.repository.CommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityPermissionRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityRepository;
import com.ria.olita.tech.silingan.repository.UserCommunityStaffRoleRepository;
import com.ria.olita.tech.silingan.repository.UserRepository;
import com.ria.olita.tech.silingan.security.scope.CommunityScopeGuard;
import com.ria.olita.tech.silingan.security.scope.StaffGrantGuard;
import com.ria.olita.tech.silingan.service.impl.StaffPermissionServiceImpl;

class StaffPermissionServiceImplTest {

	@Test
	void shouldReturnCentralizedStaffDirectoryWithAssignedRoleAndStatusFilter() {
		UserCommunityPermissionRepository permissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserCommunityStaffRoleRepository userCommunityStaffRoleRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		KeycloakService keycloakService = Mockito.mock(KeycloakService.class);

		StaffPermissionServiceImpl service = new StaffPermissionServiceImpl(
			permissionRepository,
			userRepository,
			communityRepository,
			userCommunityRepository,
			userCommunityStaffRoleRepository,
			keycloakService,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository)),
			Mockito.mock(CommunityRbacService.class)
		);

		UUID communityId = UUID.randomUUID();
		UUID staffUserId = UUID.randomUUID();
		UUID adminUserId = UUID.randomUUID();

		User staffUser = User.builder()
			.id(staffUserId)
			.keycloakUserId("kc-staff")
			.username("juan.staff")
			.firstName("Juan")
			.lastName("Dela Cruz")
			.email("juan@example.com")
			.mobileNumber("09170000001")
			.build();

		User adminUser = User.builder()
			.id(adminUserId)
			.keycloakUserId("kc-admin")
			.username("admin.user")
			.email("admin@example.com")
			.mobileNumber("09170000002")
			.build();

		UserCommunity staffMembership = UserCommunity.builder()
			.user(staffUser)
			.role(SilinganRealmRole.STAFF)
			.build();

		UserCommunity adminMembership = UserCommunity.builder()
			.user(adminUser)
			.role(SilinganRealmRole.COMMUNITY_ADMIN)
			.build();

		UserCommunityStaffRole staffAssignment = UserCommunityStaffRole.builder()
			.userId(staffUserId)
			.staffRole(StaffRole.builder().code(StaffRoleCode.PMO_STAFF).build())
			.active(true)
			.build();

		when(communityRepository.existsById(communityId)).thenReturn(true);
		when(userCommunityRepository.findStaffByCommunityWithFilters(
			communityId,
			EnumSet.of(SilinganRealmRole.STAFF, SilinganRealmRole.COMMUNITY_ADMIN),
			"juan"
		)).thenReturn(List.of(staffMembership, adminMembership));
		when(userCommunityStaffRoleRepository.findByCommunityIdAndUserIdInAndActiveTrue(
			Mockito.eq(communityId),
			Mockito.anyList()
		)).thenReturn(List.of(staffAssignment));
		when(keycloakService.isUserEnabled("kc-staff")).thenReturn(true);
		when(keycloakService.isUserEnabled("kc-admin")).thenReturn(false);

		List<CommunityStaffMemberResponse> result = service.getCommunityStaffDirectory(
			communityId,
			"juan",
			null,
			CommunityStaffStatus.ACTIVE
		);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().userId()).isEqualTo(staffUserId);
		assertThat(result.getFirst().fullName()).isEqualTo("Juan Dela Cruz");
		assertThat(result.getFirst().role()).isEqualTo(StaffRoleCode.PMO_STAFF);
		assertThat(result.getFirst().email()).isEqualTo("juan@example.com");
		assertThat(result.getFirst().mobileNumber()).isEqualTo("09170000001");
		assertThat(result.getFirst().status()).isEqualTo(CommunityStaffStatus.ACTIVE);
	}

	@Test
	void shouldFilterCommunityStaffDirectoryByAssignedRoleCode() {
		UserCommunityPermissionRepository permissionRepository = Mockito.mock(UserCommunityPermissionRepository.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		CommunityRepository communityRepository = Mockito.mock(CommunityRepository.class);
		UserCommunityRepository userCommunityRepository = Mockito.mock(UserCommunityRepository.class);
		UserCommunityStaffRoleRepository userCommunityStaffRoleRepository = Mockito.mock(UserCommunityStaffRoleRepository.class);
		KeycloakService keycloakService = Mockito.mock(KeycloakService.class);

		StaffPermissionServiceImpl service = new StaffPermissionServiceImpl(
			permissionRepository,
			userRepository,
			communityRepository,
			userCommunityRepository,
			userCommunityStaffRoleRepository,
			keycloakService,
			new StaffGrantGuard(new CommunityScopeGuard(userCommunityRepository)),
			Mockito.mock(CommunityRbacService.class)
		);

		UUID communityId = UUID.randomUUID();
		UUID user1Id = UUID.randomUUID();
		UUID user2Id = UUID.randomUUID();
		when(communityRepository.existsById(communityId)).thenReturn(true);

		User user1 = User.builder().id(user1Id).keycloakUserId("kc-1").username("u1").build();
		User user2 = User.builder().id(user2Id).keycloakUserId("kc-2").username("u2").build();

		UserCommunity member1 = UserCommunity.builder().user(user1).role(SilinganRealmRole.STAFF).build();
		UserCommunity member2 = UserCommunity.builder().user(user2).role(SilinganRealmRole.STAFF).build();

		when(userCommunityRepository.findStaffByCommunityWithFilters(
			communityId,
			EnumSet.of(SilinganRealmRole.STAFF, SilinganRealmRole.COMMUNITY_ADMIN),
			null
		)).thenReturn(List.of(member1, member2));

		UserCommunityStaffRole assignment1 = UserCommunityStaffRole.builder()
			.userId(user1Id)
			.staffRole(StaffRole.builder().code(StaffRoleCode.PMO_STAFF).build())
			.active(true)
			.build();
		UserCommunityStaffRole assignment2 = UserCommunityStaffRole.builder()
			.userId(user2Id)
			.staffRole(StaffRole.builder().code(StaffRoleCode.SECURITY_ADMIN).build())
			.active(true)
			.build();

		when(userCommunityStaffRoleRepository.findByCommunityIdAndUserIdInAndActiveTrue(
			Mockito.eq(communityId),
			Mockito.anyList()
		)).thenReturn(List.of(assignment1, assignment2));
		when(keycloakService.isUserEnabled("kc-1")).thenReturn(true);
		when(keycloakService.isUserEnabled("kc-2")).thenReturn(true);

		List<CommunityStaffMemberResponse> result = service.getCommunityStaffDirectory(
			communityId,
			null,
			StaffRoleCode.SECURITY_ADMIN,
			null
		);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().userId()).isEqualTo(user2Id);
		assertThat(result.getFirst().role()).isEqualTo(StaffRoleCode.SECURITY_ADMIN);
	}
}
