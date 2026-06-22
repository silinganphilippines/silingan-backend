package com.ria.olita.tech.silingan.entity.rbac;

public enum PermissionEnum {

	COMMUNITY_VIEW("community:view"),
	COMMUNITY_MANAGE("community:manage"),

	RESIDENT_VIEW("resident:view"),
	RESIDENT_MANAGE("resident:manage"),

	STAFF_VIEW("staff:view"),
	STAFF_MANAGE("staff:manage"),

	ROLE_VIEW("role:view"),
	ROLE_MANAGE("role:manage"),

	ANNOUNCEMENT_VIEW("announcement:view"),
	ANNOUNCEMENT_MANAGE("announcement:manage"),

	REPORT_VIEW("report:view"),
	REPORT_MANAGE("report:manage"),

	DIRECTORY_VIEW("directory:view"),
	DIRECTORY_MANAGE("directory:manage"),

	DASHBOARD_VIEW("dashboard:view"),

	NOTIFICATION_MANAGE("notification:manage"),

	DOCUMENT_VIEW("document:view"),
	DOCUMENT_MANAGE("document:manage");


	private final String value;

	PermissionEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

