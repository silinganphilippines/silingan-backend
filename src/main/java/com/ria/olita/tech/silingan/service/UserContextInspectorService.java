package com.ria.olita.tech.silingan.service;

import org.springframework.stereotype.Service;

import com.ria.olita.tech.silingan.security.context.UserContext;
import com.ria.olita.tech.silingan.security.context.UserContextHolder;

@Service
public class UserContextInspectorService {

	public UserContext requireContext() {
		UserContext context = UserContextHolder.get();
		if (context == null) {
			throw new IllegalStateException("UserContext is not available on the current thread");
		}
		return context;
	}


}
