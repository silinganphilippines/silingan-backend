package com.ria.olita.tech.silingan.mapper;

import com.ria.olita.tech.silingan.dto.req.CreateCommunityRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateCommunityRequest;
import com.ria.olita.tech.silingan.dto.res.CommunityResponse;
import com.ria.olita.tech.silingan.entity.Community;
import com.ria.olita.tech.silingan.entity.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AddressMapper.class, TenantMapper.class})
public interface CommunityMapper {

	Community toEntity(CreateCommunityRequest request);

	@Mapping(source = "address", target = "address")
	@Mapping(source = "createdBy.id", target = "createdBy")
	@Mapping(source = "updatedBy.id", target = "updatedBy")
	CommunityResponse toResponse(Community community);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(UpdateCommunityRequest request, @MappingTarget Community community);

	default UUID map(User user) {
		return user != null ? user.getId() : null;
	}
}
