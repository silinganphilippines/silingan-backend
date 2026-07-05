package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.CreateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.res.AnnouncementResponse;
import com.ria.olita.tech.silingan.entity.rbac.Action;
import com.ria.olita.tech.silingan.entity.rbac.Domain;
import com.ria.olita.tech.silingan.security.permission.RequiresPermission;
import com.ria.olita.tech.silingan.service.AnnouncementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Announcement", description = "Announcement management APIs")
public class AnnouncementController {

	private final AnnouncementService announcementService;

	@PostMapping
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.MANAGE)
	@Operation(
		summary = "Create a new announcement",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				schema = @Schema(implementation = CreateAnnouncementRequest.class),
				examples = {
					@ExampleObject(
						name = "createAnnouncementExample",
						summary = "Example announcement creation",
						value = """
							{
								"communityId": "550e8400-e29b-41d4-a716-446655440000",
								"title": "Community General Assembly",
								"content": "Please attend the general assembly this Saturday at 2PM.",
								"category": "NOTICE",
								"pinned": false
							}
							"""
					)
				}
			)
		)
	)
	public ResponseEntity<AnnouncementResponse> createAnnouncement(
		@Valid @RequestBody CreateAnnouncementRequest request) {
		AnnouncementResponse response = announcementService.createAnnouncement(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/community/{communityId}")
	@Operation(summary = "Get announcements by community")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.VIEW)
	public ResponseEntity<Page<AnnouncementResponse>> getCommunityAnnouncements(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID communityId,
		Pageable pageable
	) {
		return ResponseEntity.ok(announcementService.getCommunityAnnouncements(communityId, pageable));
	}

	@GetMapping("/community/{communityId}/active")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.VIEW)
	@Operation(summary = "Get active announcements by community")
	public ResponseEntity<Page<AnnouncementResponse>> getActiveCommunityAnnouncements(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID communityId,
		Pageable pageable
	) {
		return ResponseEntity.ok(announcementService.getActiveCommunityAnnouncements(communityId, pageable));
	}

	@GetMapping("/{id}")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.VIEW)
	@Operation(summary = "Get announcement by ID")
	public ResponseEntity<AnnouncementResponse> getAnnouncementById(		@Parameter(description = "Announcement ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id)  {
		return ResponseEntity.ok(announcementService.getAnnouncementById(id));
	}

	@PutMapping("/{id}")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.MANAGE)
	@Operation(
		summary = "Update an announcement",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				schema = @Schema(implementation = UpdateAnnouncementRequest.class),
				examples = {
					@ExampleObject(
						name = "updateAnnouncementExample",
						summary = "Example announcement update",
						value = """
							{
								"title": "Community General Assembly",
								"content": "Please attend the general assembly this Saturday at 2PM.",
								"category": "NOTICE",
								"status": "PUBLISHED",
								"pinned": false
							}
							"""
					)
				}
			)
		)
	)
	public ResponseEntity<AnnouncementResponse> updateAnnouncement(
		@Parameter(description = "Announcement ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
		@Valid @RequestBody UpdateAnnouncementRequest request
	) {
		return ResponseEntity.ok(announcementService.updateAnnouncement(id, request));
	}

	@DeleteMapping("/{id}")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.MANAGE)
	@Operation(summary = "Delete an announcement")
	public ResponseEntity<Void> deleteAnnouncement(		@Parameter(description = "Announcement ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		announcementService.deleteAnnouncement(id);
		return ResponseEntity.noContent()
			.build();
	}

	@PutMapping("/{id}/publish")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.MANAGE)
	@Operation(summary = "Publish an announcement")
	public ResponseEntity<AnnouncementResponse> publishAnnouncement(@Parameter(description = "Announcement ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		return ResponseEntity.ok(announcementService.publishAnnouncement(id));
	}

	@PutMapping("/{id}/unpublish")
	@RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.MANAGE)
	@Operation(summary = "Unpublish an announcement")
	public ResponseEntity<AnnouncementResponse> unpublishAnnouncement(		@Parameter(description = "Announcement ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		return ResponseEntity.ok(announcementService.unpublishAnnouncement(id));
	}
}
