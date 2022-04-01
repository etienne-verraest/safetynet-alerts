package com.safetynet.alerts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "allergy")
public class Allergy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "allergy_id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "p_firstname")
	private String firstName;
	
	@Column(name = "p_lastname")
	private String lastName;
	
}