package com.safetynet.alerts.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceAlreadyExistingException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonDto;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	PersonService personService;

	@Autowired
	FirestationService firestationService;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * This method returns a list of every person registered in database
	 * 
	 * @return List<Person> List of Person
	 */
	@GetMapping
	public List<Person> returnGetPeopleJson() {
		log.info("[GET /PERSON] Fetching people from database");
		return personService.getPeople();
	}

	/**
	 * This method returns datas linked to a given person (first name + last name)
	 * 
	 * @param firstName The first name of the person
	 * 
	 * @param lastName  The last name of the person
	 * 
	 * @return Related informations about a given person
	 */
	@GetMapping(path = "/{firstName}/{lastName}")
	public ResponseEntity<Person> findByFirstNameAndLastName(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		Person person = personService.getPersonFromDatabase(firstName, lastName);

		// Fetching person data if she exists
		if (person != null) {
			log.info("[GET /PERSON] Fetching person from database : {} {}", firstName, lastName);
			return new ResponseEntity<Person>(person, HttpStatus.FOUND);
		}

		// Logging the error if the person doesn't exists
		log.error("[GET /PERSON] Person with name '{} {}' is not registered in database", firstName, lastName);
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * This method creates a new person in database
	 * 
	 * @param personDto {@link PersonDto.java}
	 * 
	 * @return a new {@link Person.java} entity
	 */
	@PostMapping
	public ResponseEntity<Person> createPerson(@RequestBody PersonDto personDto) {

		String firstName = personDto.getId().getFirstName();
		String lastName = personDto.getId().getLastName();

		// Checking if the person exists in database, if not, we add it to the database
		Person person = personService.getPersonFromDatabase(firstName, lastName);
		if (person == null) {

			// Mapping Person DTO to an Entity
			Person personRequestBody = modelMapper.map(personDto, Person.class);
			person = personService.createPerson(personRequestBody);

			// Creating a firestation if the address is not found in database
			Firestation firestation = firestationService.findFirestationByAddress(personDto.getAddress());	
			if (firestation == null) {
				firestation = new Firestation();
				firestation.setAddress(personDto.getAddress());
				firestation.setStationNumber(firestationService.getMaxFirestationNumber() + 1);
				firestationService.createFirestation(firestation);
				log.info("[FIRESTATION] Created firestation for address '{}'", personDto.getAddress());
			}

			log.info("[POST /PERSON] Adding person to database : {} {}", firstName, lastName);
			return new ResponseEntity<Person>(person, HttpStatus.CREATED);
		}

		// If the person exists we throw a "ResourceAlreadyExistingException"
		log.error("[POST /PERSON] Person with name '{} {}' is already registered in database", firstName, lastName);
		throw new ResourceAlreadyExistingException(ExceptionMessages.PERSON_FOUND);
	}

	/**
	 * This method updates a person's information. The person must be registered in
	 * the database
	 * 
	 * @param personDto {@link PersonDto.java}
	 * 
	 * @return an updated {@link Person.java} entity
	 * 
	 * @throws ResourceNotFoundException if the person was not found
	 */
	@PutMapping
	public ResponseEntity<Person> updatePerson(@RequestBody PersonDto personDto) {

		String firstName = personDto.getId().getFirstName();
		String lastName = personDto.getId().getLastName();

		// Checking if the person exists in database
		Person person = personService.getPersonFromDatabase(firstName, lastName);

		// Updating the person if she exists in database
		if (person != null) {
			
			// Mapping Person Dto to the entity
			Person personRequestBody = modelMapper.map(personDto, Person.class);
			
			// Creating a firestation if the address is not found in database
			Firestation firestation = firestationService.findFirestationByAddress(personDto.getAddress());	
			if (firestation == null) {
				firestation = new Firestation();
				firestation.setAddress(personDto.getAddress());
				firestation.setStationNumber(firestationService.getMaxFirestationNumber() + 1);
				firestationService.createFirestation(firestation);
				log.info("[FIRESTATION] Created firestation for address '{}'", personDto.getAddress());
			}
			
			// Avoiding deletion of allergies and medications when updating the person
			personRequestBody.setAllergies(person.getAllergies());
			personRequestBody.setMedications(person.getMedications());
			
			// Updating the person
			person = personService.updatePerson(personRequestBody);

			// Logging the request
			log.info("[PUT /PERSON] Updating person in database : {} {}", firstName, lastName);
			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}

		// Logging the error
		log.error("[PUT /PERSON] Person with name '{} {}' was not found in database", firstName, lastName);

		// Throwing an exception if the person doesn't not exist
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * This method deletes a person from the database.
	 * 
	 * @param firstName The first name of the person
	 * @param lastName  The last name of the person
	 * 
	 * @return A message indicating that the person has been deleted
	 * 
	 * @throws ResourceNotFoundException if the person was not found
	 */
	@DeleteMapping(path = "/{firstName}/{lastName}")
	public ResponseEntity<String> deletePerson(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		// Checking if the person exists in database
		Person person = personService.getPersonFromDatabase(firstName, lastName);

		// Deleting the person if she exists in the database
		if (person != null) {
			personService.deletePerson(person);

			// Logging the request
			log.info("[DELETE /PERSON] Deleted '{} {}' from database", firstName, lastName);
			return new ResponseEntity<String>(firstName + " " + lastName + " was succesfully deleted", HttpStatus.OK);
		}

		// Logging the error
		log.error("[DELETE /PERSON] Person with name '{} {}' was not found in database", firstName, lastName);

		// Throwing an exception if the person doesn't not exist
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
}
