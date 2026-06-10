package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.CreateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.req.UpdateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.res.ApiResponse;
import com.ria.olita.tech.silingan.dto.res.IssueCategoryResponse;
import com.ria.olita.tech.silingan.service.IssueCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/issue-categories")
@RequiredArgsConstructor
public class IssueCategoryController {

	private final IssueCategoryService issueCategoryService;

	@PostMapping
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> create(
		@Valid @RequestBody CreateIssueCategoryRequest request) {
		IssueCategoryResponse response = issueCategoryService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Issue category created successfully", response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> getById(@PathVariable UUID id) {
		IssueCategoryResponse response = issueCategoryService.getById(id);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getAll() {
		List<IssueCategoryResponse> responses = issueCategoryService.getAll();
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getAllIncludingDeleted() {
		List<IssueCategoryResponse> responses = issueCategoryService.getAllIncludingDeleted();
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/community/{communityId}")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getByCommunityId(
		@PathVariable UUID communityId) {
		List<IssueCategoryResponse> responses = issueCategoryService.getByCommunityId(communityId);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/community/{communityId}/active")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getActiveByCommunityId(
		@PathVariable UUID communityId) {
		List<IssueCategoryResponse> responses = issueCategoryService.getActiveByCommunityId(communityId);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

//	@PutMapping("/{id}")
//	public ResponseEntity<ApiResponse<IssueCategoryResponse>> update(
//		@PathVariable UUID id,
//		@RequestBody UpdateIssueCategoryRequest request) {
//		IssueCategoryResponse response = issueCategoryService.update(id, request);
//		return ResponseEntity.ok(ApiResponse.success("Issue category updated successfully", response));
//	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
		issueCategoryService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Issue category deleted successfully", null));
	}

	@PutMapping("/{id}/restore")
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> restore(@PathVariable UUID id) {
		IssueCategoryResponse response = issueCategoryService.restore(id);
		return ResponseEntity.ok(ApiResponse.success("Issue category restored successfully", response));
	}

	@PutMapping("/{id}/toggle-active")
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> toggleActive(@PathVariable UUID id) {
		IssueCategoryResponse response = issueCategoryService.toggleActive(id);
		return ResponseEntity.ok(ApiResponse.success("Issue category active status toggled", response));
	}
}
