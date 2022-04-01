package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PersonId.class)
public class Person {

	@Id
	@Column(name = "firstname")
	private String firstName;

	@Id
	@Column(name = "lastname")
	private String lastName;

	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "zip")
	private String zip;

	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;

	@Column(name = "birthdate")
	private String birthdate;

	/**
	 * "medical_record_id" column is from this table and "record_id" column is from
	 * medical_record table.
	 * 
	 * "medical_record_id" is a foreign key (this is a bidirectional relationship).
	 * 
	 */
	// @OneToOne(cascade = CascadeType.ALL)
	// @JoinColumn(name = "medical_record_id", referencedColumnName = "record_id")
	// private MedicalRecord medicalRecord;

}
