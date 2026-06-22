package com.ria.olita.tech.silingan.entity.rbac;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permissionService.check #domain, #action, #communityId)")
public @interface AuthorizeDomain {
	Domain domain();
	Action action();

}
