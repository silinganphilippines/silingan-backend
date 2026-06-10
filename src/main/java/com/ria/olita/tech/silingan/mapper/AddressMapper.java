package com.ria.olita.tech.silingan.mapper;

import com.ria.olita.tech.silingan.dto.req.AddressRequest;
import com.ria.olita.tech.silingan.dto.res.AddressResponse;
import com.ria.olita.tech.silingan.entity.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	uses = RegionConverter.class)
public interface AddressMapper {

	@Mapping(target = "region", source = "region")
	Address toEntity(AddressRequest request);

	AddressResponse toResponse(Address address);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(AddressRequest request, @MappingTarget Address address);
}
