package com.ria.olita.tech.silingan.dto.req;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


import com.ria.olita.tech.silingan.entity.SilinganRealmRole;

@Builder
public record CreateUserRequest(
	String username,
	String email,
	String firstName,
	String lastName,
	String password,
	boolean enabled,
	boolean emailVerified,
	SilinganRealmRole communityRole,
	@NotBlank
	UUID communityId,
	AddressRequest address
) {
}

