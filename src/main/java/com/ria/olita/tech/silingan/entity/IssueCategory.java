package com.ria.olita.tech.silingan.entity;

import java.util.UUID;

import com.ria.olita.tech.silingan.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "issue_categories")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class IssueCategory extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@Column(name = "name", columnDefinition = "VARCHAR(255) NOT NULL")
	private String name;

	@Column(name = "description", columnDefinition = "VARCHAR(255)")
	private String description;

	// Each category belongs to a community
	@ManyToOne
	@JoinColumn(name = "community_id", columnDefinition = "UUID NOT NULL")
	private Community community;

	// Display order for the category
	@Column(name = "display_order", columnDefinition = "INTEGER")
	private Integer displayOrder;

	// Whether the category is active or archived
	@Builder.Default
	@Column(name = "is_active", columnDefinition = "BOOLEAN")
	private Boolean isActive = true;
}
