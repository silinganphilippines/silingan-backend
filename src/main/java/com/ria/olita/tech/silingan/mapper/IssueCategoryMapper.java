package com.ria.olita.tech.silingan.mapper;

import com.ria.olita.tech.silingan.dto.req.CreateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.res.IssueCategoryResponse;
import com.ria.olita.tech.silingan.entity.IssueCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueCategoryMapper {

	IssueCategoryResponse toResponse(IssueCategory category);

	@Mapping(target = "community.id", source = "communityId")
	IssueCategory toEntity(CreateIssueCategoryRequest request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(UpdateIssueCategoryRequest request, @MappingTarget IssueCategory category);
}

