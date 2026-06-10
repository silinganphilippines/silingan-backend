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
	String communityName,
	Integer displayOrder,
	Boolean isActive,
	Boolean deleted
) {
	public static IssueCategoryResponse fromEntity(IssueCategory category) {
		return IssueCategoryResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.description(category.getDescription())
			.communityId(category.getCommunity() != null ? category.getCommunity().getId() : null)
			.communityName(category.getCommunity() != null ? category.getCommunity().getName() : null)
			.displayOrder(category.getDisplayOrder())
			.isActive(category.getIsActive())
			.deleted(category.getDeleted())
			.build();
	}
}
