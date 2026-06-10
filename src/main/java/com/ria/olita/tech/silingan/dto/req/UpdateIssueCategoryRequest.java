package com.ria.olita.tech.silingan.dto.req;

import lombok.Builder;


@Builder
public record UpdateIssueCategoryRequest(
	String name,
	String description,
	Integer displayOrder,
	Boolean isActive
) {
}
