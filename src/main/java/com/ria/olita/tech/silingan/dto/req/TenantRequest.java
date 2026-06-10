package com.ria.olita.tech.silingan.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TenantRequest(
	@NotBlank(message = "Tenant name is required")
	String name,

	String contactPerson,

	@Email(message = "Contact email must be valid")
	String contactEmail,

	String contactNumber,

	@Valid
	AddressRequest billingAddress
) {
}
