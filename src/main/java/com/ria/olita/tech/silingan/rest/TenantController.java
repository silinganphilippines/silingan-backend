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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
@Tag(name = "Tenant", description = "Tenant management APIs")
public class TenantController {

	private final TenantService tenantService;

	@PostMapping
	@Operation(
		summary = "Create a new tenant",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = TenantRequest.class),
				examples = {
					@ExampleObject(
						name = "tenantExample",
						summary = "Example tenant creation",
						value = """
							{
								"name": "Acme Corporation",
								"contactPerson": "Juan Dela Cruz",
								"contactEmail": "juan@acme.com",
								"contactNumber": "+63-912-345-6789",
								"billingAddress": {
									"street": "123 Main St",
									"barangay": "Poblacion",
									"city": "Makati",
									"province": "Metro Manila",
									"region": 13,
									"postalCode": "1200",
									"country": "Philippines"
								}
							}
							"""
					)
				}
			)
		)
	)
	public ResponseEntity<ApiResponse<TenantResponse>> create(
		@Valid @RequestBody TenantRequest request) {
		TenantResponse response = tenantService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Tenant created successfully", response));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get tenant by ID")
	public ResponseEntity<ApiResponse<TenantResponse>> getById(
		@Parameter(description = "Tenant ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		TenantResponse response = tenantService.getById(id);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping
	@Operation(summary = "Get all tenants, optionally filtered by status")
	public ResponseEntity<ApiResponse<List<TenantResponse>>> getAll(
		@Parameter(in = ParameterIn.QUERY, description = "Filter by tenant status", example = "ACTIVE") @RequestParam(required = false) TenantStatus status) {
		List<TenantResponse> responses = status == null
			? tenantService.getAll()
			: tenantService.getByStatus(status);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@PutMapping("/{id}")
	@Operation(
		summary = "Update an existing tenant",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = UpdateTenantRequest.class),
				examples = {
					@ExampleObject(
						name = "updateTenantExample",
						summary = "Example tenant update",
						value = """
							{
								"name": "Acme Corporation",
								"contactPerson": "Juan Dela Cruz",
								"contactEmail": "juan@acme.com",
								"contactNumber": "+63-912-345-6789",
								"status": "ACTIVE",
								"billingAddress": {
									"street": "123 Main St",
									"barangay": "Poblacion",
									"city": "Makati",
									"province": "Metro Manila",
									"region": 13,
									"postalCode": "1200",
									"country": "Philippines"
								}
							}
							"""
					)
				}
			)
		)
	)
	public ResponseEntity<ApiResponse<TenantResponse>> update(
		@Parameter(description = "Tenant ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
		@Valid @RequestBody UpdateTenantRequest request) {
		TenantResponse response = tenantService.update(id, request);
		return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", response));
	}

	@PutMapping("/{id}/status")
	@Operation(summary = "Update tenant status")
	public ResponseEntity<ApiResponse<TenantResponse>> updateStatus(
		@Parameter(description = "Tenant ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("id") UUID id,
		@Parameter(in = ParameterIn.QUERY, description = "New tenant status", example = "ACTIVE") @RequestParam TenantStatus status) {
		TenantResponse response = tenantService.updateStatus(id, status);
		return ResponseEntity.ok(ApiResponse.success("Tenant status updated successfully", response));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a tenant")
	public ResponseEntity<ApiResponse<Void>> delete(
		@Parameter(description = "Tenant ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		tenantService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully", null));
	}
}
