package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "medication")
public class Medication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "medication_id")
	private Integer id;

	@Column(name = "name_posology")
	private String namePosology;

	@Column(name = "p_firstname")
	private String firstName;
	
	@Column(name = "p_lastname")
	private String lastName;
}
