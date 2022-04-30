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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonDto;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PersonController {

	@Autowired
	PersonService personService;

	@Autowired
	FirestationService firestationService;

	@Autowired
	ModelMapper modelMapper;

	@GetMapping(path = "/person")
	public List<Person> returnGetPeopleJson() {
		return personService.getPeople();
	}

	@GetMapping(path = "/personInfo")
	public ResponseEntity<Person> findByFirstNameAndLastName(@RequestParam String firstName,
			@RequestParam String lastName) {
		
		// Checking if the parameters are non-null or not malformed
		if(firstName.trim().isEmpty() == false & lastName.trim().isEmpty() == false) {		
			Person person = personService.getPersonFromDatabase(firstName, lastName);		
			return new ResponseEntity<Person>(person, HttpStatus.FOUND);
		}
		
		// Logging the error
		log.error("[GET /PERSON] Request to get person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}

	@PostMapping(path = "/person")
	public ResponseEntity<Person> createPerson(@RequestBody PersonDto personDto) {
		
		// Checking if the request is non-null or not malformed
		if (personDto != null) {
			
			// Mapping DTO -> Entity
			Person personRequestBody = modelMapper.map(personDto, Person.class);
			
			// Returning the result
			Person person = personService.createPerson(personRequestBody);
			return new ResponseEntity<Person>(person, HttpStatus.CREATED);
		}
		
		log.error("[POST /PERSON] Request to create person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}


	@PutMapping("/person")
	public ResponseEntity<Person> updatePerson(@RequestBody PersonDto personDto) {
		
		// Checking if the request is non-null or not malformed
		if (personDto != null) {		
			
			// Mapping DTO -> Entity
			Person personRequestBody = modelMapper.map(personDto, Person.class);		
			
			// Returning the result
			Person person = personService.updatePerson(personRequestBody);	
			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}
		
		// Logging the error
		log.error("[PUT /PERSON] Request to update person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}

	@DeleteMapping(path = "/person/{firstName}/{lastName}")
	public ResponseEntity<String> deletePerson(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		// Checking if the request is not malformed
		if (firstName != null && lastName != null) {
			
			// Returning the result
			personService.deletePerson(firstName, lastName);
			return new ResponseEntity<String>(firstName + " " + lastName + " was succesfully deleted", HttpStatus.OK);
		}

		// Logging the error
		log.error("[DELETE /PERSON] Request to delete person is malformed");
		throw new ResourceMalformedException(ExceptionMessages.PERSON_MALFORMED_REQUEST);
	}
}
