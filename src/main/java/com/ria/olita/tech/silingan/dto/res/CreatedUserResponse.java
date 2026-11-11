package com.ria.olita.tech.silingan.dto.res;

import com.ria.olita.tech.silingan.entity.SilinganRealmRole;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreatedUserResponse(
	UUID id,
	String keycloakUserId,
	String username,
	String email,
	String mobileNumber,
	UUID communityId,
	String communityCode,
	SilinganRealmRole communityRole
) {
}


