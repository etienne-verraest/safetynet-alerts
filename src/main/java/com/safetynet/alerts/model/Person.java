/**
 * Modeling of Person
 * 
 * Primary key is a compound key (firstname, lastname), thus we need to do the mapping in a new
 * class called PersonId.
 * 
 * This unique identifier is used to perform CRUD operations (POST, PUT, DELETE)
 * 
 * For reference : https://attacomsian.com/blog/spring-data-jpa-composite-primary-key
 * 
 */

package com.safetynet.alerts.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.safetynet.alerts.model.mapper.PersonId;

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

	@Column(name = "uid")
	private String uid;

	/**
	 * Allergy and Medication have a OneToMany relationship with a person (firstname, lastname)
	 * We need to reference both primary key columns in our two tables
	 * 
	 * CascadeType.ALL : If we delete a person, associated medications and allergies will be deleted
	 * orphanRemoval = true : If we delete a medication from a Person list, this medication is also deleted in the database
	 * FetchType.LAZY : We load medications and allergies on demand, because it is a part of MedicalRecord, and not from a person
	 * 
	 */
	@OneToMany(
			cascade = CascadeType.ALL, 
			orphanRemoval = true, 
			fetch = FetchType.LAZY
	)
	@JoinColumns({
		@JoinColumn(name= "p_firstname", referencedColumnName = "firstname"),
		@JoinColumn(name= "p_lastname", referencedColumnName = "lastname")
	})
	private List<Medication> medications;
	
	@OneToMany(
			cascade = CascadeType.ALL, 
			orphanRemoval = true, 
			fetch = FetchType.LAZY
	)
	@JoinColumns({
		@JoinColumn(name= "p_firstname", referencedColumnName = "firstname"),
		@JoinColumn(name= "p_lastname", referencedColumnName = "lastname")
	})
	private List<Allergy> allergies;
}
