package com.ria.olita.tech.silingan.security.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ria.olita.tech.silingan.entity.CommunityRole;

/**
 * ThreadLocal holder for {@link UserContext}. Ensures isolation per request/thread.
 */
public final class UserContextHolder {

	private static final Logger log = LoggerFactory.getLogger(UserContextHolder.class);
	private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

	private UserContextHolder() {
	}

	public static void set(UserContext context) {
		if (context == null) {
			log.debug("Attempted to set null UserContext; clearing instead.");
			clear();
			return;
		}
		CONTEXT.set(context);
	}

	public static UserContext get() {
		return CONTEXT.get();
	}

	public static void clear() {
		CONTEXT.remove();
	}


	public static boolean isPlatformAdmin() {
		UserContext context = get();

		if (context == null || context.roles() == null) {
			return false;
		}

		return context.roles().contains(CommunityRole.PLATFORM_ADMIN);
	}

}
