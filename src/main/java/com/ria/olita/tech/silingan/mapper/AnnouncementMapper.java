package com.ria.olita.tech.silingan.mapper;

import com.ria.olita.tech.silingan.dto.req.CreateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.res.AnnouncementResponse;
import com.ria.olita.tech.silingan.entity.Announcement;
import com.ria.olita.tech.silingan.entity.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnnouncementMapper {

	@Mapping(target = "createdBy", source = "createdBy.id")
	@Mapping(target = "updatedBy", source = "updatedBy.id")
	AnnouncementResponse toResponse(Announcement announcement);


	@Mapping(target = "status", expression = "java(com.ria.olita.tech.silingan.entity.AnnouncementStatus.DRAFT)")
	Announcement toEntity(CreateAnnouncementRequest request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(UpdateAnnouncementRequest request, @MappingTarget Announcement announcement);

	default UUID map(User user) {
		return user != null ? user.getId() : null;
	}
}
