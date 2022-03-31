package com.safetynet.alerts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;

@RestController
public class PersonController {

	@Autowired
	PersonService personService;

	@GetMapping("/person")
	public Iterable<Person> returnPeopleJson() {
		Person person = new Person();
		System.out.println();
		return personService.getPeopleFromDatabase();
	}

}
