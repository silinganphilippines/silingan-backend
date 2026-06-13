package com.ria.olita.tech.silingan.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.ria.olita.tech.silingan.entity.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
	name = "users",
	indexes = {
		@Index(name = "idx_user_keycloak_id", columnList = "keycloak_user_id")
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = "userCommunities")
@SQLRestriction("deleted = false")
public class User extends BaseEntity {

	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private UUID id;

	@Column(name = "keycloak_user_id", nullable = false, unique = true, updatable = false)
	private String keycloakUserId;

	private String username;
	private String email;
	private String firstName;
	private String lastName;

	@OneToMany(
		mappedBy = "user",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	private List<UserCommunity> userCommunities = new ArrayList<>();

	public void addCommunity(UserCommunity uc) {
		userCommunities.add(uc);
		uc.setUser(this);
	}

	public List<Community> getJoinedCommunities() {
		return userCommunities.stream()
			.map(UserCommunity::getCommunity)
			.toList();
	}
}


