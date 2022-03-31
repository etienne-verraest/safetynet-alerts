package com.safetynet.alerts.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "medical_record")
public class MedicalRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id")
	private Integer id;

	@Column(name = "birth_date")
	private Instant birthDate;

	@Column(name = "medications")
	private String medications;

	@Column(name = "allergies")
	private String allergies;
}
