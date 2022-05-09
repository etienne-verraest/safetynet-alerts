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
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicationRepository;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

	@InjectMocks
	private MedicationService medicationService;
	
	@Mock
	private MedicationRepository medicationRepository;
	
	private List<Medication> medications = new ArrayList<Medication>();
	
	private Person person;
	
	@BeforeEach
	void setup() throws Exception {
		
		// Creating medications for a given person
		person = new Person();
		PersonId personId = new PersonId("Alpha", "Dummy");
		person.setId(personId);

		// Creating a list of medications
		String[] medicationNamesPosology = new String[] { "Doliprane:1g", "Lysopaine:1g" };
		for(int i = 0; i < 2 ; i++) {
			
			Medication medication = new Medication();			
			medication.setId(i);
			medication.setNamePosology(medicationNamesPosology[i]);
			medication.setPerson(person);
			medications.add(medication);
		}
		
		person.setMedications(medications);
	}

	@Test
	void testGetAllPersonMedications_ShouldReturn_ListOfMedications() {
		
		// ARRANGE
		when(medicationRepository.findAllByPerson(any(Person.class))).thenReturn(medications);
		
		// ACT
		List<Medication> response = medicationService.getAllPersonMedications(person);
		
		// ASSERT
		assertThat(response.size()).isEqualTo(2);
	}
	
	@Test
	void testGetAllPersonMedications_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> medicationService.getAllPersonMedications(null));
	}

	@Test
	void testGetPersonMedication_ShouldReturn_Medication() {
		
		// ARRANGE
		// We are returning the first medication : Doliprane:1g
		when(medicationRepository.findByPersonAndNamePosology(any(Person.class), anyString())).thenReturn(medications.get(0));
	
		// ACT
		Medication response = medicationService.getPersonMedication(person, "Doliprane:1g");
		
		// ASSERT
		assertThat(response).isNotNull();
		assertThat(response.getNamePosology()).isEqualTo("Doliprane:1g");
	}
	
	@Test
	void testGetPersonMedication_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> medicationService.getPersonMedication(null, "Doliprane:1g"));
	}
	
	@Test
	void testDeletePersonMedication_ShouldReturn_Void() {
		// ARRANGE
		when(medicationRepository.findByPersonAndNamePosology(any(Person.class), anyString())).thenReturn(medications.get(0));
		doNothing().when(medicationRepository).deleteByPersonAndNamePosology(any(Person.class), anyString());
		
		// ACT
		medicationService.deletePersonMedication(person, "Doliprane:1g");
		
		// ASSERT
		verify(medicationRepository, times(1)).deleteByPersonAndNamePosology(person, "Doliprane:1g");
	}
	
	@Test
	void testDeletePersonMedication_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> medicationService.deletePersonMedication(null, null));
	}
	
	@Test
	void testDeletePersonMedication_ShouldReturn_MedicationNotFound() {
		
		// ARRANGE
		when(medicationRepository.findByPersonAndNamePosology(any(Person.class), anyString())).thenReturn(null);
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> medicationService.deletePersonMedication(person, "Unknown Medication"));

	}

	@Test
	void testSavePersonMedications_ShouldReturn_ListOfMedications() {
		
		// ARRANGE
		when(medicationRepository.findByPersonAndNamePosology(any(Person.class), anyString())).thenReturn(null);
		
		// ACT
		List<Medication> response = medicationService.savePersonMedications(person, medications);
		
		// ASSERT
		assertThat(response).isNotNull();
		assertThat(response.size()).isEqualTo(2);

	}
	
	@Test
	void testSavePersonMedications_ButPersonIsNull() {
		
		// ACT and ASSERT
		assertThrows(ResourceNotFoundException.class, () -> medicationService.savePersonMedications(null, medications));
		
	}

}
