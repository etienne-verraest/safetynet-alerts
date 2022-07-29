package com.safetynet.alerts.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceMalformedException;
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

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Get a person from database using first name and last name
	 *
	 * 
	 * @param firstName 			First name of the person
	 * @param lastName  			Last name of the person
	 * @return 						a person if first and last name matches someone, otherwise returns null
	 */
	public Person getPersonFromDatabase(String firstName, String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		if (personRepository.findPersonById(id) != null) {
			log.info("[PERSON] Fetching person from database : {} {}", firstName, lastName);
			return personRepository.findPersonById(id);
		}

		return null;
	}
	
	/**
	 * Updates the person with its birth date given in the medical record section
	 * 
	 * @param personToUpdate		The person to update
	 * @param birthdate				The birth date we want to add
	 */
	public void updatePersonBirthdateFromMedicalRecord(String firstName, String lastName, String birthdate) {		
		
		// Checking if the person is already registered in database
		if(checkIfPersonExists(firstName, lastName)) 
		{
			PersonId id = new PersonId(firstName, lastName);
			// We check if the birth date is set for a given person (firstName, lastName)
			// We don't use the method getPersonFromDatabase since we don't want to fetch an entire person object
			if(personRepository.findPersonById(id).getBirthdate() == null) {
				
				// If no birth date is set, then we fetch everything and update the person.
				Person person = getPersonFromDatabase(firstName, lastName);
				log.info("[PERSON] Setting birthdate to '{}' for : {} {}", birthdate, firstName, lastName);		
				person.setBirthdate(birthdate);	
				personRepository.save(person);
			}
		}
	}
	
	/**
	 * Check if a person exists in database
	 * 
	 * @param firstName 			First name of the person
	 * @param lastName  			Last name of the person
	 * @return						True if the person exists
	 */
	public boolean checkIfPersonExists(String firstName, String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		if(personRepository.findPersonById(id) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Get every person in the database
	 * 
	 * @return 						a list of Person
	 */
	public List<Person> getPeople() {
		log.info("[PERSON] Fetching people from database");
		return personRepository.findAll();

	}

	/**
	 * Creates and populate a person in database
	 * 
	 * @param person 				A Person object
	 * @return 						A person is saved in database
	 */
	public Person createPerson(Person personEntity) {

		String firstName = personEntity.getId().getFirstName();
		String lastName = personEntity.getId().getLastName();

		if (!checkIfPersonExists(firstName, lastName)) {
			log.info("[PERSON] Adding person to database : {} {}", firstName, lastName);
			return personRepository.save(personEntity);
		}
		return null;
	}
	
	/**
	 * Creates and populate a list of person in database
	 * 
	 * @param listOfPerson			A List<Person> containing informations about people
	 * @return						A saved list of person in database
	 */
	public Iterable<Person> createPersonFromList(List<Person> listOfPerson)	{	
		
		if(!listOfPerson.isEmpty()) {
			List<Person> persons = listOfPerson.stream()
			.filter(p -> !checkIfPersonExists(p.getId().getFirstName(), p.getId().getLastName()))
			.collect(Collectors.toList());
			
			persons.forEach(p -> log.info("[PERSON] Adding person to database : {} {}", p.getId().getFirstName(), p.getId().getLastName()));
			return personRepository.saveAll(persons);
		}
		return Collections.emptyList();
	}


	/**
	 * Update person fields and save it in a database
	 * 
	 * @param person 				A person object
	 * @return 						An updated person object
	 */
	public Person updatePerson(Person personEntity) {

		String firstName = personEntity.getId().getFirstName();
		String lastName = personEntity.getId().getLastName();

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
	 * @param firstName 			String : First name of the person
	 * @param lastName  			String : Last name of the person
	 * @return 						true if the person exists, otherwise returns false
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

	/**
	 * Get list of emails for a given city
	 * 
	 * @param city					String : the name of the city
	 * @return						List<String> of emails for the given city
	 */
	public List<String> getEmailsByCity(String city) {
		log.info("[COMMUNITY EMAIL] Getting emails of {} residents", city);
		return personRepository.findEmailByCity(city);
	}
	
	/**
	 * Get every person for a given address
	 * 
	 * @param address				List<String> : the desired addresses
	 * @return						List<Person> of people living at this address
	 */
	public List<Person> findPersonByAddresses(List<String> addresses) {

		if (!addresses.isEmpty()) {

			List<Person> persons = new ArrayList<>();
			addresses.stream().forEach(address -> {
				persons.addAll(personRepository.findAllByAddress(address));
				log.info("[PERSON] Getting people living at address : {}", address);
			});

			return persons;
		}

		log.error("[PERSON] No addresses were specified for the request");
		throw new ResourceMalformedException("There are no addresses specified for this request");
	}
}
