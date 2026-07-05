package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.AnnouncementCategory;
import com.ria.olita.tech.silingan.entity.AnnouncementStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateAnnouncementRequest(
	@Size(max = 255)
	@Schema(example = "Community General Assembly") String title,
	@Schema(example = "Please attend the general assembly this Saturday at 2PM.") String content,
	@Schema(example = "NOTICE") AnnouncementCategory category,
	@Schema(example = "PUBLISHED") AnnouncementStatus status,
	@Schema(example = "false") boolean pinned
) {
}
