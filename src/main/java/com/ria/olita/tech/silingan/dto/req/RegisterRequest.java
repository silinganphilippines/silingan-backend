package com.ria.olita.tech.silingan.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegisterRequest(

	@NotBlank
	String communityCode,

	@NotBlank
	String username,

	@NotBlank
	String password,

	@NotBlank
	String email,

	@NotBlank
	String fullName,

	@NotBlank
	AddressRequest address,

	@NotBlank
	String contactNumber
) {
}
