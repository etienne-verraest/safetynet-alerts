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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Person {

	@EmbeddedId
	private PersonId id;

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
	 * Allergy and Medication have a OneToMany relationship with a person (firstname, lastname)
	 * We need to reference both primary key columns in the two tables
	 * 
	 * CascadeType.ALL : If we delete a person, associated medications and allergies will be deleted
	 * orphanRemoval = true : If we delete a medication from a Person list, this medication is also deleted in the database
	 * 
	 */
	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			mappedBy = "person"
	)
//	@JoinColumns({
//		@JoinColumn(name= "p_firstname", referencedColumnName = "firstname"),
//		@JoinColumn(name= "p_lastname", referencedColumnName = "lastname")
//	})
	private List<Medication> medications;
	
	@OneToMany(
			cascade = CascadeType.ALL, 
			orphanRemoval = true,
			mappedBy = "person"
	)
//	@JoinColumns({
//		@JoinColumn(name= "p_firstname", referencedColumnName = "firstname"),
//		@JoinColumn(name= "p_lastname", referencedColumnName = "lastname")
//	})
	private List<Allergy> allergies;
}
