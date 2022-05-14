package com.safetynet.alerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;

@WebMvcTest(controllers = PersonController.class)
class PersonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ModelMapper modelMapper;

	@MockBean
	private PersonService personService;

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
		mockMvc.perform(get("/person")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)			
			).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].id.firstName").value("Alpha"))
			.andExpect(jsonPath("$[1].id.firstName").value("Bravo"));
	}

	@Test
	void performUnitGet_ShouldReturn_StatusFound() throws Exception {

		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();
		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);

		// ACT AND ASSERT
		mockMvc.perform(get("/personInfo")
				.param("firstName", firstName)
				.param("lastName", lastName)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			).andExpect(status().isFound())
			.andExpect(jsonPath("$.id.firstName").value(firstName))
			.andExpect(jsonPath("$.id.lastName").value(lastName));
	}

	@Test
	void performUnitGet_ShouldReturn_StatusNotFound() throws Exception {

		// ARRANGE
		String firstName = "Zulu";
		String lastName = "Foxtrot";	
		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(null);

		// ASSERT
		mockMvc.perform(get("/personInfo")
				.param("firstName", firstName)
				.param("lastName", lastName)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			).andExpect(status().isNotFound());
	}
	
	@Test
	void performUnitGet_ShouldReturn_StatusBadRequest() throws Exception {

		// ARRANGE
		String firstName = "";
		String lastName = "";	
		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(null);

		// ASSERT
		mockMvc.perform(get("/personInfo")
				.param("firstName", firstName)
				.param("lastName", lastName)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			).andExpect(status().isBadRequest());
	}

	@Test
	void performPost_ShouldReturn_StatusCreated() throws Exception {

		// ARRANGE
		when(personService.createPerson(any(Person.class))).thenReturn(person);

		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(person);

		// ASSERT
		mockMvc.perform(post("/person")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andExpect(jsonPath("$.id.firstName").value("Alpha"));
	}
	
	@Test
	void performPost_ShouldReturn_StatusBadRequest() throws Exception {

		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(null);

		// ASSERT
		mockMvc.perform(post("/person")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isBadRequest());
	}

	@Test
	void performPut_ShouldReturn_StatusAccepted() throws Exception {

		// ARRANGE
		person.setEmail("alpha@gmail.com");
		when(personService.updatePerson(any(Person.class))).thenReturn(person);

		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(person);

		// ASSERT
		mockMvc.perform(put("/person")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isAccepted())
			.andExpect(jsonPath("$.email").value("alpha@gmail.com"));
	}
	
	@Test
	void performPut_ShouldReturn_StatusBadRequest() throws Exception {

		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(null);

		// ASSERT
		mockMvc.perform(put("/person")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isBadRequest());
	}

	@Test
	void performDelete_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		String firstName = "Alpha";
		String lastName = "Dummy";
		doNothing().when(personService).deletePerson(firstName, lastName);

		// ACT
		mockMvc.perform(delete("/person")
				.param("firstName", firstName)
				.param("lastName", lastName)
			).andExpect(status().isOk());

		// ASSERT
		verify(personService, times(1)).deletePerson(firstName, lastName);
	}

	@Test
	void performDelete_ShouldReturn_StatusBadRequest() throws Exception {

		// ARRANGE
		String firstName = "";
		String lastName = "";

		// ACT
		mockMvc.perform(delete("/person")
				.param("firstName", firstName)
				.param("lastName", lastName)
			).andExpect(status().isBadRequest());

		// ASSERT
		verify(personService, never()).deletePerson(null, null);
	}

}
