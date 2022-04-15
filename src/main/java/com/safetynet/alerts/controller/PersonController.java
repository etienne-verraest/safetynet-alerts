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
		
	@GetMapping
	public List<Person> returnGetPeopleJson() {
		log.info("[GET /PERSON] Fetching people from database");
		return personService.getPeople();
	}
	
	@GetMapping(path = "/{firstName}/{lastName}")
	public ResponseEntity<Person> findByFirstNameAndLastName(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		log.info("[GET /PERSON] Fetching person from database : {} {}", firstName, lastName);
		
		Person person = personService.getPersonFromDatabase(firstName, lastName);
				
		return new ResponseEntity<Person>(person, HttpStatus.FOUND);
	}

	@PostMapping()
	public ResponseEntity<Person> createPerson(@RequestBody PersonDto personDto) {
		log.info("[POST /PERSON] Adding person to database : {} {}", personDto.getId().getFirstName(), personDto.getId().getLastName());
		
		Person personRequest = modelMapper.map(personDto, Person.class);
		Person person = personService.createPerson(personRequest);
		
		return new ResponseEntity<Person>(person, HttpStatus.CREATED);
	}
	
	@PutMapping()
	public ResponseEntity<Person> updatePerson(@RequestBody PersonDto personDto) {
		log.info("[PUT /PERSON] Updating person in database : {} {}", personDto.getId().getFirstName(), personDto.getId().getLastName());
		
		Person personRequest = modelMapper.map(personDto, Person.class);
		Person person = personService.updatePerson(personRequest);
		
		return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping(path = "/{firstName}/{lastName}")
	public ResponseEntity<String> deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		
		boolean deletePerson = personService.deletePerson(firstName, lastName);
		if(deletePerson == true) {
			log.info("[DELETE /PERSON] Deleted '{} {}' from database", firstName, lastName);
			return new ResponseEntity<String>(firstName + " " + lastName + " was succesfully deleted", HttpStatus.OK);
		}
		return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
	}	
}
