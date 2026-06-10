package com.ria.olita.tech.silingan.dto.req;

import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record CreateUserRequest(
	String username,
	String email,
	String firstName,
	String lastName,
	String password,
	boolean enabled,
	boolean emailVerified,
	Map<String, List<String>> attributes,
	String communityCode
) {
}
