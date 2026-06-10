package com.ria.olita.tech.silingan.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resident")
public class ResidentController {

	@GetMapping("/profile")
	@PreAuthorize("hasRole('RESIDENT')")
	public String getResidentProfile() {
		return "Resident profile data";
	}
}
