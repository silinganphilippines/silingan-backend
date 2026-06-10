package com.ria.olita.tech.silingan.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateIssueCategoryRequest(
	@NotBlank(message = "Name is required")
	String name,

	String description,

	@NotNull(message = "Community ID is required")
	UUID communityId,

	Integer displayOrder
) {
}
