package com.ria.olita.tech.silingan.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AddressRequest(
	@Schema(example = "123 Main Street") String street,
	@Schema(example = "Poblacion") String barangay,
	@Schema(example = "Makati") String city,
	@Schema(example = "Metro Manila") String province,
	@Schema(example = "13") int region,
	@Schema(example = "1200") String postalCode,
	@Schema(example = "Philippines") String country,

	// condo or subdivision
	@Schema(example = "Greenbelt Tower") String buildingName,
	@Schema(example = "Tower 1") String tower,
	@Schema(example = "Unit 1205") String unitNumber,
	@Schema(example = "12") String floor
) {
}
