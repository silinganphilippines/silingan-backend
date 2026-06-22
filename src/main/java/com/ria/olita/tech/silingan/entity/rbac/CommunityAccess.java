package com.ria.olita.tech.silingan.entity.rbac;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;

@Builder
public class CommunityAccess {

	private UUID communityId;

	private List<CommunityRole> roles = new ArrayList<>();

	private Map<Domain, Set<Action>> permissions = new EnumMap<>(Domain.class);

	public CommunityAccess(UUID communityId) {
		this.communityId = communityId;
	}

	public void addRole(CommunityRole role) {
		this.roles.add(role);
	}

	public void addPermission(Domain domain, Action action) {
		permissions
			.computeIfAbsent(domain, d -> EnumSet.noneOf(Action.class))
			.add(action);
	}

	public boolean hasPermission(Domain domain, Action action) {
		return permissions
			.getOrDefault(domain, EnumSet.noneOf(Action.class))
			.contains(action);
	}

	public boolean canAccess(Domain domain) {
		return permissions.containsKey(domain);
	}

	public UUID getCommunityId() {
		return communityId;
	}

	public List<CommunityRole> getRoles() {
		return roles;
	}

	public Map<Domain, Set<Action>> getPermissions() {
		return permissions;
	}
}

