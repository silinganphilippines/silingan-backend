package com.ria.olita.tech.silingan.entity;

import java.util.UUID;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLRestriction;

import com.ria.olita.tech.silingan.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "issues")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted = false")
@FilterDef(
	name = "communityFilter",
	parameters = @ParamDef(name = "communityId", type = UUID.class)
)
@Filter(
	name = "communityFilter",
	condition = "community_id = :communityId"
)
public class Issue extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "community_id", columnDefinition = "UUID NOT NULL")
	private Community community;

	@Column(name = "community_id", insertable = false, updatable = false)
	private UUID communityId;

	@Column(name = "title", columnDefinition = "VARCHAR(255) NOT NULL")
	private String title;

	@Column(name = "description", columnDefinition = "TEXT NOT NULL")
	private String description;

	@Column(name = "status", columnDefinition = "VARCHAR(255) NOT NULL")
	@Enumerated(EnumType.STRING)
	private IssueStatus status = IssueStatus.OPEN;

	@ManyToOne
	@JoinColumn(name = "reporter_id", columnDefinition = "UUID NOT NULL")
	private User reporter;

	@ManyToOne
	@JoinColumn(name = "category_id", columnDefinition = "UUID NOT NULL")
	private IssueCategory category;

	// Optional custom category for dynamically defined categories
	@Column(name = "custom_category", columnDefinition = "VARCHAR(255)")
	private String customCategory;

	// Optional image URL
	@Column(name = "image_url", columnDefinition = "VARCHAR(255)")
	private String imageUrl;

	// Optional location (address string)
	@Column(name = "location", columnDefinition = "VARCHAR(255)")
	private String location;

	// Optional coordinates
	@Column(name = "latitude", columnDefinition = "DOUBLE PRECISION")
	private Double latitude;

	@Column(name = "longitude", columnDefinition = "DOUBLE PRECISION")
	private Double longitude;
}
