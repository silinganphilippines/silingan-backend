package com.ria.olita.tech.silingan.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UpdateIssueCategoryRequest(
	@Schema(example = "Plumbing") String name,
	@Schema(example = "Issues related to water pipes and leaks") String description,
	@Schema(example = "1") Integer displayOrder,
	@Schema(example = "true") Boolean isActive
) {
}
