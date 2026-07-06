package com.ria.olita.tech.silingan.security.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ria.olita.tech.silingan.entity.rbac.Action;
import com.ria.olita.tech.silingan.entity.rbac.Domain;

/**
 * Annotation for declarative permission checks.
 * Checks realm roles first, then fine-grained permissions for STAFF.
 * 
 * Usage:
 * @RequiresPermission(domain = Domain.ANNOUNCEMENT, action = Action.MANAGE)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
	Domain domain();
	Action action();
}

