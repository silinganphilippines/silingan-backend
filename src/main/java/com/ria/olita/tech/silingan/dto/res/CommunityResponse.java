package com.ria.olita.tech.silingan.dto.res;

import com.ria.olita.tech.silingan.entity.CommunityStatus;
import com.ria.olita.tech.silingan.entity.CommunityType;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record CommunityResponse(
	UUID id,
	String communityCode,
	String systemGenCode,
	String name,
	CommunityType type,
	AddressResponse address,
	TenantResponse tenant,
	Double latitude,
	Double longitude,
	CommunityStatus status,
	int memberCount,

	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	UUID createdBy,
	UUID updatedBy
) {
}
