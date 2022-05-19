package com.safetynet.alerts.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.response.AdultsResponse;
import com.safetynet.alerts.model.response.ChildAlertResponse;
import com.safetynet.alerts.model.response.ChildrenResponse;
import com.safetynet.alerts.model.response.FireAlertResponse;
import com.safetynet.alerts.model.response.FirestationResponse;
import com.safetynet.alerts.model.response.FloodAlertGroupByAddress;
import com.safetynet.alerts.model.response.FloodAlertResponse;
import com.safetynet.alerts.model.response.PersonByFirestationResponse;
import com.safetynet.alerts.model.response.PersonFireAlertResponse;
import com.safetynet.alerts.model.response.PersonFloodAlertResponse;
import com.safetynet.alerts.service.AlertsService;
import com.safetynet.alerts.util.AgeCalculator;

@WebMvcTest(controllers = AlertsController.class)
class AlertsControllerTest {

	private static final String ADDRESS = "123 Dummy Address";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ModelMapper modelMapper;

	@MockBean
	AlertsService alertsService;

	private static Firestation firestation;
	private static List<Person> listOfPerson = new ArrayList<Person>();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		// Creating a firestation linked to our 2 users
		firestation = new Firestation();
		firestation.setAddress(ADDRESS);
		firestation.setStationNumber(1);

		// Preparing a list of persons with datas
		String[] names = new String[] { "Alpha", "Bravo" };
		String[] birthdates = new String[] { "02/16/1998", "01/01/2020" };
		String[] phoneNumbers = new String[] { "+33700000", "+33600000" };

		for (int i = 0; i < 2; i++) {
			Person person = new Person();

			// Person ID
			PersonId personId = new PersonId(names[i], "Dummy");
			person.setId(personId);

			// Personal informations
			person.setAddress(ADDRESS);
			person.setCity("Paris");
			person.setZip("75000");
			person.setPhone(phoneNumbers[i]);
			person.setEmail(names[i].toLowerCase() + "-dummy@mail.com");
			person.setBirthdate(birthdates[i]);
			person.setMedications(null);
			person.setAllergies(null);
			listOfPerson.add(person);
		}

