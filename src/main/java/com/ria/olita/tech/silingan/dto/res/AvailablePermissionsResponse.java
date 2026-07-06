package com.ria.olita.tech.silingan.dto.res;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;

import java.util.List;

public record AvailablePermissionsResponse(
	List<PermissionInfo> permissions
) {
	public record PermissionInfo(
		PermissionEnum permission,
		String domain,
		String action,
		String description
	) {
	}
}

