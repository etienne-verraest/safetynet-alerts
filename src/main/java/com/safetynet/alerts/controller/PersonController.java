package com.safetynet.alerts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	PersonService personService;

	@GetMapping
	public Iterable<Person> returnPeopleJson() {
		log.info("[GET /PERSON] Fetching people from database");
		return personService.getPeopleFromDatabase();
	}
	
	@GetMapping(path = "/{firstName}/{lastName}")
	public Person findByFirstNameAndLastName(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		log.info("[GET /PERSON] Fetching person from database : {} {}", firstName, lastName);
		return personService.getPersonFromDatabase(firstName, lastName);
	}

	@PostMapping()
	public Person createPerson(@RequestBody Person person) {
		log.info("[POST /PERSON] Adding person to database : {} {}", person.getId().getFirstName(), person.getId().getLastName());
		return personService.createPerson(person);
	}
	
	@PutMapping()
	public Person updatePerson(@RequestBody Person person) {
		log.info("[PUT /PERSON] Updating person in database : {} {}", person.getId().getFirstName(), person.getId().getLastName());
		return personService.updatePerson(person);
	}
	
	@DeleteMapping(path = "/{firstName}/{lastName}")
	public void deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		if(personService.deletePerson(firstName, lastName)) {
			log.info("[DELETE /PERSON] Deleting person from database : {} {}", firstName, lastName);
		}	
	}	
}
