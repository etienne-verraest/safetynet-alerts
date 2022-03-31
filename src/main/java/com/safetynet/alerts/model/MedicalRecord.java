package com.safetynet.alerts.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {

	/**
	 * "id" column is from PERSON table and "person_id" column is from
	 * MEDICAL_RECORD table In this case, we use "id" to reference the foreign key
	 * "person_id"
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id", referencedColumnName = "person_id")
	private Integer id;

	@Column(name = "birth_date")
	private Instant birthDate;

	private Map<String, Integer> medications;

	private List<String> allergies;

	/**
	 * 
	 * Getters and setters
	 * 
	 */

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Instant getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Instant birthDate) {
		this.birthDate = birthDate;
	}

	public Map<String, Integer> getMedications() {
		return medications;
	}

	public void setMedications(Map<String, Integer> medications) {
		this.medications = medications;
	}

	public List<String> getAllergies() {
		return allergies;
	}

	public void setAllergies(List<String> allergies) {
		this.allergies = allergies;
	}

}
