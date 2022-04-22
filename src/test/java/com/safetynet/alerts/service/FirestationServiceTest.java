package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
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
			firestations.setStationNumber(i);

			// Adding firestation to a list
			listOfFirestation.add(firestations);
		}

		// This is the value of the fire station we will test
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
	void testGetAllFirestation_ShouldReturn_OneFirestation() {
		// ACT
		when(firestationRepository.findAll()).thenReturn(listOfFirestation);
		
		// ARRANGE
		List<Firestation> response = firestationService.getAllFirestation();
		
		// ASSERT
		assertThat(response.get(1).getAddress()).isEqualTo(FIRESTATION_ADDRESS_2);
		assertThat(response.size()).isEqualTo(2);
	}

}
