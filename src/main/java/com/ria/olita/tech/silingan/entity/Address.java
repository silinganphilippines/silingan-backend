package com.ria.olita.tech.silingan.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
