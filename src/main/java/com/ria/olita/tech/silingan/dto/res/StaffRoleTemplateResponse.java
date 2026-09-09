package com.ria.olita.tech.silingan.dto.res;

import java.util.Set;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;

public record StaffRoleTemplateResponse(
	StaffRoleCode roleCode,
	String name,
	String description,
	Set<PermissionEnum> permissions,
	boolean customized
) {
}
