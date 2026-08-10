package com.ria.olita.tech.silingan.dto.res;

import com.ria.olita.tech.silingan.entity.CommunityAdminInvitationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommunityAdminInvitationStatusResponse(
	UUID invitationId,
	UUID communityId,
	String communityName,
	String email,
	CommunityAdminInvitationStatus status,
	LocalDateTime invitedAt,
	LocalDateTime acceptedAt
) {
}


