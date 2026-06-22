package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.CommunityStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommunityStatusUpdateRequest(
	@NotNull
	@Schema(example = "ACTIVE") CommunityStatus status
) {
}
