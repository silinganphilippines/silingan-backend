package com.ria.olita.tech.silingan.dto.res;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ApiResponse<T>(
	boolean success,
	String message,
	T data,
	Instant timestamp
) {
	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.message("Success")
			.data(data)
			.timestamp(Instant.now())
			.build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.message(message)
			.data(data)
			.timestamp(Instant.now())
			.build();
	}

	public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder()
			.success(false)
			.message(message)
			.data(null)
			.timestamp(Instant.now())
			.build();
	}
}
