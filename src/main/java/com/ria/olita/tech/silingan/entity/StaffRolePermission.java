package com.ria.olita.tech.silingan.entity;

import java.util.UUID;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "staff_role_permissions",
	indexes = {
		@Index(name = "idx_srp_role", columnList = "staff_role_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"staff_role_id", "permission"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffRolePermission {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_role_id", nullable = false)
	private StaffRole staffRole;

	@Column(name = "staff_role_id", insertable = false, updatable = false)
	private UUID staffRoleId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 100)
	private PermissionEnum permission;
}
