package com.ria.olita.tech.silingan.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.ria.olita.tech.silingan.entity.rbac.PermissionEnum;
import com.ria.olita.tech.silingan.entity.rbac.PermissionOverrideEffect;

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
	name = "community_role_permission_overrides",
	indexes = {
		@Index(name = "idx_crpo_community_role", columnList = "community_id, staff_role_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"community_id", "staff_role_id", "permission"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityRolePermissionOverride {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	@Column(name = "community_id", insertable = false, updatable = false)
	private UUID communityId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_role_id", nullable = false)
	private StaffRole staffRole;

	@Column(name = "staff_role_id", insertable = false, updatable = false)
	private UUID staffRoleId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 100)
	private PermissionEnum permission;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 12)
	private PermissionOverrideEffect effect;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false)
	private LocalDateTime createdAt;
}
