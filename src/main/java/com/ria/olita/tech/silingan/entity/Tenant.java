package com.ria.olita.tech.silingan.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.ria.olita.tech.silingan.entity.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenants")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class Tenant extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@Column(name = "name", columnDefinition = "VARCHAR(255) NOT NULL")
	private String name;

	@Column(name = "contact_person", columnDefinition = "VARCHAR(255)")
	private String contactPerson;

	@Column(name = "contact_email", columnDefinition = "VARCHAR(255)")
	private String contactEmail;

	@Column(name = "contact_number", columnDefinition = "VARCHAR(255)")
	private String contactNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "VARCHAR(255) NOT NULL")
	@lombok.Builder.Default
	private TenantStatus status = TenantStatus.PENDING;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id", columnDefinition = "UUID")
	private Address billingAddress;


	@OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
	private List<Community> communities = new ArrayList<>();

	@Column(name = "subscription_plan")
	private String subscriptionPlan;


}
