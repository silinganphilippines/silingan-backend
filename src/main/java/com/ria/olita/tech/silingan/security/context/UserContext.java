package com.ria.olita.tech.silingan.security.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.entity.rbac.Action;
import com.ria.olita.tech.silingan.entity.rbac.CommunityAccess;
import com.ria.olita.tech.silingan.entity.rbac.Domain;


import lombok.Builder;


@Builder
public record UserContext
	(String userId,
	 String keycloakUserId,
	 String communityId,
	 List<SilinganRealmRole> roles,
	 Map<UUID, CommunityAccess> communityAccessMap) {


	public void addCommunityAccess(CommunityAccess access) {
		communityAccessMap.put(access.getCommunityId(), access);
	}

	public CommunityAccess getAccess(UUID communityId) {
		return communityAccessMap.get(communityId);
	}


	public boolean canManage(Domain domain, UUID communityId) {
		if (isAdmin()) return true;

		CommunityAccess access = getAccess(communityId);
		return access != null && access.hasPermission(domain, Action.MANAGE);
	}

	public boolean canView(Domain domain, UUID communityId) {
		if (isAdmin()) return true;

		CommunityAccess access = getAccess(communityId);
		return access != null && access.hasPermission(domain, Action.VIEW);
	}


	public boolean canAccess(Domain domain, UUID communityId) {
		if (isAdmin()) return true;

		CommunityAccess access = getAccess(communityId);
		return access != null && access.canAccess(domain);
	}

	private boolean isAdmin() {
		return UserContextHolder.isPlatformAdmin() || UserContextHolder.isCommunityAdmin();
	}


}
