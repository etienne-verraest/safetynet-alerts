package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
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

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;

@ExtendWith(MockitoExtension.class)
class FirestationServiceTest {

	@InjectMocks
	FirestationService firestationService;

	@Mock
	FirestationRepository firestationRepository;

	private static final String FIRESTATION_ADDRESS = "123 Dummy Address";
	private static final String FIRESTATION_ADDRESS_2 = "789 Dummy Address";

	private List<Firestation> listOfFirestation = new ArrayList<Firestation>();

	private Firestation firestation;

	@BeforeEach
	void setup() throws Exception {

		String[] address = new String[] { FIRESTATION_ADDRESS, FIRESTATION_ADDRESS_2 };
		for (int i = 0; i < 2; i++) {
			Firestation firestations = new Firestation();

			// Creating a firestation with address : 123 Dummy Address and stationNumber is
			// equals to 1
			// Creating a firestation with address : 789 Dummy Address and stationNumber is
			// equals to 2
			firestations.setAddress(address[i]);
			firestations.setStationNumber(i+1);

			// Adding firestation to a list
			listOfFirestation.add(firestations);
		}

		// This is the value of the fire station we will test
		// Address : 123 Dummy Address
		// Number : 1
		firestation = new Firestation();
		firestation.setAddress(FIRESTATION_ADDRESS);
		firestation.setStationNumber(1);
	}

	@Test
	void testFindFirestationByAddress_ShouldReturn_DummyFirestation() {
		// ACT
		when(firestationRepository.findByAddress(anyString())).thenReturn(firestation);

		// ARRANGE
		Firestation response = firestationService.findFirestationByAddress("123 Dummy Address");

		// ASSERT
		verify(firestationRepository, times(1)).findByAddress(FIRESTATION_ADDRESS);
		assertThat(response.getAddress()).isEqualTo(FIRESTATION_ADDRESS);
	}

	@Test
	void testGetAllFirestation_ShouldReturn_TwoFirestations() {
		// ACT
		when(firestationRepository.findAll()).thenReturn(listOfFirestation);
		
		// ARRANGE
		List<Firestation> response = firestationService.getAllFirestation();
		
		// ASSERT
		assertThat(response.get(1).getAddress()).isEqualTo(FIRESTATION_ADDRESS_2);
		assertThat(response.size()).isEqualTo(2);
	}
	
	@Test
	void testCreateFirestation_ShouldReturn_NewFirestation() {
		// ARRANGE
		when(firestationRepository.save(any(Firestation.class))).thenReturn(firestation);

		// ACT
		Firestation response = firestationService.createFirestation(firestation);

		// ASSERT
		assertNotNull(response);
		assertThat(response.getAddress()).isEqualTo(FIRESTATION_ADDRESS);
	}
	
	@Test
	void testUpdateFirestation_ShouldReturn_UpdatedFirestation() {
		// ARRANGE
		firestation.setAddress("123 New Address");
		when(firestationRepository.save(any(Firestation.class))).thenReturn(firestation);

		// ACT
		Firestation response = firestationService.updateFirestation(firestation);

		// ASSERT
		assertNotNull(response);
		assertThat(response.getAddress()).isEqualTo("123 New Address");
	}
	
	@Test
	void testUpdateFirestation_ShouldReturn_Null() {
		// ARRANGE
		lenient().when(firestationRepository.save(any(Firestation.class))).thenReturn(null);
		
		// ACT
		Firestation response = firestationService.updateFirestation(null);
		
		// ASSERT
		assertNull(response);	
	}
	
	/*
	 * @Disabled
	 * 
	 * @Test void testDeleteFirestation_VerifyThat_MethodIsCalled() { // ARRANGE
	 * doNothing().when(firestationRepository).delete(firestation);
	 * 
	 * // ACT firestationService.deleteFirestation(firestation);
	 * 
	 * // ASSERT verify(firestationRepository, times(1)).delete(firestation); }
	 */

}