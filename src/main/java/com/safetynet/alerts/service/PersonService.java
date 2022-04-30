package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceAlreadyExistingException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Service
@Slf4j
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	/**
	 * Get a person from database using first name and last name
	 *
	 * 
	 * @param firstName First name of the person
	 * @param lastName  Last name of the person
	 * @return a person if first and last name matches someone, otherwise returns
	 *         null
	 */
	public Person getPersonFromDatabase(String firstName, String lastName) {		
		PersonId id = new PersonId(firstName, lastName);
		if(personRepository.findPersonById(id) != null) {
			log.info("[PERSON] Fetching person from database : {} {}", firstName, lastName);
			return personRepository.findPersonById(id);
		}		
		log.error("[PERSON] Person with name '{} {}' was not found in database", firstName, lastName);
		return null;
	}

	/**
	 * Get every person (= people) in the database
	 * 
	 * @return a list of Person
	 */
	public List<Person> getPeople() {
		log.info("[PERSON] Fetching people from database");
		return personRepository.findAll();
	}

	/**
	 * Creates and populate a person in database
	 * 
	 * @param person A Person object
	 * @return A person is saved in database
	 */
	public Person createPerson(Person personEntity) {
		
		String firstName = personEntity.getId().getFirstName();
		String lastName = personEntity.getId().getLastName();
		
		if(getPersonFromDatabase(firstName, lastName) == null) {
			log.info("[PERSON] Adding person to database : {} {}", firstName, lastName);
			return personRepository.save(personEntity);
		}
		
		log.error("[PERSON] Person with name '{} {}' is already registered in database", firstName, lastName);
		throw new ResourceAlreadyExistingException(ExceptionMessages.PERSON_FOUND);
	}

	/**
	 * Update person fields and save it in a database
	 * 
	 * @param person A person object
	 * @return An updated person object
	 */
	public Person updatePerson(Person personEntity) {
		
		String firstName = personEntity.getId().getFirstName();
		String lastName =  personEntity.getId().getLastName();
		
		Person person = getPersonFromDatabase(firstName, lastName);
		if (person != null) {
			
			// Avoiding deletion of allergies and medications when updating the person
			personEntity.setAllergies(person.getAllergies());
			personEntity.setMedications(person.getMedications());
			
			// Updating the person
			log.info("[PERSON] Updating person in database : {} {}", firstName, lastName);
			return personRepository.save(personEntity);
		
		}		
		log.error("[PERSON] Person with name '{} {}' was not found in database", firstName, lastName);
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Delete a person from database, only if the person exists
	 * 
	 * @param firstName First name of the person
	 * @param lastName  Last name of the person
	 * @return true if the person exists, otherwise returns false
	 */
	public void deletePerson(String firstName, String lastName) {
		Person person = getPersonFromDatabase(firstName, lastName);
		if (person != null) {	
			log.info("[PERSON] Deleted '{} {}' from database", firstName, lastName);
			personRepository.delete(person);
			return;
		}
		log.error("[PERSON] Person with name '{} {}' was not found in database", firstName, lastName);
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
}
