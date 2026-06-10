package com.ria.olita.tech.silingan.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.ria.olita.tech.silingan.entity.User;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {

	@Override
	public Optional<User> getCurrentAuditor() {
		return Optional.ofNullable(UserContextHolder.get())
			.filter(ctx -> ctx.userId() != null && !ctx.userId().trim().isEmpty())
			.map(ctx -> {
				User user = new User();
				user.setId(UUID.fromString(ctx.userId()));
				return user;
			});
	}
}
