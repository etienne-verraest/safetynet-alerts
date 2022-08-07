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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.safetynet.alerts.mapper.PersonId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table()
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Person {

	@EmbeddedId
	private PersonId id;

	@Column
	private String address;

	@Column
	private String city;

	@Column
	private String zip;

	@Column
	private String phone;

	@Column
	private String email;

	@Column
	private String birthdate;

	/**
	 * Allergy and Medication have a OneToMany relationship with a person PK(firstname, lastname)
	 * We need to reference both columns in the two tables
	 *
	 * CascadeType.ALL : If we delete a person, associated medications and allergies will be deleted
	 * orphanRemoval = true : If we delete a medication from a Person list, this medication is also deleted in the database
	 *
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
	@JsonInclude(Include.NON_EMPTY)
	private List<Medication> medications;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
	@JsonInclude(Include.NON_EMPTY)
	private List<Allergy> allergies;

}
