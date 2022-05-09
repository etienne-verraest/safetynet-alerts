package com.safetynet.alerts.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.PersonService;

@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ModelMapper modelMapper;

	@MockBean
	private PersonService personService;

	@MockBean
	private FirestationService firestationService;

	private Person person;

	private List<Person> listOfPerson = new ArrayList<Person>();

	@BeforeEach
	void setup() {

		String[] names = new String[] { "Alpha", "Bravo" };
		for (int i = 0; i < 2; i++) {
			Person person = new Person();

			// Person ID
			PersonId personId = new PersonId(names[i], "Dummy");
			person.setId(personId);

			// Personal informations
			person.setAddress("123 Dummy Address");
			person.setCity("Liverpool");
			person.setZip("000000");
			person.setPhone("0102030405");
			person.setEmail("dummy-name@gmail.com");
			person.setBirthdate("16/02/1998");

			// Allergies and medications
			List<Allergy> allergies = new ArrayList<Allergy>();
			List<Medication> medications = new ArrayList<Medication>();
			person.setAllergies(allergies);
			person.setMedications(medications);

			listOfPerson.add(person);
		}

		// The unique Person we are going to test
		person = new Person();

		// Person ID
		PersonId personId = new PersonId("Alpha", "Dummy");
		person.setId(personId);

		// Personal informations
		person.setAddress("123 Dummy Address");
		person.setCity("Liverpool");
		person.setZip("000000");
		person.setPhone("0102030405");
		person.setEmail("dummy-name@gmail.com");
		person.setBirthdate("16/02/1998");

		// Allergies and medications
		List<Allergy> allergies = new ArrayList<Allergy>();
		List<Medication> medications = new ArrayList<Medication>();
		person.setAllergies(allergies);
		person.setMedications(medications);
	}

	@Test
	void performGetAll_ShouldReturn_StatusOK() throws Exception {

		// ARRANGE
		when(personService.getPeople()).thenReturn(listOfPerson);

		// ACT AND ASSERT
		mockMvc.perform(get("/person").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id.firstName").value("Alpha"))
				.andExpect(jsonPath("$[1].id.firstName").value("Bravo"));
	}

	@Test
	void performUnitGet_ShouldReturn_StatusFound() throws Exception {

		// ARRANGE
		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);

		// ACT AND ASSERT
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();

		mockMvc.perform(get(String.format("/personInfo?firstName=%s&lastName=%s", firstName, lastName))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isFound())
				.andExpect(jsonPath("$.id.firstName").value(firstName))
				.andExpect(jsonPath("$.id.lastName").value(lastName));
	}

	@Test
	void performUnitGet_ShouldReturn_StatusNotFound() throws Exception {

		// ARRANGE
		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(null);

		// ASSERT
		mockMvc.perform(get(String.format("/personInfo?firstName=%s&lastName=%s", "Zulu", "Foxtrot"))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	void performPost_ShouldReturn_StatusCreated() throws Exception {

		// ARRANGE
		when(personService.createPerson(any(Person.class))).thenReturn(person);

		// ACT
		// Convert our person to a Json String
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(person);

		// ASSERT
		mockMvc.perform(MockMvcRequestBuilders.post("/person").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

	}
	
	@Disabled
	@Test
	void performPost_ShouldReturn_BadRequest() throws Exception {
		
		// ARRANGE
		when(personService.createPerson(null)).thenReturn(null);

		// ACT and ASSERT
		assertThrows(ResourceMalformedException.class, () -> mockMvc.perform(MockMvcRequestBuilders.post("/person")));
	
	}

}
