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

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.PersonDto;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	PersonService personService;

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
	 * @param lastName  The last name of the person
	 * @return Related informations about a given person
	 */
	@GetMapping(path = "/{firstName}/{lastName}")
	public ResponseEntity<Person> findByFirstNameAndLastName(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {
		log.info("[GET /PERSON] Fetching person from database : {} {}", firstName, lastName);

		Person person = personService.getPersonFromDatabase(firstName, lastName);

		return new ResponseEntity<Person>(person, HttpStatus.FOUND);
	}

	/**
	 * This method creates a new person in database
	 * 
	 * @param personDto {@link PersonDto.java}
	 * @return a new {@link Person.java} entity
	 */
	@PostMapping
	public ResponseEntity<Person> createPerson(@RequestBody PersonDto personDto) {
		log.info("[POST /PERSON] Adding person to database : {} {}", personDto.getId().getFirstName(),
				personDto.getId().getLastName());
		Person personRequestBody = modelMapper.map(personDto, Person.class);
		Person person = personService.createPerson(personRequestBody);
		return new ResponseEntity<Person>(person, HttpStatus.CREATED);
	}

	/**
	 * This method updates a person's information. The person must be registered in
	 * the database
	 * 
	 * @param personDto {@link PersonDto.java}
	 * @return an updated {@link Person.java} entity
	 * @throws ResourceNotFoundException if the person was not found
	 */
	@PutMapping
	public ResponseEntity<Person> updatePerson(@RequestBody PersonDto personDto) {

		Person personRequestBody = modelMapper.map(personDto, Person.class);
		Person person = personService.updatePerson(personRequestBody);

		if (person != null) {
			log.info("[PUT /PERSON] Updating person in database : {} {}", personDto.getId().getFirstName(),
					personDto.getId().getLastName());
			return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
		}

		log.error("[PUT /FIRESTATION] Person with name '{} {}' was not found in database",
				personDto.getId().getFirstName(), personDto.getId().getLastName());
		throw new ResourceNotFoundException("Person was not found");
	}

	/**
	 * This method deletes a person from the database.
	 * 
	 * @param firstName The first name of the person
	 * @param lastName  The last name of the person
	 * @return A message indicating that the person has been deleted
	 * @throws ResourceNotFoundException if the person was not found
	 */
	@DeleteMapping(path = "/{firstName}/{lastName}")
	public ResponseEntity<String> deletePerson(@PathVariable("firstName") String firstName,
			@PathVariable("lastName") String lastName) {

		if (personService.deletePerson(firstName, lastName) == true) {
			log.info("[DELETE /PERSON] Deleted '{} {}' from database", firstName, lastName);
			return new ResponseEntity<String>(firstName + " " + lastName + " was succesfully deleted", HttpStatus.OK);
		}

		throw new ResourceNotFoundException("Person was not found");
	}
}
