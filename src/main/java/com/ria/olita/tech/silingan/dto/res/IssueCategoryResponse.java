package com.ria.olita.tech.silingan.dto.res;

import com.ria.olita.tech.silingan.entity.IssueCategory;
import lombok.Builder;

import java.util.UUID;

@Builder
public record IssueCategoryResponse(
	UUID id,
	String name,
	String description,
	UUID communityId,
	Integer displayOrder,
	Boolean isActive,
	Boolean deleted
) {
	public static IssueCategoryResponse fromEntity(IssueCategory category) {
		return IssueCategoryResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.description(category.getDescription())
			.communityId(category.getCommunityId())
			.displayOrder(category.getDisplayOrder())
			.isActive(category.getIsActive())
			.deleted(category.getDeleted())
			.build();
	}
}
