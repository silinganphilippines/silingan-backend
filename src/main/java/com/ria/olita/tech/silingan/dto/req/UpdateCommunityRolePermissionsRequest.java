package com.ria.olita.tech.silingan.dto.req;

import java.util.Set;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;

import jakarta.validation.constraints.NotNull;

public record UpdateCommunityRolePermissionsRequest(
	@NotNull Set<PermissionEnum> permissions
) {
}
