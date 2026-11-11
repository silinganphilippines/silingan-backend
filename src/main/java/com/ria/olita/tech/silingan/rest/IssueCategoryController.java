package com.ria.olita.tech.silingan.rest;

import com.ria.olita.tech.silingan.dto.req.CreateIssueCategoryRequest;
import com.ria.olita.tech.silingan.dto.res.ApiResponse;
import com.ria.olita.tech.silingan.dto.res.IssueCategoryResponse;
import com.ria.olita.tech.silingan.service.IssueCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Issue Category", description = "Issue category management APIs")
public class IssueCategoryController {

	private final IssueCategoryService issueCategoryService;

	@PostMapping
	@Operation(
		summary = "Create a new issue category",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				schema = @Schema(implementation = CreateIssueCategoryRequest.class),
				examples = {
					@ExampleObject(
						name = "createIssueCategoryExample",
						summary = "Example issue category creation",
						value = """
							{
								"name": "Plumbing",
								"description": "Issues related to water pipes and leaks",
								"communityId": "550e8400-e29b-41d4-a716-446655440000",
								"displayOrder": 1
							}
							"""
					)
				}
			)
		)
	)
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> create(
		@Valid @RequestBody CreateIssueCategoryRequest request) {
		IssueCategoryResponse response = issueCategoryService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Issue category created successfully", response));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get issue category by ID")
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> getById(
		@Parameter(description = "Issue category ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		IssueCategoryResponse response = issueCategoryService.getById(id);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping
	@Operation(summary = "Get all active issue categories")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getAll() {
		List<IssueCategoryResponse> responses = issueCategoryService.getAll();
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/all")
	@Operation(summary = "Get all issue categories including deleted")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getAllIncludingDeleted() {
		List<IssueCategoryResponse> responses = issueCategoryService.getAllIncludingDeleted();
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/community/{communityId}")
	@Operation(summary = "Get issue categories by community ID")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getByCommunityId(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID communityId) {
		List<IssueCategoryResponse> responses = issueCategoryService.getByCommunityId(communityId);
		return ResponseEntity.ok(ApiResponse.success(responses));
	}

	@GetMapping("/community/{communityId}/active")
	@Operation(summary = "Get active issue categories by community ID")
	public ResponseEntity<ApiResponse<List<IssueCategoryResponse>>> getActiveByCommunityId(
		@Parameter(description = "Community ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID communityId) {
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
	@Operation(summary = "Delete an issue category")
	public ResponseEntity<ApiResponse<Void>> delete(
		@Parameter(description = "Issue category ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		issueCategoryService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Issue category deleted successfully", null));
	}

	@PutMapping("/{id}/restore")
	@Operation(summary = "Restore a deleted issue category")
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> restore(
		@Parameter(description = "Issue category ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		IssueCategoryResponse response = issueCategoryService.restore(id);
		return ResponseEntity.ok(ApiResponse.success("Issue category restored successfully", response));
	}

	@PutMapping("/{id}/toggle-active")
	@Operation(summary = "Toggle active status of an issue category")
	public ResponseEntity<ApiResponse<IssueCategoryResponse>> toggleActive(
		@Parameter(description = "Issue category ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
		IssueCategoryResponse response = issueCategoryService.toggleActive(id);
		return ResponseEntity.ok(ApiResponse.success("Issue category active status toggled", response));
	}
}
