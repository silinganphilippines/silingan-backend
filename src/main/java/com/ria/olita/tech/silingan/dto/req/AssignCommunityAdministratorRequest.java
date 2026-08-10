package com.ria.olita.tech.silingan.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AssignCommunityAdministratorRequest(
	@NotBlank
	@Email
	@Schema(example = "john.doe@company.com") String email
) {
}

