package com.safetynet.alerts.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonDto;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PersonController {
	
	@Autowired
	PersonService personService;

	@Autowired
	ModelMapper modelMapper;

	/**
	 * Get a list of every person
	 * 
	 * @return					A List<Person> containing all existing persons
	 */
	@GetMapping(path = "/person")
	public List<Person> returnGetPeopleJson() {
		return personService.getPeople();
	}

	/**
	 * Get information for a given person
	 * 
	 * @param firstName			String : The first name of the person
	 * @param lastName			String : The last name of the person
	 * @return					A Person if it exists in database
	 */
	@GetMapping(path = "/personInfo")
	public ResponseEntity<Person> findByFirstNameAndLastName(@RequestParam String firstName,
			@RequestParam String lastName) {
		
		if (!firstName.isBlank() && !lastName.isBlank()) {		
			Person person = personService.getPersonFromDatabase(firstName, lastName);		
			
			if(person != null) {
				return new ResponseEntity<Person>(person, HttpStatus.FOUND);
			}
			
			log.error("[PERSON] Person with name '{} {}' was not found in database", firstName, lastName);
			throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
		}
		
		log.error("[GET /PERSON] Request to get person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}

	/**
	 * Creates and populate a person in database
	 * 
	 * @param personDto			A Data Transfer Object class that contains informations we want to save
	 * @return					A created peron
	 */
	@PostMapping(path = "/person")
	public ResponseEntity<Person> createPerson(@RequestBody PersonDto personDto) {
		
		if (personDto != null) {
			Person personRequestBody = modelMapper.map(personDto, Person.class);
			
			Person person = personService.createPerson(personRequestBody);
			return new ResponseEntity<Person>(person, HttpStatus.CREATED);
		}
		
		log.error("[POST /PERSON] Request to create person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}


	/**
	 * Updates a person in database
	 * 
	 * @param personDto			A Data Transfer Object class that contains informations we want to save
	 * @return					An updated person
	 */
	@PutMapping("/person")
	public ResponseEntity<Person> updatePerson(@RequestBody PersonDto personDto) {
		
		if (personDto != null) {		
			Person personRequestBody = modelMapper.map(personDto, Person.class);		
			
			Person person = personService.updatePerson(personRequestBody);	
			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}
		
		log.error("[PUT /PERSON] Request to update person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}

	/**
	 * Deletes a person in database
	 * 
	 * @param firstName			String : The first name of the person
	 * @param lastName			String : The last name of the person
	 * @return					A message that indicates that the person has been deleted
	 */
	@DeleteMapping(path = "/person")
	public ResponseEntity<String> deletePerson(@RequestParam String firstName,
			@RequestParam String lastName) {

		if (!firstName.isBlank() && !lastName.isBlank()) {
			personService.deletePerson(firstName, lastName);
			return new ResponseEntity<String>(firstName + " " + lastName + " was succesfully deleted", HttpStatus.OK);
		}

		log.error("[DELETE /PERSON] Request to delete person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}
	
	/**
	 * Get mail addresses for a given city
	 * 
	 * @param city						String : The name of the city
	 * @return							List<String> of corresponding mail addresses
	 */
	@GetMapping(path = "/communityEmail")
	public ResponseEntity<List<String>> communityEmailAlert(@RequestParam String city) {

		if (!city.isBlank()) {
			List<String> response = personService.getEmailsByCity(city);
			return new ResponseEntity<List<String>>(response, HttpStatus.FOUND);
		}

		log.error("[GET /COMMUNITYEMAIL] Request to get emails by city is malformed");
		throw new ResourceMalformedException("Request to get emails is malformed");
	}
}
