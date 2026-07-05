package com.ria.olita.tech.silingan.dto.req;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


import com.ria.olita.tech.silingan.entity.SilinganRealmRole;

@Builder
public record CreateUserRequest(
	@Schema(example = "jdelacruz") String username,
	@Schema(example = "juan@example.com") String email,
	@Schema(example = "Juan") String firstName,
	@Schema(example = "Dela Cruz") String lastName,
	@Schema(example = "SecurePass123!") String password,
	@Schema(example = "true", defaultValue = "true") Boolean enabled,
	@Schema(example = "true", defaultValue = "false") Boolean emailVerified,
	@Schema(example = "RESIDENT") SilinganRealmRole communityRole,
	@NotNull
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID communityId,
	@Schema(example = "{\"street\":\"123 Main St\",\"barangay\":\"Poblacion\",\"city\":\"Makati\",\"province\":\"Metro Manila\",\"region\":13,\"postalCode\":\"1200\",\"country\":\"Philippines\"}")
	AddressRequest address
) {
	public CreateUserRequest {
		// Set defaults if null
		if (enabled == null) {
			enabled = true;
		}
		if (emailVerified == null) {
			emailVerified = false;
		}
	}
}

