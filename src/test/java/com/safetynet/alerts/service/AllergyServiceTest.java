package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.AllergyRepository;

@ExtendWith(MockitoExtension.class)
class AllergyServiceTest {

	@InjectMocks
	private AllergyService allergyService;
	
	@Mock
	private AllergyRepository allergyRepository;
	
	private List<Allergy> allergies = new ArrayList<Allergy>();
	
	private Person person;
	
	@BeforeEach
	void setup() throws Exception {
		
		// Creating allergies for a given person
		person = new Person();
		PersonId personId = new PersonId("Alpha", "Dummy");
		person.setId(personId);

		// Creating a list of allergies
		String[] allergyNames = new String[] { "Peanuts", "Ketchup" };
		for(int i = 0; i < 2 ; i++) {
			Allergy allergy = new Allergy();
			
			allergy.setId(i);
			allergy.setName(allergyNames[i]);
			allergy.setPerson(person);
			allergies.add(allergy);
		}
		
		person.setAllergies(allergies);
	}

	@Test
	void testGetAllPersonAllergies_ShouldReturn_ListOfAllergies() {
		
		// ARRANGE
		when(allergyRepository.findAllByPerson(any(Person.class))).thenReturn(allergies);
		
		// ACT
		List<Allergy> response = allergyService.getAllPersonAllergies(person);
		
		// ASSERT
		assertThat(response.size()).isEqualTo(2);
	}

	@Test
	void testGetPersonAllergy_ShouldReturn_AnAllergy() {
		
		// ARRANGE
		// We are returning the first allergy : Peanuts
		when(allergyRepository.findByPersonAndName(any(Person.class), anyString())).thenReturn(allergies.get(0));
	
		// ACT
		Allergy response = allergyService.getPersonAllergy(person, "Peanuts");
		
		// ASSERT
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("Peanuts");
	}
	
	@Test
	void testGetPersonAllergy_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> allergyService.getPersonAllergy(null, "Peanuts"));
	}

	@Test
	void testDeletePersonAllergy_ShouldReturn_Void() {
		// ARRANGE
		when(allergyRepository.findByPersonAndName(any(Person.class), anyString())).thenReturn(allergies.get(0));
		doNothing().when(allergyRepository).deleteByPersonAndName(any(Person.class), anyString());
		
		// ACT
		allergyService.deletePersonAllergy(person, "Peanuts");
		
		// ASSERT
		verify(allergyRepository, times(1)).deleteByPersonAndName(person, "Peanuts");
	}
	
	@Test
	void testDeletePersonAllergy_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> allergyService.deletePersonAllergy(null, null));
	}
	
	@Test
	void testDeletePersonAllergy_ShouldReturn_AllergyNotFound() {
		// ARRANGE
		when(allergyRepository.findByPersonAndName(any(Person.class), anyString())).thenReturn(null);
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> allergyService.deletePersonAllergy(person, "Unknown Allergy"));

	}

	@Test
	void testSavePersonAllergies_ShouldReturn_ListOfAllergies() {
		// ARRANGE
		when(allergyRepository.findByPersonAndName(any(Person.class), anyString())).thenReturn(null);
		
		// ACT
		List<Allergy> response = allergyService.savePersonAllergies(person, allergies);
		
		// ASSERT
		assertThat(response).isNotNull();
	}
	
	@Test
	void testSavePersonAllergies_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> allergyService.savePersonAllergies(null, allergies));
		
	}

}
