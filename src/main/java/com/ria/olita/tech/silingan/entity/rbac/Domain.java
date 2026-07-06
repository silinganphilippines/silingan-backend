package com.ria.olita.tech.silingan.entity.rbac;

import lombok.Getter;

@Getter
public enum Domain {
	COMMUNITY("community"),
	RESIDENT("resident"),
	STAFF("staff"),
	ROLE("role"),
	ANNOUNCEMENT("announcement"),
	REPORT("report"),
	DIRECTORY("directory"),
	DASHBOARD("dashboard"),
	DOCUMENT("document");

	private final String value;

	Domain(String value) {
		this.value = value;
	}
}
