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

@Data
@Entity
@Table(name = "allergy")
public class Allergy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	@Column(name = "allergy_id")
	private String id;

	@Column(name = "name")
	private String name;

	@JsonBackReference
    @ManyToOne()
    @JoinColumn(name = "firstName", nullable = false)
    @JoinColumn(name = "lastName", nullable = false)
    private Person person;
	
}
