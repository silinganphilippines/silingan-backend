package com.ria.olita.tech.silingan.service;

import java.util.List;
import java.util.UUID;

import com.ria.olita.tech.silingan.dto.req.TenantRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateTenantRequest;
import com.ria.olita.tech.silingan.dto.res.TenantResponse;
import com.ria.olita.tech.silingan.entity.TenantStatus;

public interface TenantService {

	TenantResponse create(TenantRequest request);

	TenantResponse getById(UUID id);

	List<TenantResponse> getAll();

	List<TenantResponse> getByStatus(TenantStatus status);

	TenantResponse update(UUID id, UpdateTenantRequest request);

	TenantResponse updateStatus(UUID id, TenantStatus status);

	void delete(UUID id);
}
