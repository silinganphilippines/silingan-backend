package com.ria.olita.tech.silingan.security.context;

import java.util.List;
import java.util.UUID;

import com.ria.olita.tech.silingan.entity.SilinganRealmRole;
import com.ria.olita.tech.silingan.entity.rbac.Action;
import com.ria.olita.tech.silingan.entity.rbac.CommunityAccess;
import com.ria.olita.tech.silingan.entity.rbac.Domain;

import lombok.Builder;

@Builder
public record UserContext(
	String userId,
	String keycloakUserId,
	String communityId,
	List<SilinganRealmRole> roles,
	CommunityAccess communityAccess
) {

	public CommunityAccess getAccess(UUID communityId) {
		if (communityAccess == null) return null;
		if (communityAccess.getCommunityId().equals(communityId)) {
			return communityAccess;
		}
		return null;
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
