package com.ria.olita.tech.silingan.dto.req;

import lombok.Builder;

@Builder
public record AddressRequest(
	String street,
	String barangay,
	String city,
	String province,
	int region,
	String postalCode,
	String country,

	// condo or subdivision
	String buildingName,
	String tower,
	String unitNumber,
	String floor
) {
}
