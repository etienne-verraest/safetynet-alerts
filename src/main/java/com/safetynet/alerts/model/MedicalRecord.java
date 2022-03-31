package com.safetynet.alerts.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@OneToOne(mappedBy = "medicalRecord")
	private Person person;

	@Column(name = "birth_date")
	private Instant birthDate;

	/**
	 * 
	 * Getters and setters
	 * 
	 */

}
