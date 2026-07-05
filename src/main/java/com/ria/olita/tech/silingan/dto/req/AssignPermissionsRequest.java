package com.ria.olita.tech.silingan.dto.req;

import java.util.Set;
import java.util.UUID;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AssignPermissionsRequest(
	@NotNull UUID userId,
	@NotEmpty Set<PermissionEnum> permissions
) {
}

