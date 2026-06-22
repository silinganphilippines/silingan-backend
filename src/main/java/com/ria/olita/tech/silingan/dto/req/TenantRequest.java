package com.ria.olita.tech.silingan.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TenantRequest(
	@NotBlank(message = "Tenant name is required")
	@Schema(example = "Acme Corporation") String name,

	@Schema(example = "Juan Dela Cruz") String contactPerson,

	@Email(message = "Contact email must be valid")
	@Schema(example = "juan@acme.com") String contactEmail,

	@Schema(example = "+63-912-345-6789") String contactNumber,

	@Valid
	@Schema(example = "{\"street\":\"123 Main St\",\"barangay\":\"Poblacion\",\"city\":\"Makati\",\"province\":\"Metro Manila\",\"region\":13,\"postalCode\":\"1200\",\"country\":\"Philippines\"}")
	AddressRequest billingAddress
) {
}
