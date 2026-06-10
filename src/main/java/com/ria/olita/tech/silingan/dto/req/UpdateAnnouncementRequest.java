package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.AnnouncementCategory;
import com.ria.olita.tech.silingan.entity.AnnouncementStatus;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateAnnouncementRequest(
	@Size(max = 255)
	String title,
	String content,
	AnnouncementCategory category,
	AnnouncementStatus status,
	boolean pinned
) {
}
