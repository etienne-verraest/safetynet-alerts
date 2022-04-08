package com.safetynet.alerts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

import lombok.Data;

@Data
@Service
public class PersonService {

	@Autowired
	PersonRepository personRepository;
	
	Person person;
	
	/**
	 * Get a person from database using first name and last name
	 * 
	 * @param firstName			First name of the person
	 * @param lastName			Last name of the person
	 * @return 					a person if first and last name matches someone, otherwise returns null
	 */
	public Person getPersonFromDatabase(String firstName, String lastName) {
		PersonId id = new PersonId();
		id.setFirstName(firstName);
		id.setLastName(lastName);
		return personRepository.findPersonById(id);
	}
	
	/**
	 * Get every person (= people) in the database
	 * 
	 * @return 					a list of Person
	 */
	public Iterable<Person> getPeopleFromDatabase() {
		return personRepository.findAll();
	}
	
	/**
	 * Creates and populate a person in database
	 * 
	 * @param person			A Person object
	 * @return					A person is saved in database
	 */
	public Person createPerson(Person person) {
		return personRepository.save(person);
	}
	
	/**
	 * Update person fields and save it in a database
	 * 
	 * @param person			A person object
	 * @return					An updated person object
	 */
	public Person updatePerson(Person person) {
		return personRepository.save(person);
	}
	
	/**
	 * Delete a person from database, only if the person exists
	 * 
	 * @param firstName			First name of the person
	 * @param lastName 			Last name of the person
	 * @return 					true if the person exists, otherwise returns false
	 */
	public boolean deletePerson(String firstName, String lastName) {			
		person = getPersonFromDatabase(firstName, lastName);
		
		if(person != null) {
			personRepository.delete(person);
			return true;
		}	
		return false;
	}

}
