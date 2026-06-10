package com.ria.olita.tech.silingan.security.context;

import java.util.List;
import java.util.Map;

import com.ria.olita.tech.silingan.entity.CommunityRole;

import lombok.Builder;


@Builder
public record UserContext
	(String userId, String keycloakUserId, String communityId, List<CommunityRole> roles) {
}
