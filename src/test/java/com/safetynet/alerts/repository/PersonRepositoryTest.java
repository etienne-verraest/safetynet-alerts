package com.safetynet.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
public class PersonRepositoryTest {

	@Mock
	PersonRepository personRepository;

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
	void testFindPersonById_ShouldReturn_ExistingPerson() {
		
		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(person);
		
		Person response = personRepository.findPersonById(person.getId());
		assertThat(response).isNotNull();
		assertThat(response.getId().getFirstName()).isEqualTo("Emma");
		
	}
}
