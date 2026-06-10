package com.ria.olita.tech.silingan.rest;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ria.olita.tech.silingan.security.context.UserContext;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;
import com.ria.olita.tech.silingan.service.UserContextInspectorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/context")
@RequiredArgsConstructor
public class UserContextController {

	private final UserContextInspectorService inspectorService;

	@GetMapping
	public ResponseEntity<UserContext> currentUser() {
		UserContext context = Optional.ofNullable(UserContextHolder.get())
			.orElseThrow(() -> new IllegalStateException("UserContext not available"));
		return ResponseEntity.ok(context);
	}

}
