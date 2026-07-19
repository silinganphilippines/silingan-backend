package com.ria.olita.tech.silingan.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/secure")
public class SecuredProfileController {

	@GetMapping("/profile")
	@PreAuthorize("hasRole('RESIDENT') or hasRole('COMMUNITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
	public Map<String, Object> profile(JwtAuthenticationToken authentication) {
		Jwt jwt = authentication.getToken();
		Map<String, Object> response = new HashMap<>();
		response.put("sub", jwt.getSubject());
		response.put("keycloakId", jwt.getClaimAsString("keycloakId"));
		response.put("mobileNumber", jwt.getClaimAsString("mobileNumber"));
		response.put("roles", jwt.getClaimAsStringList("roles"));
		return response;
	}
}


