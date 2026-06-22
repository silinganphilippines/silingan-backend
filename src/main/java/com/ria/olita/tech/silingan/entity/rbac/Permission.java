package com.ria.olita.tech.silingan.entity.rbac;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Entity
@Table(
	name = "permissions",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_permission_name", columnNames = "name")
	}
)
public class Permission {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@Getter
	@Column(nullable = false, unique = true)
	@Pattern(
		regexp = "^[a-z]+:[a-z]+$",
		message = "Name must be in the format 'domain:action' (lowercase letters only)")
	private String name;


	public Domain getDomain() {
		String[] parts = name.split(":");
		return Domain.valueOf(parts[0].toUpperCase());
	}

	public Action getAction() {
		String[] parts = name.split(":");
		return Action.valueOf(parts[1].toUpperCase());
	}
}

