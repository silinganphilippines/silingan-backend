package com.ria.olita.tech.silingan.dto.req;

import java.util.UUID;

import com.ria.olita.tech.silingan.entity.CommunityType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateCommunityRequest(

	@NotBlank(message = "Name is required")
	@Schema(example = "Greenbelt Residences") String name,

	@NotNull(message = "Type is required")
	@Schema(example = "CONDO") CommunityType type,

	@NotNull(message = "Address is required")
	@Schema(example = "{\"street\":\"123 Main St\",\"barangay\":\"Poblacion\",\"city\":\"Makati\",\"province\":\"Metro Manila\",\"region\":13,\"postalCode\":\"1200\",\"country\":\"Philippines\"}")
	AddressRequest address,

	@NotNull(message = "Tenant is required")
	@Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID tenantId
) {


	@AssertTrue(message = "buildingName is required when community type is CONDO")
	public boolean isCondoBuildingNameValid() {
		if (type != CommunityType.CONDO) {
			return true;
		}
		return address != null && address.buildingName() != null;
	}

}
