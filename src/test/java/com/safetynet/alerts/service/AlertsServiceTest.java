package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.response.ChildAlertResponse;
import com.safetynet.alerts.model.response.FireAlertResponse;
import com.safetynet.alerts.model.response.FirestationResponse;
import com.safetynet.alerts.model.response.FloodAlertResponse;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AlertsServiceTest {

	private static final String ADDRESS = "123 Dummy Address";

	@Autowired
	AlertsService alertsService;
	
	@MockBean
	FirestationService firestationService;

	@MockBean
	PersonService personService;
	
	private static Firestation firestation;
	private static List<Person> listOfPerson = new ArrayList<Person>();
	
	@BeforeAll
	static void setup() throws Exception {

		// Creating a firestation linked to our 2 users
		firestation = new Firestation();
		firestation.setAddress(ADDRESS);
		firestation.setStationNumber(1);
		
		// Preparing a list of persons with datas
		String[] names = new String[] { "Alpha", "Bravo" };
		String[] birthdates = new String[] { "16/02/1998", "01/01/2020" };
		String[] phoneNumbers = new String[] { "+33700000", "+3600000" };

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
	void testGetPhoneAlert_ShouldReturn_ListOfPhoneNumbers() {

		// ARRANGE
		List<String> addresses = new ArrayList<String>();
		addresses.add(ADDRESS);

		when(firestationService.getAddressesFromFirestationNumber(anyInt())).thenReturn(addresses);
		when(personService.findPersonByAddresses(anyList())).thenReturn(listOfPerson);
		
		// ACT
		List<String> phones = alertsService.getPhoneAlert(1);

		// ASSERT
		assertThat(phones.size()).isEqualTo(2);
		assertThat(phones.get(0)).isEqualTo("+33700000");
	}

	@Test
	void testGetFireAlert_ShouldReturn_FireAlertResponseList() {

		// ARRANGE
		when(personService.findPersonByAddresses(anyList())).thenReturn(listOfPerson);
		when(firestationService.getFirestationNumber(anyString())).thenReturn(1);

		// ACT
		List<FireAlertResponse> response = alertsService.getFireAlert(ADDRESS);

		// ASSERT
		// General assertions
		assertThat(response.size()).isEqualTo(2);
		
		// Assertions related to "Alpha Dummy"
		assertThat(response.get(0).getAge()).isGreaterThan(18);
		assertThat(response.get(0).getAllergies().get(0).getName()).isEqualTo("Peanuts");
		
		// Assertions related to "Bravo Dummy"
		assertThat(response.get(1).getStationNumber()).isEqualTo(1);
		
	}

	@Test
	void testGetChildAlert_ShouldReturn_ChildAlertResponse() {
		
		// ARRANGE
		when(personService.findPersonByAddresses(anyList())).thenReturn(listOfPerson);
		
		// ACT
		ChildAlertResponse response = alertsService.getChildAlert(ADDRESS);
		
		// ASSERT
		// General assertions
		assertThat(response.getChildrens().size()).isEqualTo(1);
		assertThat(response.getRelatives().size()).isEqualTo(1);
		
		// Child assertions
		assertThat(response.getChildrens().get(0).getAge()).isLessThanOrEqualTo(18);
		assertThat(response.getChildrens().get(0).getFirstName()).isEqualTo("Bravo");
		
		// Relatives assertions
		assertThat(response.getRelatives().get(0).getFirstName()).isEqualTo("Alpha");
	}

	@Test
	void testGetFirestationAlert_ShouldReturnFirestationResponse() {
		
		// ARRANGE
		List<String> addresses = new ArrayList<String>();
		addresses.add(ADDRESS);
		
		when(firestationService.getAddressesFromFirestationNumber(anyInt())).thenReturn(addresses);
		when(personService.findPersonByAddresses(anyList())).thenReturn(listOfPerson);

		// ACT
		FirestationResponse response = alertsService.getFirestationAlert(1);
		
		// ASSERT
		assertThat(response.getPersons().size()).isEqualTo(2);
		assertThat(response.getNumberOfAdults()).isEqualTo(1);
		assertThat(response.getNumberOfChildren()).isEqualTo(1);
	}

	@Test
	void testGetFloodAlert_ShouldReturn_FloodAlertResponse() {

		// ARRANGE
		List<Integer> numbers = new ArrayList<Integer>();
		numbers.add(1);
		
		List<String> addresses = new ArrayList<String>();
		addresses.add(ADDRESS);
		
		when(firestationService.getAddressesFromFirestationNumber(anyInt())).thenReturn(addresses);
		when(personService.findPersonByAddresses(anyList())).thenReturn(listOfPerson);
		
		// ACT
		FloodAlertResponse response = alertsService.getFloodAlert(numbers);

		// ASSERT
		assertThat(response.getAddressesServed().get(0)).isEqualTo(ADDRESS);
		assertThat(response.getPersonsByAddress().get(0).getPersons().size()).isEqualTo(2);
	}

}
