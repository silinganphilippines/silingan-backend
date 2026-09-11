package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.res.ApiResponse;
import com.ria.olita.tech.silingan.dto.res.CommunityResponse;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;
import com.ria.olita.tech.silingan.service.CommunityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/secure/communities")
@RequiredArgsConstructor
@Tag(name = "Secured Community", description = "Community APIs requiring an authenticated resident")
public class SecuredCommunityController {

	private final CommunityService communityService;

	@GetMapping("/my-communities")
	@PreAuthorize("hasRole('RESIDENT')")
	@Operation(summary = "Get communities the authenticated resident has joined")
	public ResponseEntity<ApiResponse<List<CommunityResponse>>> getMyCommunities() {
		// userId is derived from the authenticated token, never from client input, to prevent IDOR
		UUID userId = UUID.fromString(UserContextHolder.get().userId());
		List<CommunityResponse> responses = communityService.getByUserId(userId);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}
}
