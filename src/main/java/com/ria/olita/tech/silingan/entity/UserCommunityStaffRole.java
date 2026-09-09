package com.ria.olita.tech.silingan.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	name = "user_community_staff_roles",
	indexes = {
		@Index(name = "idx_ucsr_user_community", columnList = "user_id, community_id"),
		@Index(name = "idx_ucsr_community_role", columnList = "community_id, staff_role_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "community_id"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommunityStaffRole {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "user_id", insertable = false, updatable = false)
	private UUID userId;

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

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = true;

	@Column(name = "assigned_at", columnDefinition = "TIMESTAMP", nullable = false)
	private LocalDateTime assignedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_by", columnDefinition = "UUID")
	private User assignedBy;

	@UpdateTimestamp
	@Column(name = "updated_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime updatedAt;
}
