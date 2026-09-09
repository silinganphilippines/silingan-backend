package com.ria.olita.tech.silingan.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ria.olita.tech.silingan.dto.req.UpdateCommunityRolePermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.CurrentUserCapabilitiesResponse;
import com.ria.olita.tech.silingan.dto.res.StaffRoleTemplateResponse;
import com.ria.olita.tech.silingan.entity.rbac.StaffRoleCode;
import com.ria.olita.tech.silingan.service.CommunityRbacService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Community RBAC", description = "Manage staff role templates and permission mappings")
public class CommunityRbacController {

	private final CommunityRbacService communityRbacService;

	@GetMapping("/rbac/permissions")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
	@Operation(summary = "Get all permission definitions")
	public ResponseEntity<AvailablePermissionsResponse> getPermissionCatalog() {
		return ResponseEntity.ok(communityRbacService.getPermissionCatalog());
	}

	@GetMapping("/rbac/staff-roles")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
	@Operation(summary = "Get default staff role templates")
	public ResponseEntity<List<StaffRoleTemplateResponse>> getDefaultRoleTemplates() {
		return ResponseEntity.ok(communityRbacService.getDefaultRoleTemplates());
	}

	@GetMapping("/communities/{communityId}/rbac/roles")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
	@Operation(summary = "Get effective staff role templates for a community")
	public ResponseEntity<List<StaffRoleTemplateResponse>> getCommunityRoleTemplates(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable UUID communityId
	) {
		return ResponseEntity.ok(communityRbacService.getCommunityRoleTemplates(communityId));
	}

	@PutMapping("/communities/{communityId}/rbac/roles/{roleCode}/permissions")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
	@Operation(summary = "Replace community-specific permissions for a staff role")
	public ResponseEntity<StaffRoleTemplateResponse> updateCommunityRolePermissions(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable UUID communityId,
		@Parameter(description = "Role code", example = "PMO_STAFF")
		@PathVariable StaffRoleCode roleCode,
		@Valid @RequestBody UpdateCommunityRolePermissionsRequest request
	) {
		return ResponseEntity.ok(communityRbacService.updateCommunityRolePermissions(communityId, roleCode, request));
	}

	@GetMapping("/me/communities/{communityId}/capabilities")
	@PreAuthorize("isAuthenticated()")
	@Operation(summary = "Get current user capabilities for a community")
	public ResponseEntity<CurrentUserCapabilitiesResponse> getCurrentUserCapabilities(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
		@PathVariable UUID communityId
	) {
		return ResponseEntity.ok(communityRbacService.getCurrentUserCapabilities(communityId));
	}
}
