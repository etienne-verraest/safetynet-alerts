package com.safetynet.alerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.safetynet.alerts.service.AllergyService;
import com.safetynet.alerts.service.DataPopulatorService;
import com.safetynet.alerts.service.MedicationService;
import com.safetynet.alerts.service.PersonService;

@WebMvcTest(controllers = MedicalRecordController.class)
class MedicalRecordControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	ModelMapper modelMapper;

	@MockBean
	private DataPopulatorService dataPopulatorService;

	@MockBean
	private PersonService personService;

	@MockBean
	private AllergyService allergyService;

	@MockBean
	private MedicationService medicationService;

	private Person person;

	private List<Medication> medications = new ArrayList<Medication>();

	private List<Allergy> allergies = new ArrayList<Allergy>();

	@BeforeEach
	void setup() throws Exception {

		// Person informations
		person = new Person();
		PersonId personId = new PersonId("Alpha", "Dummy");
		person.setId(personId);

		// Creating medications for our person
		String[] medicationNamesPosology = new String[] { "Doliprane:1g", "Lysopaine:1g" };
		for (int i = 0; i < 2; i++) {
			Medication medication = new Medication();
			medication.setId(i);
			medication.setNamePosology(medicationNamesPosology[i]);
			medication.setPerson(person);
			medications.add(medication);
		}

		// Creating allergies for our person
		String[] allergyNames = new String[] { "Peanuts", "Ketchup" };
		for (int i = 0; i < 2; i++) {
			Allergy allergy = new Allergy();
			allergy.setId(i);
			allergy.setName(allergyNames[i]);
			allergy.setPerson(person);
			allergies.add(allergy);
		}

		person.setMedications(medications);
		person.setAllergies(allergies);
	}

	@Test
	void testAddMedicalRecord_ShouldReturn_StatusAccepted() throws Exception {

		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();

		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);
		when(allergyService.savePersonAllergies(any(Person.class), anyList())).thenReturn(allergies);
		when(medicationService.savePersonMedications(any(Person.class), anyList())).thenReturn(medications);

		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(person);

		// ACT AND ASSERT
		mockMvc.perform(post("/medicalRecord").content(json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.id.firstName").value(firstName))
				.andExpect(jsonPath("$.id.lastName").value(lastName));
	}

	@Test
	void testGetPersonAllergies_ShouldReturn_StatusFound() throws Exception {

		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();

		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);
		when(allergyService.getAllPersonAllergies(any(Person.class))).thenReturn(allergies);

		// ACT AND ASSERT
		mockMvc.perform(get(String.format("/medicalRecord/%s/%s/allergies", firstName, lastName))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isFound())
				.andExpect(jsonPath("$[0].name").value("Peanuts"));
	}

	@Test
	void testGetPersonMedications_ShouldReturn_StatusFound() throws Exception {

		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();

		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);
		when(medicationService.getAllPersonMedications(any(Person.class))).thenReturn(medications);

		// ACT AND ASSERT
		mockMvc.perform(get(String.format("/medicalRecord/%s/%s/medications", firstName, lastName))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isFound())
				.andExpect(jsonPath("$[0].namePosology").value("Doliprane:1g"));
	}

	@Test
	void testDeletePersonAllergy_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();
		String allergy = "Ketchup";

		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);
		doNothing().when(allergyService).deletePersonAllergy(person, allergy);

		// ACT
		mockMvc.perform(delete(String.format("/medicalRecord/%s/%s/allergies/%s", firstName, lastName, allergy))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		// ASSERT
		verify(allergyService, times(1)).deletePersonAllergy(person, allergy);

	}

	@Test
	void testDeletePersonMedication_ShouldReturn_StatusOk() throws Exception {

		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();
		String medication = "Lysopaine:1g";

		when(personService.getPersonFromDatabase(anyString(), anyString())).thenReturn(person);
		doNothing().when(medicationService).deletePersonMedication(person, medication);

		// ACT
		mockMvc.perform(delete(String.format("/medicalRecord/%s/%s/medications/%s", firstName, lastName, medication))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		// ASSERT
		verify(medicationService, times(1)).deletePersonMedication(person, medication);
	}

}
