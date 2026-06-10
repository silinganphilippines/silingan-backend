package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.AddressRequest;
import com.ria.olita.tech.silingan.dto.req.CreateCommunityRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateCommunityRequest;
import com.ria.olita.tech.silingan.dto.res.ApiResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityResponse;
import com.ria.olita.tech.silingan.entity.CommunityStatus;
import com.ria.olita.tech.silingan.entity.CommunityType;
import com.ria.olita.tech.silingan.service.CommunityCodeService;
import com.ria.olita.tech.silingan.service.CommunityService;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;
import com.ria.olita.tech.silingan.service.KeycloakService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityController {

	private final CommunityService communityService;

	@PostMapping
	public ResponseEntity<ApiResponse<CommunityResponse>> create(
		@Valid @RequestBody CreateCommunityRequest request) {
		CommunityResponse response = communityService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Community created successfully", response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CommunityResponse>> getById(@PathVariable UUID id) {
		CommunityResponse response = communityService.getById(id);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PermitAll
	@GetMapping("/code/{code}")
	public ResponseEntity<ApiResponse<CommunityResponse>> getByCode(@PathVariable String code) {
		CommunityResponse response = communityService.getByCode(code);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping
	@PreAuthorize("hasRole('PLATFORM_ADMIN')")
	public ResponseEntity<ApiResponse<List<CommunityResponse>>> getAll() {
		List<CommunityResponse> responses = communityService.getAll();
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/status/{status}")
	@PreAuthorize("hasRole('PLATFORM_ADMIN')")
	public ResponseEntity<ApiResponse<List<CommunityResponse>>> getByStatus(
		@PathVariable CommunityStatus status) {
		List<CommunityResponse> responses = communityService.getByStatus(status);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/type/{type}")
	@PreAuthorize("hasRole('PLATFORM_ADMIN')")
	public ResponseEntity<ApiResponse<List<CommunityResponse>>> getByType(
		@PathVariable CommunityType type) {
		List<CommunityResponse> responses = communityService.getByType(type);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('PLATFORM_ADMIN')")
	public ResponseEntity<ApiResponse<CommunityResponse>> update(
		@PathVariable UUID id,
		@RequestBody UpdateCommunityRequest request) {
		CommunityResponse response = communityService.update(id, request);
		return ResponseEntity.ok(ApiResponse.success("Community updated successfully", response));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('PLATFORM_ADMIN')")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
		communityService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Community deleted successfully", null));
	}

	@PutMapping("/{id}/archive")
	@PreAuthorize("hasRole('PLATFORM_ADMIN')")
	public ResponseEntity<ApiResponse<CommunityResponse>> archive(@PathVariable UUID id) {
		CommunityResponse response = communityService.archive(id);
		return ResponseEntity.ok(ApiResponse.success("Community archived successfully", response));
	}

	@GetMapping("/validate")
	public ResponseEntity<ApiResponse<Boolean>> validate(
		@RequestParam String communityId) {
		boolean isValid = communityService.validate(communityId);
		return ResponseEntity.ok(ApiResponse.success(isValid));
	}

	@GetMapping("/my-communities")
	@PreAuthorize("hasRole('RESIDENT')")
	public ResponseEntity<ApiResponse<List<CommunityResponse>>> getMyCommunities(
		@RequestParam UUID userId) {
		List<CommunityResponse> responses = communityService.getByUserId(userId);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@PutMapping("/switch/{communityId}")
	@PreAuthorize("hasRole('RESIDENT')")
	public ResponseEntity<ApiResponse<UUID>> switchCommunity(@PathVariable UUID communityId) {
		communityService.switchCommunity(communityId);
		return ResponseEntity.ok(ApiResponse.success("Community switched successfully", communityId));
	}
}
