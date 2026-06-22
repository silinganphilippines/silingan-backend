package com.ria.olita.tech.silingan.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ria.olita.tech.silingan.entity.rbac.Action;
import com.ria.olita.tech.silingan.entity.rbac.CommunityAccess;
import com.ria.olita.tech.silingan.entity.rbac.Domain;
import com.ria.olita.tech.silingan.security.context.UserContext;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;

@Service
public class PermissionService {

	public boolean check(Domain domain, Action action, UUID communityId) {
		UserContext user = UserContextHolder.get();
		if (UserContextHolder.isPlatformAdmin() || UserContextHolder.isCommunityAdmin()) {
			return true;
		}

		CommunityAccess access = user.getAccess(communityId);

		return access != null && access.hasPermission(domain, action);
	}


	public boolean canManage(Domain domain, UUID communityId) {
		return UserContextHolder.get()
			.canManage(domain, communityId);
	}

	public boolean canView(Domain domain, UUID communityId) {
		return UserContextHolder.get()
			.canView(domain, communityId);
	}

	public boolean canAccess(Domain domain, UUID communityId) {
		return UserContextHolder.get()
			.canAccess(domain, communityId);
	}
}

