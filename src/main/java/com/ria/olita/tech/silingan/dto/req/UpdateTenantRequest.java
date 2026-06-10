package com.ria.olita.tech.silingan.dto.req;

import com.ria.olita.tech.silingan.entity.TenantStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UpdateTenantRequest(
	String name,
	String contactPerson,

	@Email(message = "Contact email must be valid")
	String contactEmail,

	String contactNumber,
	TenantStatus status,

	@Valid
	AddressRequest billingAddress
) {
}
