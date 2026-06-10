package com.ria.olita.tech.silingan.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ria.olita.tech.silingan.dto.req.UpdateTenantRequest;
import com.ria.olita.tech.silingan.dto.req.TenantRequest;
import com.ria.olita.tech.silingan.dto.res.TenantResponse;
import com.ria.olita.tech.silingan.entity.Address;
import com.ria.olita.tech.silingan.entity.Tenant;
import com.ria.olita.tech.silingan.entity.TenantStatus;
import com.ria.olita.tech.silingan.exception.BadRequestException;
import com.ria.olita.tech.silingan.exception.ResourceNotFoundException;
import com.ria.olita.tech.silingan.mapper.AddressMapper;
import com.ria.olita.tech.silingan.mapper.TenantMapper;
import com.ria.olita.tech.silingan.repository.TenantRepository;
import com.ria.olita.tech.silingan.service.TenantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

	private final TenantRepository tenantRepository;
	private final TenantMapper tenantMapper;
	private final AddressMapper addressMapper;

	@Override
	public TenantResponse create(TenantRequest request) {
		// Validate request
		if (request == null) {
			throw new BadRequestException("Tenant request cannot be null");
		}
		
		if (request.name() == null || request.name().trim().isEmpty()) {
			throw new BadRequestException("Tenant name is required and cannot be empty");
		}
		
		try {
			Tenant tenant = tenantMapper.toEntity(request);
			
			// Ensure name is not null - defensive check
			if (tenant.getName() == null || tenant.getName().trim().isEmpty()) {
				tenant.setName(request.name());
			}
			
			if (request.billingAddress() != null) {
				tenant.setBillingAddress(addressMapper.toEntity(request.billingAddress()));
			}
			if (tenant.getStatus() == null) {
				tenant.setStatus(TenantStatus.PENDING);
			}

			Tenant saved = tenantRepository.save(tenant);
			return tenantMapper.toResponse(saved);
		} catch (Exception e) {
			log.error("Error creating tenant", e);
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public TenantResponse getById(UUID id) {
		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
		return tenantMapper.toResponse(tenant);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TenantResponse> getAll() {
		return tenantRepository.findAll()
			.stream()
			.map(tenantMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TenantResponse> getByStatus(TenantStatus status) {
		return tenantRepository.findByStatus(status)
			.stream()
			.map(tenantMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	public TenantResponse update(UUID id, UpdateTenantRequest request) {
		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));

		if (request.name() != null) {
			tenant.setName(request.name());
		}
		if (request.contactPerson() != null) {
			tenant.setContactPerson(request.contactPerson());
		}
		if (request.contactEmail() != null) {
			tenant.setContactEmail(request.contactEmail());
		}
		if (request.contactNumber() != null) {
			tenant.setContactNumber(request.contactNumber());
		}
		if (request.status() != null) {
			tenant.setStatus(request.status());
		}
		if (request.billingAddress() != null) {
			if (tenant.getBillingAddress() != null) {
				addressMapper.updateEntityFromRequest(request.billingAddress(), tenant.getBillingAddress());
			} else {
				Address address = addressMapper.toEntity(request.billingAddress());
				tenant.setBillingAddress(address);
			}
		}

		Tenant updated = tenantRepository.save(tenant);
		return tenantMapper.toResponse(updated);
	}

	@Override
	public TenantResponse updateStatus(UUID id, TenantStatus status) {
		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
		tenant.setStatus(status);
		Tenant updated = tenantRepository.save(tenant);
		return tenantMapper.toResponse(updated);
	}

	@Override
	public void delete(UUID id) {
		Tenant tenant = tenantRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
		tenant.setDeleted(true);
		tenantRepository.save(tenant);
	}
}
