package com.ria.olita.tech.silingan.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ria.olita.tech.silingan.dto.req.CreateUserRequest;
import com.ria.olita.tech.silingan.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@Profile("dev")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {
		log.info("Create user request received for username: {}", request.username());
		userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
