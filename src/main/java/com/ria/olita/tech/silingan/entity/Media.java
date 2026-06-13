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
import jakarta.persistence.FetchType;
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
@Table(name = "media")
@Builder
@EqualsAndHashCode(callSuper = false)
@Data
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
public class Media extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	@Column(name = "community_id", insertable = false, updatable = false)
	private UUID communityId;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "stored_name", nullable = false)
	private String storedName;

	@Column(name = "content_type", nullable = false)
	private String contentType;

	@Column(name = "url", nullable = false)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(name = "media_type", nullable = false)
	private MediaType mediaType;

	@Column(name = "reference_type",nullable = false)
	@Enumerated(EnumType.STRING)
	private MediaReference referenceType;

	@Column(name = "reference_id",nullable = false)
	private UUID referenceId;

}

