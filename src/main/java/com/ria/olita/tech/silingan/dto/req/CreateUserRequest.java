package com.ria.olita.tech.silingan.dto.req;

import java.util.UUID;

import com.ria.olita.tech.silingan.entity.CommunityRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateUserRequest(
	@Schema(example = "jdelacruz") String username,
	@Schema(example = "juan@example.com") String email,
	@Schema(example = "Juan") String firstName,
	@Schema(example = "Dela Cruz") String lastName,
	@Schema(example = "SecurePass123!") String password,
	@Schema(example = "true") boolean enabled,
	@Schema(example = "true") boolean emailVerified,
	@Schema(example = "RESIDENT") CommunityRole communityRole,
	@NotBlank
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID communityId,
	@Schema(example = "{\"street\":\"123 Main St\",\"barangay\":\"Poblacion\",\"city\":\"Makati\",\"province\":\"Metro Manila\",\"region\":13,\"postalCode\":\"1200\",\"country\":\"Philippines\"}")
	AddressRequest address
) {
}

