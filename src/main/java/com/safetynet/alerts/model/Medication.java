package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@Table()
public class Medication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	@Column(name = "medication_id")
	private Integer id;

	@Column(name = "name_posology")
	private String namePosology;

	@JsonBackReference
	@ManyToOne()
	@JoinColumn(name = "first_name", nullable = false)
	@JoinColumn(name = "last_name", nullable = false)
	private Person person;
}
