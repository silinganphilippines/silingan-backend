package com.ria.olita.tech.silingan.rest;

import java.util.List;
import java.util.Set;
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
import org.springframework.web.bind.annotation.RestController;

import com.ria.olita.tech.silingan.dto.req.AssignPermissionsRequest;
import com.ria.olita.tech.silingan.dto.res.AvailablePermissionsResponse;
import com.ria.olita.tech.silingan.dto.res.StaffPermissionResponse;
import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.service.StaffPermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/staff-permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMMUNITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
@Tag(name = "Staff Permissions", description = "Manage fine-grained permissions for community staff members")
public class StaffPermissionController {

	private final StaffPermissionService staffPermissionService;

	@GetMapping("/available")
	@Operation(
		summary = "Get available permissions",
		description = "Returns all available permissions that can be assigned to staff. Use this to populate permission selection UI."
	)
	@ApiResponse(responseCode = "200", description = "List of all available permissions",
		content = @Content(examples = @ExampleObject(value = """
			{
			  "permissions": [
			    {"permission": "ANNOUNCEMENT_VIEW", "domain": "announcement", "action": "view", "description": "View Announcement"},
			    {"permission": "ANNOUNCEMENT_MANAGE", "domain": "announcement", "action": "manage", "description": "Manage Announcement"},
			    {"permission": "REPORT_VIEW", "domain": "report", "action": "view", "description": "View Report"},
			    {"permission": "RESIDENT_MANAGE", "domain": "resident", "action": "manage", "description": "Manage Resident"}
			  ]
			}
			""")))
	public ResponseEntity<AvailablePermissionsResponse> getAvailablePermissions() {
		return ResponseEntity.ok(staffPermissionService.getAvailablePermissions());
	}

	@GetMapping
	@Operation(
		summary = "List all staff with permissions",
		description = "Returns all users who have been assigned permissions in this community."
	)
	@ApiResponse(responseCode = "200", description = "List of staff with their permissions",
		content = @Content(examples = @ExampleObject(value = """
			[
			  {
			    "userId": "550e8400-e29b-41d4-a716-446655440001",
			    "username": "juan.delacruz",
			    "firstName": "Juan",
			    "lastName": "Dela Cruz",
			    "communityId": "550e8400-e29b-41d4-a716-446655440000",
			    "permissions": ["ANNOUNCEMENT_MANAGE", "ANNOUNCEMENT_VIEW"]
			  },
			  {
			    "userId": "550e8400-e29b-41d4-a716-446655440002",
			    "username": "maria.santos",
			    "firstName": "Maria",
			    "lastName": "Santos",
			    "communityId": "550e8400-e29b-41d4-a716-446655440000",
			    "permissions": ["REPORT_VIEW", "REPORT_MANAGE"]
			  }
			]
			""")))
	public ResponseEntity<List<StaffPermissionResponse>> getAllStaffWithPermissions(
			@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID communityId) {
		return ResponseEntity.ok(staffPermissionService.getAllStaffWithPermissions(communityId));
	}

	@GetMapping("/users/{userId}")
	@Operation(
		summary = "Get staff permissions",
		description = "Returns the permissions assigned to a specific user in this community."
	)
	@ApiResponse(responseCode = "200", description = "Staff member's permissions",
		content = @Content(examples = @ExampleObject(value = """
			{
			  "userId": "550e8400-e29b-41d4-a716-446655440001",
			  "username": "juan.delacruz",
			  "firstName": "Juan",
			  "lastName": "Dela Cruz",
			  "communityId": "550e8400-e29b-41d4-a716-446655440000",
			  "permissions": ["ANNOUNCEMENT_MANAGE", "ANNOUNCEMENT_VIEW", "REPORT_VIEW"]
			}
			""")))
	@ApiResponse(responseCode = "404", description = "User not found")
	public ResponseEntity<StaffPermissionResponse> getStaffPermissions(
			@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID communityId,
			@Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440001")
			@PathVariable UUID userId) {
		return ResponseEntity.ok(staffPermissionService.getStaffPermissions(communityId, userId));
	}

	@PostMapping
	@Operation(
		summary = "Assign permissions to staff",
		description = "Assigns one or more permissions to a user. Existing permissions are preserved; only new ones are added."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "User ID and permissions to assign",
		content = @Content(schema = @Schema(implementation = AssignPermissionsRequest.class),
			examples = @ExampleObject(value = """
				{
				  "userId": "550e8400-e29b-41d4-a716-446655440001",
				  "permissions": ["ANNOUNCEMENT_MANAGE", "ANNOUNCEMENT_VIEW", "REPORT_VIEW"]
				}
				""")))
	@ApiResponse(responseCode = "201", description = "Permissions assigned successfully")
	@ApiResponse(responseCode = "404", description = "User or community not found")
	public ResponseEntity<StaffPermissionResponse> assignPermissions(
			@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID communityId,
			@Valid @RequestBody AssignPermissionsRequest request) {
		StaffPermissionResponse response = staffPermissionService.assignPermissions(communityId, request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/users/{userId}")
	@Operation(
		summary = "Replace staff permissions",
		description = "Replaces all permissions for a user with the provided set. Use this to update/sync permissions."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "New set of permissions (replaces existing)",
		content = @Content(examples = @ExampleObject(value = """
			["ANNOUNCEMENT_MANAGE", "REPORT_VIEW", "REPORT_MANAGE"]
			""")))
	@ApiResponse(responseCode = "200", description = "Permissions updated successfully")
	@ApiResponse(responseCode = "404", description = "User or community not found")
	public ResponseEntity<StaffPermissionResponse> updatePermissions(
			@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID communityId,
			@Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440001")
			@PathVariable UUID userId,
			@RequestBody Set<PermissionEnum> permissions) {
		return ResponseEntity.ok(staffPermissionService.updatePermissions(communityId, userId, permissions));
	}

	@DeleteMapping("/users/{userId}")
	@Operation(
		summary = "Revoke all permissions",
		description = "Removes all permissions from a user in this community. User will no longer have staff access."
	)
	@ApiResponse(responseCode = "204", description = "All permissions revoked")
	@ApiResponse(responseCode = "404", description = "User or community not found")
	public ResponseEntity<Void> revokeAllPermissions(
			@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID communityId,
			@Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440001")
			@PathVariable UUID userId) {
		staffPermissionService.revokeAllPermissions(communityId, userId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/users/{userId}/permissions/{permission}")
	@Operation(
		summary = "Revoke specific permission",
		description = "Removes a single permission from a user. Other permissions remain intact."
	)
	@ApiResponse(responseCode = "204", description = "Permission revoked")
	@ApiResponse(responseCode = "404", description = "User or community not found")
	public ResponseEntity<Void> revokePermission(
			@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID communityId,
			@Parameter(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440001")
			@PathVariable UUID userId,
			@Parameter(description = "Permission to revoke", example = "ANNOUNCEMENT_MANAGE")
			@PathVariable PermissionEnum permission) {
		staffPermissionService.revokePermission(communityId, userId, permission);
		return ResponseEntity.noContent().build();
	}
}

