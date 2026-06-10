package com.ria.olita.tech.silingan.dto.res;

import com.ria.olita.tech.silingan.entity.Address;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AddressResponse(
	UUID id,
	String street,
	String barangay,
	String city,
	String province,
	String region,
	String postalCode,
	String country,
	String buildingName,
	String tower,
	String unitNumber,
	String floor,
	Double latitude,
	Double longitude
	) {

	public static AddressResponse fromEntity(Address address) {
		if (address == null) {
			return null;
		}
		return AddressResponse.builder()
			.id(address.getId())
			.street(address.getStreet())
			.barangay(address.getBarangay())
			.city(address.getCity())
			.province(address.getProvince())
			.region(address.getRegion())
			.postalCode(address.getPostalCode())
			.country(address.getCountry())
			.buildingName(address.getBuildingName())
			.tower(address.getTower())
			.unitNumber(address.getUnitNumber())
			.floor(address.getFloor())
			.latitude(address.getLatitude())
			.longitude(address.getLongitude())
			.build();
	}
}
