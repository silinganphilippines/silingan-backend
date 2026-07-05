package com.ria.olita.tech.silingan.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateIssueCategoryRequest(
	@NotBlank(message = "Name is required")
	@Schema(example = "Plumbing") String name,

	@Schema(example = "Issues related to water pipes and leaks") String description,

	@NotNull(message = "Community ID is required")
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID communityId,

	@Schema(example = "1") Integer displayOrder
) {
}
