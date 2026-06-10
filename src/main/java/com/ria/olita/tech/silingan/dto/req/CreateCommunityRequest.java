package com.ria.olita.tech.silingan.dto.req;

import java.util.UUID;

import com.ria.olita.tech.silingan.entity.CommunityType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateCommunityRequest(

	@NotBlank(message = "Name is required")
	String name,

	@NotNull(message = "Type is required")
	CommunityType type,

	@NotNull(message = "Address is required")
	AddressRequest address,

	@NotNull(message = "Tenant is required")
	UUID tenantId
) {


	@AssertTrue(message = "buildingName is required when community type is CONDO")
	public boolean isCondoBuildingNameValid() {
		if (type != CommunityType.CONDO) {
			return true;
		}
		return address != null && address.buildingName() != null;
	}

}
