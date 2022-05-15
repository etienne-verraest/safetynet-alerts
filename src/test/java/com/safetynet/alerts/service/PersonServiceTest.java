package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.alerts.exception.ResourceAlreadyExistingException;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

	@InjectMocks
	PersonService personService;
	
	@Mock
	PersonRepository personRepository;
	
	private static final String ADDRESS = "123 Dummy Address";
	
	private Person person;

	private List<Person> listOfPerson = new ArrayList<Person>();

	@BeforeEach
	void setup() {

		// Names of our dummy person
		String[] names = new String[] { "Alpha", "Bravo" };
		for (int i = 0; i < 2; i++) {
			Person person = new Person();

			// Person ID
			PersonId personId = new PersonId(names[i], "Dummy");
			person.setId(personId);

			// Personal informations
			person.setAddress(ADDRESS);
			person.setCity("Paris");
			person.setZip("000000");
			person.setPhone("0102030405");
			person.setEmail(names[i].toLowerCase() + "-dummy@mail.com");
			person.setBirthdate("16/02/1998");

			// Allergies and medications
			List<Allergy> allergies = new ArrayList<Allergy>();
			List<Medication> medications = new ArrayList<Medication>();
			person.setAllergies(allergies);
			person.setMedications(medications);

			listOfPerson.add(person);
		}
		
		// Value of the person we will test unitarily
		person = new Person();

		// Person ID
		PersonId personId = new PersonId("Alpha", "Dummy");
		person.setId(personId);

		// Personal informations
		person.setAddress(ADDRESS);
		person.setCity("Paris");
		person.setZip("000000");
		person.setPhone("0102030405");
		person.setEmail("alpha-dummy@mail.com");
		person.setBirthdate("16/02/1998");

		// Allergies and medications
		List<Allergy> allergies = new ArrayList<Allergy>();
		List<Medication> medications = new ArrayList<Medication>();
		person.setAllergies(allergies);
		person.setMedications(medications);
		
	}
	
	@Test
	void testGetPersonFromDatabase_ShouldReturn_DummyUser() {
		
		// ARRANGE
		String firstName = person.getId().getFirstName();
		String lastName = person.getId().getLastName();

		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(person);

		// ACT
		Person response = personService.getPersonFromDatabase(firstName, lastName);

		// ASSERT
		assertEquals(response.getId().getFirstName(), firstName);
		assertEquals(response.getId().getLastName(), lastName);
	}
	
	@Test
	void testGetPersonFromDatabase_ShouldReturn_Null() {
		
		// ARRANGE
		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(null);
		
		// ACT
		Person response = personService.getPersonFromDatabase("Zulu", "Dummy");
		
		// ASSERT
		assertNull(response);
	
	}

	@Test
	void testGetPeople_ShouldReturn_ListOfPerson() {
		
		// ARRANGE
		when(personRepository.findAll()).thenReturn(listOfPerson);

		// ACT
		List<Person> response = personService.getPeople();

		// ASSERT
		assertThat(response).hasSize(2);
	}

	@Test
	void testCreatePerson_ShouldReturn_NewPerson() {
		
		// ARRANGE
		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(null);
		when(personRepository.save(any(Person.class))).thenReturn(person);

		// ACT
		Person response = personService.createPerson(person);

		// ASSERT
		assertNotNull(response);
		assertEquals("Paris", response.getCity());
	}
	
	@Test
	void testCreatePerson_ShouldReturn_ResourceAlreadyExistingException() {
		
		// ARRANGE
		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(person);
		
		// ACT AND ASSERT
		assertThrows(ResourceAlreadyExistingException.class, () -> personService.createPerson(person));
		
	}
	
	@Test
	void testUpdatePerson_ShouldReturn_UpdatedPerson() {
		
		// ARRANGE
		person.setCity("Lille");
		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(person);
		when(personRepository.save(any(Person.class))).thenReturn(person);

		// ACT
		Person response = personService.updatePerson(person);

		// ASSERT
		assertNotNull(response);
		assertEquals("Lille", response.getCity());
	}
	
	@Test
	void testUpdatePerson_ShouldReturn_ResourceNotFoundException() {
		
		// ARRANGE
		when(personRepository.findPersonById(any(PersonId.class))).thenReturn(null);
			
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> personService.updatePerson(person));
		
	}

	 @Test 
	 void testDeletePerson_VerifyThat_MethodIsCalled() { 
		 
		 // ARRANGE
		 when(personRepository.findPersonById(any(PersonId.class))).thenReturn(person);
		 doNothing().when(personRepository).delete(person);
	 
		 // ACT 
		 personService.deletePerson("Alpha", "Dummy");
	 
		 // ASSERT 
		verify(personRepository, times(1)).delete(person); 
	}
	 
	 @Test
	 void testDeletePerson_ShouldReturn_ResourceNotFoundException() {
		 
		 // ARRANGE
		 when(personRepository.findPersonById(any(PersonId.class))).thenReturn(null);
		 
		 // ACT and ASSERT
		 assertThrows(ResourceNotFoundException.class, () -> personService.deletePerson("Zulu", "Dummy"));
		 
	 }
	 
	 @Test
	 void testGetEmailsByCity_ShouldReturn_ListOfMails() {
		 
		 // ARRANGE
		 List<String> mails = listOfPerson.stream().map(p -> p.getEmail()).collect(Collectors.toList());
		 when(personRepository.findEmailByCity(anyString())).thenReturn(mails);
		 
		 // ACT
		 List<String> response = personService.getEmailsByCity("Paris");
		 
		 // ASSERT
		 assertThat(response).hasSize(2);
	 }
	 
	 @Test
	 void testFindPersonByAddresses_ShouldReturn_ListOfPerson() {
		 
		 // ARRANGE
		 List<String> addresses = new ArrayList<String>();
		 addresses.add(ADDRESS);
		 when(personRepository.findAllByAddress(anyString())).thenReturn(listOfPerson);
		 
		 // ACT
		 List<Person> response = personService.findPersonByAddresses(addresses);
		 
		 // ASSERT
		 assertThat(response.get(0).getAddress()).isEqualTo(ADDRESS);
		 assertThat(response.get(0).getId().getFirstName()).isEqualTo("Alpha");
		 assertThat(response.get(1).getId().getFirstName()).isEqualTo("Bravo");
		 
	 }
	 
	 @Test
	 void testFindPersonByAddresses_ShouldThrow_ResourceMalformedException() {
		 
		 // ARRANGE
		 List<String> addresses = new ArrayList<String>();
		 
		 // ACT
		 Executable executable = () -> personService.findPersonByAddresses(addresses);
		 
		 // ASSERT
		 assertThrows(ResourceMalformedException.class, executable);
		 
	 }
	 
}
