package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.CommunityStatus;
import com.ria.olita.tech.silingan.entity.CommunityType;

import lombok.Builder;

@Builder
public record UpdateCommunityRequest(
	String name,
	String code,
	CommunityType type,
	AddressRequest address,
	Double latitude,
	Double longitude,
	CommunityStatus status
) {
}
