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

@RestController
@RequestMapping("person")
public class PersonController {

	@Autowired
	PersonService personService;

	@GetMapping
	public Iterable<Person> returnPeopleJson() {
		return personService.getPeopleFromDatabase();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Person createPerson(@RequestBody Person person) {
		return personService.createPerson(person);
	}
}
