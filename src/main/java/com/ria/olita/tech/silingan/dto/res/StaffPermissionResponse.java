package com.ria.olita.tech.silingan.dto.res;

import java.util.Set;
import java.util.UUID;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;

public record StaffPermissionResponse(
	UUID userId,
	String username,
	String firstName,
	String lastName,
	UUID communityId,
	Set<PermissionEnum> permissions
) {
}

