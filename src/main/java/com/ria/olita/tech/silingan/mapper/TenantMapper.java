package com.ria.olita.tech.silingan.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.ria.olita.tech.silingan.dto.req.TenantRequest;
import com.ria.olita.tech.silingan.dto.res.TenantResponse;
import com.ria.olita.tech.silingan.entity.Tenant;

@Mapper(componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	uses = AddressMapper.class)
public interface TenantMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "name", source = "name")
	@Mapping(target = "contactPerson", source = "contactPerson")
	@Mapping(target = "contactEmail", source = "contactEmail")
	@Mapping(target = "contactNumber", source = "contactNumber")
	Tenant toEntity(TenantRequest request);

	TenantResponse toResponse(Tenant tenant);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(TenantRequest request, @MappingTarget Tenant tenant);
}
