package com.ria.olita.tech.silingan.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;


import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Address {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "UUID")
	private UUID id;

	@Column(name = "street", columnDefinition = "VARCHAR(255)")
	private String street;

	@Column(name = "barangay", columnDefinition = "VARCHAR(255)")
	private String barangay;

	@Column(name = "city", columnDefinition = "VARCHAR(255)")
	private String city;

	@Column(name = "province", columnDefinition = "VARCHAR(255)")
	private String province;

	@Column(name = "region", columnDefinition = "VARCHAR(255)")
	private String region;

	@Column(name = "postal_code", columnDefinition = "VARCHAR(255)")
	private String postalCode;

	@Column(name = "country", columnDefinition = "VARCHAR(255)")
	private String country;

	// Condominium-specific fields

	@Column(name = "building_name", columnDefinition = "VARCHAR(255)")
	private String buildingName;

	@Column(name = "tower", columnDefinition = "VARCHAR(255)")
	private String tower;

	@Column(name = "unit_number", columnDefinition = "VARCHAR(255)")
	private String unitNumber;

	@Column(name = "floor", columnDefinition = "VARCHAR(255)")
	private String floor;

	@Column(name = "latitude", columnDefinition = "DOUBLE PRECISION")
	private Double latitude;

	@Column(name = "longitude", columnDefinition = "DOUBLE PRECISION")
	private Double longitude;
}
