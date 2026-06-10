package com.ria.olita.tech.silingan.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_communities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCommunity {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", columnDefinition = "UUID NOT NULL")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", columnDefinition = "UUID NOT NULL")
	private Community community;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", columnDefinition = "VARCHAR(255)")
	private CommunityRole role;

	@CreationTimestamp
	@Column(name = "joined_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime joinedAt;

}
