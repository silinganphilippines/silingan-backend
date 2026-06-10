package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.AnnouncementCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateAnnouncementRequest(

	@NotNull
	UUID communityId,

	@NotBlank
	@Size(max = 255)
	String title,

	@NotBlank
	String content,

	@NotNull
	AnnouncementCategory category,

	boolean pinned
) {
}
