package com.ria.olita.tech.silingan.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ria.olita.tech.silingan.config.KeycloakProperties;
import com.ria.olita.tech.silingan.security.context.UserContextFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final KeycloakProperties keycloakProperties;

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		Logger log = LoggerFactory.getLogger(SecurityConfig.class);

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {

			logJwtClaims(jwt, log);

			List<GrantedAuthority> authorities = new ArrayList<>();

			extractRealmRoles(jwt, log).forEach(role ->
				authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
			);

			extractClientRoles(jwt, log, keycloakProperties.getClientId()).forEach(role ->
				authorities.add(new SimpleGrantedAuthority("CLIENT_ROLE_" + role.toUpperCase()))
			);

			logGrantedAuthorities(log, authorities);

			return authorities;
		});

		return converter;
	}

/* =============================
   Helper Methods
   ============================= */

	private void logJwtClaims(Jwt jwt, Logger log) {
		log.debug("=== JWT Claims Debug ===");
		jwt.getClaims()
			.forEach((key, value) ->
				log.debug("Claim '{}': {}", key, value)
			);
		log.debug("=========================");
	}

	private List<String> extractRealmRoles(Jwt jwt, Logger log) {
		List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

		if (roles != null) {
			log.debug("Extracted realm roles from JWT: {}", roles);
			return filterOutDefaultRoles(roles);
		}

		Object realmAccess = jwt.getClaim("realm_access");
		log.debug("realm_access value: {}", realmAccess);

		if (realmAccess instanceof Map<?, ?> map) {
			Object rolesObj = map.get("roles");
			log.debug("roles from realm_access map: {}", rolesObj);

			if (rolesObj instanceof List<?> rawRoles) {
				List<String> extracted = rawRoles.stream()
					.map(Object::toString)
					.toList();
				log.debug("Extracted realm roles from Map (filtered): {}", extracted);
				return filterOutDefaultRoles(extracted);
			}
		}

		log.debug("No realm roles found in JWT");
		return List.of();
	}

	private List<String> extractClientRoles(Jwt jwt, Logger log, String clientId) {

		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

		if (resourceAccess == null) {
			log.debug("No resource_access claim found in JWT");
			return List.of();
		}

		log.debug("Resource access found in JWT: {}", resourceAccess.keySet());

		if (!resourceAccess.containsKey(clientId)) {
			log.debug("Client ID '{}' not found in resource_access. Available clients: {}",
				clientId, resourceAccess.keySet());
			return List.of();
		}

		Object clientAccessObj = resourceAccess.get(clientId);

		if (clientAccessObj instanceof Map<?, ?> clientAccess) {

			Object rolesObj = clientAccess.get("roles");

			if (rolesObj instanceof List<?> rawRoles) {
				List<String> roles = rawRoles.stream()
					.map(Object::toString)
					.toList();
				log.debug("Extracted client roles for {}: {}", clientId, roles);
				return roles;
			}
		}

		log.debug("No client roles found for client: {}", clientId);
		return List.of();
	}

	private List<String> filterOutDefaultRoles(List<String> roles) {
		return roles.stream()
			.filter(role ->
				!role.startsWith("default-") &&
					!role.equals("offline_access") &&
					!role.equals("uma_authorization")
			)
			.toList();
	}

	private void logGrantedAuthorities(Logger log, List<GrantedAuthority> authorities) {
		log.info("JWT authentication successful - Total authorities granted: {}", authorities.size());

		if (authorities.isEmpty()) {
			log.warn("No authorities granted - check if user has roles assigned in Keycloak!");
			return;
		}

		String granted = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(", "));

		log.info("Granted authorities: {}", granted);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, UserContextFilter userContextFilter) throws Exception {

		http
			// H2 console needs full CSRF disabled
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/public/**", "/auth/register")
				.permitAll()
				.requestMatchers("/admin/**")
				.hasRole("PLATFORM_ADMIN")
				.requestMatchers("/community-admin/**")
				.hasRole("COMMUNITY_ADMIN")
				.requestMatchers("/resident/**")
				.hasRole("RESIDENT")
				.anyRequest()
				.authenticated()
			)
			// Keep OAuth2 login (Keycloak UI login)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/oauth2/authorization/silingan")
				.defaultSuccessUrl("/", true)
			)
			// Keep JWT resource server for API clients
			.oauth2ResourceServer(oauth2 ->
				oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
			);
		// Ensure this runs after Bearer token filter
		http.addFilterAfter(userContextFilter, BearerTokenAuthenticationFilter.class);

		return http.build();
	}

}
