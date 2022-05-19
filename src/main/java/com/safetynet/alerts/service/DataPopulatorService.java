package com.safetynet.alerts.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Person;

@Service
public class DataPopulatorService {

	@Autowired
	PersonService personService;
	
	@Autowired
	FirestationService firestationService;
	
	@EventListener(ApplicationReadyEvent.class)
	public void loadDatas() throws Exception {
    	
		// Loading and reading the datas .json file
    	ObjectMapper objectMapper = new ObjectMapper();
    	File json = new File("src/main/resources/datas.json");
    	JsonNode node = objectMapper.readTree(json);
    	
    	// Loading persons node
    	List<Person> persons = new ArrayList<>();
    	node.get("persons").forEach(p -> { 
    		PersonId id = new PersonId(p.get("firstName").asText(), p.get("lastName").asText());
    		
    		String address = p.get("address").asText();
    		String city = p.get("city").asText();
    		String zip = p.get("zip").asText();
    		String phone = p.get("phone").asText();
    		String email = p.get("email").asText();
    		String birthdate = p.get("birthdate").asText();
    		
    		Person person = new Person(id, address, city, zip, phone, email, birthdate, null, null);
    		persons.add(person);
    	});
    	persons.forEach(p -> personService.createPerson(p));
    	
    	// Loading firestations node
    	List<Firestation> firestations = new ArrayList<>();
    	node.get("firestations").forEach(f -> {
    		
    		String address = f.get("address").asText();
    		String stationNumber = f.get("station").asText();
    		
    		Firestation firestation = new Firestation(address, Integer.valueOf(stationNumber));
    		firestations.add(firestation);
    	});
    	firestations.forEach(f -> firestationService.createFirestation(f));
	}
}
