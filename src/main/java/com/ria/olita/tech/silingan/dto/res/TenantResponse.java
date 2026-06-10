package com.ria.olita.tech.silingan.dto.res;

import java.util.UUID;

import com.ria.olita.tech.silingan.entity.Tenant;
import com.ria.olita.tech.silingan.entity.TenantStatus;

import lombok.Builder;

@Builder
public record TenantResponse(
	UUID id,
	String name,
	String contactPerson,
	String contactEmail,
	String contactNumber,
	TenantStatus status,
	AddressResponse billingAddress
) {
	public static TenantResponse fromEntity(Tenant tenant) {
		if (tenant == null) {
			return null;
		}

		return TenantResponse.builder()
			.id(tenant.getId())
			.name(tenant.getName())
			.contactPerson(tenant.getContactPerson())
			.contactEmail(tenant.getContactEmail())
			.contactNumber(tenant.getContactNumber())
			.status(tenant.getStatus())
			.billingAddress(AddressResponse.fromEntity(tenant.getBillingAddress()))
			.build();
	}
}
