package com.ria.olita.tech.silingan.entity.base;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ria.olita.tech.silingan.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime updatedAt;

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", columnDefinition = "UUID", updatable = false)
	private User createdBy;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", columnDefinition = "UUID")
	private User updatedBy;

	@Column(name = "deleted", columnDefinition = "BOOLEAN NOT NULL")
	private Boolean deleted = false;

}
