package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.CreateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateAnnouncementRequest;
import com.ria.olita.tech.silingan.dto.res.AnnouncementResponse;
import com.ria.olita.tech.silingan.service.AnnouncementService;

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
public class AnnouncementController {

	private final AnnouncementService announcementService;

	@PostMapping
	@PreAuthorize("hasRole('COMMUNITY_ADMIN')")
	public ResponseEntity<AnnouncementResponse> createAnnouncement(@Valid @RequestBody CreateAnnouncementRequest request) {
		AnnouncementResponse response = announcementService.createAnnouncement(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/community/{communityId}")
	public ResponseEntity<Page<AnnouncementResponse>> getCommunityAnnouncements(
		@PathVariable UUID communityId,
		Pageable pageable
	) {
		return ResponseEntity.ok(announcementService.getCommunityAnnouncements(communityId, pageable));
	}

	@GetMapping("/community/{communityId}/active")
	public ResponseEntity<Page<AnnouncementResponse>> getActiveCommunityAnnouncements(
		@PathVariable UUID communityId,
		Pageable pageable
	) {
		return ResponseEntity.ok(announcementService.getActiveCommunityAnnouncements(communityId, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable UUID id) {
		return ResponseEntity.ok(announcementService.getAnnouncementById(id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN')")
	public ResponseEntity<AnnouncementResponse> updateAnnouncement(
		@PathVariable UUID id,
		@Valid @RequestBody UpdateAnnouncementRequest request
	) {
		return ResponseEntity.ok(announcementService.updateAnnouncement(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN')")
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable UUID id) {
		announcementService.deleteAnnouncement(id);
		return ResponseEntity.noContent()
			.build();
	}

	@PutMapping("/{id}/publish")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN')")
	public ResponseEntity<AnnouncementResponse> publishAnnouncement(@PathVariable UUID id) {
		return ResponseEntity.ok(announcementService.publishAnnouncement(id));
	}

	@PutMapping("/{id}/unpublish")
	@PreAuthorize("hasRole('COMMUNITY_ADMIN')")
	public ResponseEntity<AnnouncementResponse> unpublishAnnouncement(@PathVariable UUID id) {
		return ResponseEntity.ok(announcementService.unpublishAnnouncement(id));
	}
}
