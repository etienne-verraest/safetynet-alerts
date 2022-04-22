package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	
	private Firestation firestation;
	
	@BeforeEach
	void setup() throws Exception {
		
		firestation = new Firestation();
		
		// Creating a firestation with address : 123 Dummy Address and stationNumber equals to 1
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

}
