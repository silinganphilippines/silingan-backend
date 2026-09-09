package com.ria.olita.tech.silingan.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ria.olita.tech.silingan.dto.req.AssignPermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityStaffMemberResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityStaffStatus;
import com.ria.olita.tech.silingan.dto.res.StaffPermissionResponse;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;

public interface StaffPermissionService {

	StaffPermissionResponse assignPermissions(UUID communityId, AssignPermissionsRequest request);

	StaffPermissionResponse updatePermissions(UUID communityId, UUID userId, Set<PermissionEnum> permissions);

	void revokeAllPermissions(UUID communityId, UUID userId);

	void revokePermission(UUID communityId, UUID userId, PermissionEnum permission);

	StaffPermissionResponse getStaffPermissions(UUID communityId, UUID userId);

	List<StaffPermissionResponse> getAllStaffWithPermissions(UUID communityId);

	List<CommunityStaffMemberResponse> getCommunityStaffDirectory(
		UUID communityId,
		String searchTerm,
		StaffRoleCode role,
		CommunityStaffStatus status
	);

	AvailablePermissionsResponse getAvailablePermissions();
}
