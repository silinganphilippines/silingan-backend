package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.CommunityStatus;

import jakarta.validation.constraints.NotNull;

public record CommunityStatusUpdateRequest(
	@NotNull
	CommunityStatus status
) {
}
