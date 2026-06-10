package com.ria.olita.tech.silingan.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ria.olita.tech.silingan.dto.req.TenantRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateTenantRequest;
import com.ria.olita.tech.silingan.dto.res.ApiResponse;
import com.ria.olita.tech.silingan.dto.res.TenantResponse;
import com.ria.olita.tech.silingan.entity.TenantStatus;
import com.ria.olita.tech.silingan.service.TenantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class TenantController {

	private final TenantService tenantService;

	@PostMapping
	public ResponseEntity<ApiResponse<TenantResponse>> create(@Valid @RequestBody TenantRequest request) {
		TenantResponse response = tenantService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Tenant created successfully", response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<TenantResponse>> getById(@PathVariable UUID id) {
		TenantResponse response = tenantService.getById(id);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<TenantResponse>>> getAll(
		@RequestParam(required = false) TenantStatus status) {
		List<TenantResponse> responses = status == null
			? tenantService.getAll()
			: tenantService.getByStatus(status);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<TenantResponse>> update(
		@PathVariable UUID id,
		@Valid @RequestBody UpdateTenantRequest request) {
		TenantResponse response = tenantService.update(id, request);
		return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<ApiResponse<TenantResponse>> updateStatus(
		@PathVariable UUID id,
		@RequestParam TenantStatus status) {
		TenantResponse response = tenantService.updateStatus(id, status);
		return ResponseEntity.ok(ApiResponse.success("Tenant status updated successfully", response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
		tenantService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully", null));
	}
}
