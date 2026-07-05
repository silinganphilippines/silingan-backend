package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.TenantStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UpdateTenantRequest(
	@Schema(example = "Acme Corporation") String name,
	@Schema(example = "Juan Dela Cruz") String contactPerson,

	@Email(message = "Contact email must be valid")
	@Schema(example = "juan@acme.com") String contactEmail,

	@Schema(example = "+63-912-345-6789") String contactNumber,
	@Schema(example = "ACTIVE") TenantStatus status,

	@Valid
	@Schema(example = "{\"street\":\"123 Main St\",\"barangay\":\"Poblacion\",\"city\":\"Makati\",\"province\":\"Metro Manila\",\"region\":13,\"postalCode\":\"1200\",\"country\":\"Philippines\"}")
	AddressRequest billingAddress
) {
}
