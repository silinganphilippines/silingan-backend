package com.ria.olita.tech.silingan.dto.res;

import java.util.List;

public record LoginResponse(
	String accessToken,
	String tokenType,
	Long expiresIn,
	String userId,
	String keycloakId,
	String mobileNumber,
	List<String> roles
) {
}