		// Setting up allergies for Alpha Dummy
		List<Allergy> allergies = new ArrayList<Allergy>();
		Allergy allergy = new Allergy();
		allergy.setId(1);
		allergy.setName("Peanuts");
		allergy.setPerson(listOfPerson.get(0));
		allergies.add(allergy);
		listOfPerson.get(0).setAllergies(allergies);
	}

	@Test
	void testPhoneAlert_ShouldReturn_StatusIsFound() throws Exception {

		// ARRANGE
		Integer stationNumber = 1;
		List<String> phoneNumbers = new ArrayList<>();
		phoneNumbers.add("+33700000");
		when(alertsService.getPhoneAlert(anyInt())).thenReturn(phoneNumbers);

		// ACT AND ASSERT
		mockMvc.perform(get("/phoneAlert").param("firestationNumber", stationNumber.toString())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(status().isFound())
				.andExpect(jsonPath("$[0]").value("+33700000"));
	}

	@Test
	void testFireAlert_ShouldReturn_StatusIsOk() throws Exception {

		// ARRANG
		List<PersonFireAlertResponse> persons = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			PersonFireAlertResponse response = new PersonFireAlertResponse();
			Person person = listOfPerson.get(i);
			response.setBirthdate(person.getBirthdate());
			response.setPhone(person.getPhone());
			response.setAge(AgeCalculator.calculateAge(person.getBirthdate()));
			response.setFirstName(person.getId().getFirstName());
			response.setLastName(person.getId().getLastName());
			persons.add(response);
		}
		FireAlertResponse fireAlertResponse = new FireAlertResponse(ADDRESS, 1, persons);
		when(alertsService.getFireAlert(anyString())).thenReturn(fireAlertResponse);

		// ACT AND ASSERT
		mockMvc.perform(
				get("/fire").param("address", ADDRESS).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.persons.[0].firstName").value("Alpha"))
				.andExpect(jsonPath("$.persons.[0].age").isNumber()).andExpect(jsonPath("$.persons[1].firstName").value("Bravo"));
	}

	@Test
	void testChildAlert_ShouldReturn_StatusIsOk() throws Exception {

		// ARRANGE
		// List of children
		List<ChildrenResponse> children = new ArrayList<>();
		ChildrenResponse child = new ChildrenResponse();
		Person childPerson = listOfPerson.get(1);
		child.setBirthdate(childPerson.getBirthdate());
		child.setAge(AgeCalculator.calculateAge(child.getBirthdate()));
		child.setFirstName(childPerson.getId().getFirstName());
		child.setLastName(childPerson.getId().getLastName());
		children.add(child);

		// List of relatives
		List<AdultsResponse> adults = new ArrayList<>();
		AdultsResponse adult = new AdultsResponse();
		Person adultPerson = listOfPerson.get(0);
		adult.setFirstName(adultPerson.getId().getFirstName());
		adult.setLastName(adultPerson.getId().getLastName());
		adults.add(adult);

		ChildAlertResponse response = new ChildAlertResponse();
		response.setAddress(ADDRESS);
		response.setChildrens(children);
		response.setRelatives(adults);

		when(alertsService.getChildAlert(anyString())).thenReturn(response);

		// ACT AND ASSERT
		mockMvc.perform(get("/childAlert").param("address", ADDRESS).header(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.address").value(ADDRESS))
				.andExpect(jsonPath("$.childrens").exists()).andExpect(jsonPath("$.relatives").exists());
	}

	@Test
	void testFirestationAlert_ShouldReturn_StatusIsOk() throws Exception {

		// ARRANGE
		Integer stationNumber = 1;
		FirestationResponse firestationResponse = new FirestationResponse();
		firestationResponse.setNumberOfAdults(1);
		firestationResponse.setNumberOfChildren(1);

		List<PersonByFirestationResponse> persons = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			PersonByFirestationResponse response = new PersonByFirestationResponse();
			Person person = listOfPerson.get(i);
			response.setAddress(person.getAddress());
			response.setBirthdate(person.getBirthdate());
			response.setPhone(person.getPhone());
			response.setFirstName(person.getId().getFirstName());
			response.setLastName(person.getId().getLastName());
			persons.add(response);
		}
		firestationResponse.setPersons(persons);
		when(alertsService.getFirestationAlert(anyInt())).thenReturn(firestationResponse);

		// ACT AND ASSERT
		mockMvc.perform(get("/firestation").param("stationNumber", stationNumber.toString())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.persons").exists())
				.andExpect(jsonPath("$.numberOfChildren").value(1)).andExpect(jsonPath("$.numberOfAdults").value(1))
				.andExpect(status().isOk());

	}

	@Test
	void testFloodAlert_ShouldReturn_StatusIsOk() throws Exception {

		// ARRANGE
		FloodAlertResponse floodResponse = new FloodAlertResponse();

		// Addresses served
		List<String> addresses = new ArrayList<>();
		addresses.add(ADDRESS);

		// Group persons by address
		List<FloodAlertGroupByAddress> groupByAddressList = new ArrayList<>();

		// Setting address
		FloodAlertGroupByAddress firstAddress = new FloodAlertGroupByAddress();
		firstAddress.setAddress(ADDRESS);

		// Setting list of person living at the address
		List<PersonFloodAlertResponse> persons = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			PersonFloodAlertResponse response = new PersonFloodAlertResponse();
			Person person = listOfPerson.get(i);
			response.setAddress(person.getAddress());
			response.setBirthdate(person.getBirthdate());
			response.setPhone(person.getPhone());
			response.setFirstName(person.getId().getFirstName());
			response.setLastName(person.getId().getLastName());
			response.setAge(AgeCalculator.calculateAge(response.getBirthdate()));
			persons.add(response);
		}
		firstAddress.setPersons(persons);
		groupByAddressList.add(firstAddress);

		// Setting response
		floodResponse.setAddressesServed(addresses);
		floodResponse.setPersonsByAddress(groupByAddressList);

		when(alertsService.getFloodAlert(anyList())).thenReturn(floodResponse);

		// ACT AND ASSERT
		List<Integer> stationsNumber = new ArrayList<>();
		stationsNumber.add(1);

		mockMvc.perform(get("/flood/stations").param("stations", stationsNumber.get(0).toString())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.addressesServed.[0]").value(ADDRESS))
				.andExpect(jsonPath("$.personsByAddress").exists()).andExpect(status().isOk());

	}

}
