package com.ria.olita.tech.silingan.entity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.ria.olita.tech.silingan.entity.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class User extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@Column(name = "keycloak_user_id", columnDefinition = "VARCHAR(255) NOT NULL UNIQUE")
	private String keycloakUserId;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserCommunity> communities = new ArrayList<>();

	public List<Community> getJoinedCommunities() {
		return this.communities.stream()
			.map(UserCommunity::getCommunity)
			.toList();
	}

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime updatedAt;

}
