package com.safetynet.alerts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("person")
public class PersonController {

	@Autowired
	PersonService personService;

	@GetMapping
	public Iterable<Person> returnPeopleJson() {
		log.info("Fetching people from database...");
		return personService.getPeopleFromDatabase();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Person createPerson(@RequestBody Person person) {
		log.info("Adding {} {} to database", person.getFirstName(), person.getLastName());
		return personService.createPerson(person);
	}
}
