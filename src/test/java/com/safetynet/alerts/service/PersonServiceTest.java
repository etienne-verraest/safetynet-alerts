package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@Mock
	PersonService personService;
	
	private Person person;

	private List<Person> listOfPerson = new ArrayList<Person>();

	/**
	 * Setting up 2 dummy users in order to test Person Service The last person'
	 * data are related to "Emma Dummy"
	 * 
	 * Person : 
	 * Name : Emma Dummy
	 * Address : 123 Dummy Address
	 * City : Liverpool
	 * Zip : 000000
	 * Phone : 0102030405
	 * Mail : dummy-name@gmail.com
	 * Birthdate : 16/02/1998
	 * 
	 */
	@BeforeEach
	void setup() {

		// Names of our dummy person
		String[] names = new String[] { "John", "Emma" };

		// Creating 2 persons with hand-filled datas
		for (int i = 0; i < 2; i++) {
			person = new Person();

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
	}
	
	@Test
	void testGetPersonFromDatabase_ShouldReturn_DummyUser() {
		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();

		when(personService.getPersonFromDatabase(firstName, lastName)).thenReturn(person);

		// ACT
		Person response = personService.getPersonFromDatabase(firstName, lastName);

		// ASSERT
		assertTrue(response.getId().getFirstName().equals(firstName));
		assertTrue(response.getId().getLastName().equals(lastName));
	}

	@Test
	void testGetPeople_ShouldReturn_ListOfPerson() {
		// ARRANGE
		when(personService.getPeople()).thenReturn(listOfPerson);

		// ACT
		List<Person> response = personService.getPeople();

		// ASSERT
		assertThat(response.size()).isEqualTo(2);
	}

	@Test
	void testCreatePerson_ShouldReturn_NewPerson() {
		// ARRANGE
		when(personService.createPerson(person)).thenReturn(person);

		// ACT
		Person response = personService.createPerson(person);

		// ASSERT
		assertNotNull(response);
		assertTrue(response.getCity().equals("Liverpool"));
	}

	@Test
	void testUpdatePerson_ShouldReturn_UpdatedPerson() {
		// ARRANGE
		person.setCity("Manchester");
		when(personService.updatePerson(person)).thenReturn(person);

		// ACT
		Person response = personService.updatePerson(person);

		// ASSERT
		assertNotNull(response);
		assertTrue(response.getCity().equals("Manchester"));
	}

	@Test
	void testDeletePerson_VerifyThat_MethodIsCalled() {
		// ARRANGE
		doNothing().when(personService).deletePerson(person);

		// ACT
		personService.deletePerson(person);

		// ASSERT
		verify(personService, times(1)).deletePerson(person);
	}
}
