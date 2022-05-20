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
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;

@Service
public class DataPopulatorService {

	@Autowired
	PersonService personService;
	
	@Autowired
	FirestationService firestationService;
	
	@Autowired
	MedicationService medicationService;
	
	@Autowired
	AllergyService allergyService;
	
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
    	
    	// Loading medical record
    	node.get("medicalrecords").forEach(medicalrecord -> { 	
    		
    		String firstName = medicalrecord.get("firstName").asText();
    		String lastName = medicalrecord.get("lastName").asText();
    		
    		// Instead of fetching person from database, we create a new person object with an ID
    		// This way is faster since we don't have to fetch an entire person object
    		PersonId personId = new PersonId(firstName, lastName);
    		Person person = new Person();
    		person.setId(personId);
    	   		
			// Loading medications
    		List<Medication> medications = new ArrayList<>();
    		JsonNode medicationsNode = medicalrecord.withArray("medications");
    		medicationsNode.forEach(m -> { 
    			Medication medication = new Medication();
    			medication.setNamePosology(m.asText());
    			medication.setPerson(person);
    			medications.add(medication);
    		});		
    		medicationService.savePersonMedications(person, medications);

    		// Loading allergies
			List<Allergy> allergies = new ArrayList<>();
    		JsonNode allergiesNode = medicalrecord.withArray("allergies");
			allergiesNode.forEach(a -> { 
    			Allergy allergy = new Allergy();
    			allergy.setName(a.asText());
    			allergy.setPerson(person);
    			allergies.add(allergy);
    		});		
    		allergyService.savePersonAllergies(person, allergies);
    	});
	}
}
