package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.CommunityStatus;
import com.ria.olita.tech.silingan.entity.CommunityType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UpdateCommunityRequest(
	@Schema(example = "Greenbelt Residences") String name,
	@Schema(example = "GR-001") String code,
	@Schema(example = "CONDO") CommunityType type,
	@Schema(example = "{\"street\":\"123 Main St\",\"barangay\":\"Poblacion\",\"city\":\"Makati\",\"province\":\"Metro Manila\",\"region\":13,\"postalCode\":\"1200\",\"country\":\"Philippines\"}")
	AddressRequest address,
	@Schema(example = "14.5547") Double latitude,
	@Schema(example = "121.0244") Double longitude,
	@Schema(example = "ACTIVE") CommunityStatus status
) {
}
