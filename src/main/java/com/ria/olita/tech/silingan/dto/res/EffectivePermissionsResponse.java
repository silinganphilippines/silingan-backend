package com.ria.olita.tech.silingan.dto.res;

import java.util.Set;
import java.util.UUID;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;

public record EffectivePermissionsResponse(
	UUID userId,
	UUID communityId,
	StaffRoleCode roleCode,
	Set<PermissionEnum> rolePermissions,
	Set<PermissionEnum> directPermissions,
	Set<PermissionEnum> effectivePermissions
) {
}
