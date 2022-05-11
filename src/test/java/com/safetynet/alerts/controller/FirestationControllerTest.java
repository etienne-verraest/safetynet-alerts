package com.safetynet.alerts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.FirestationService;

@WebMvcTest(controllers = FirestationController.class)
class FirestationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ModelMapper modelMapper;

	@MockBean
	private FirestationService firestationService;

	private static final String FIRESTATION_ADDRESS = "123 Dummy Address";
	private static final String FIRESTATION_ADDRESS_2 = "789 Dummy Address";
	
	private Firestation firestation;
	
	private List<Firestation> firestationList = new ArrayList<Firestation>();
	
	@BeforeEach
	void setup() throws Exception {
		
		String[] address = new String[] { FIRESTATION_ADDRESS, FIRESTATION_ADDRESS_2 };
		for (int i = 0; i < 2; i++) {
			Firestation firestation = new Firestation();

			// Creating a firestation with address : 123 Dummy Address and stationNumber is
			// equals to 1
			// Creating a firestation with address : 789 Dummy Address and stationNumber is
			// equals to 2
			firestation.setAddress(address[i]);
			firestation.setStationNumber(i+1);

			// Adding firestation to a list
			firestationList.add(firestation);
		}

		// This is the value of the fire station we will test unitarily
		// Address : 123 Dummy Address
		// Number : 1
		firestation = new Firestation();
		firestation.setAddress(FIRESTATION_ADDRESS);
		firestation.setStationNumber(1);
		
	}

	@Test
	void performGetAll_ShouldReturn_StatusOK() throws Exception {
		
		// ARRANGE
		when(firestationService.getAllFirestation()).thenReturn(firestationList);
		
		// ACT AND ASSERT
		mockMvc.perform(get("/firestation")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)			
			).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$[0].address").value(FIRESTATION_ADDRESS))
			.andExpect(jsonPath("$[1].address").value(FIRESTATION_ADDRESS_2));
	}

	@Test
	void performPost_ShouldReturn_StatusCreated() throws Exception {
		
		// ARRANGE
		when(firestationService.createFirestation(any(Firestation.class))).thenReturn(firestation);
		
		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(firestation);
		
		// ASSERT
		mockMvc.perform(post("/firestation")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isCreated())
			.andExpect(jsonPath("$.address").value(FIRESTATION_ADDRESS));
	}
	
	@Test
	void performPost_ShouldReturn_StatusBadRequest() throws Exception {
		
		// ARRANGE
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(null);
		
		// ACT
		mockMvc.perform(post("/firestation")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			)
		.andExpect(status().isBadRequest());
	}

	@Test
	void performPut_ShouldReturn_StatusAccepted() throws Exception {
		
		// ARRANGE
		firestation.setAddress("123 New Address");
		when(firestationService.updateFirestation(any(Firestation.class))).thenReturn(firestation);
		
		// ACT
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(firestation);
		
		// ASSERT
		mockMvc.perform(put("/firestation")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isAccepted())
			.andExpect(jsonPath("$.address").value("123 New Address"));
	}
	
	@Test
	void performPut_ShouldReturn_StatusBadRequest() throws Exception {
		
		// ARRANGE
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(null);
		
		// ACT
		mockMvc.perform(put("/firestation")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isBadRequest());
	}

	@Test
	void performDelete_ShouldReturn_StatusOK() throws Exception {
		
		// ARRANGE
		doNothing().when(firestationService).deleteFirestation(FIRESTATION_ADDRESS);
		
		// ACT
		mockMvc.perform(delete("/firestation").param("address", FIRESTATION_ADDRESS)
			).andExpect(status().isOk());
		
		// ASSERT
		verify(firestationService, times(1)).deleteFirestation(FIRESTATION_ADDRESS);
		
	}
	
	@Test
	void performDelete_ShouldReturn_StatusBadRequest() throws Exception {
		
		// ARRANGE
		String address = null;
		doThrow(ResourceMalformedException.class).when(firestationService).deleteFirestation(address);
		
		// ACT
		mockMvc.perform(delete("/firestation")
				.param("address", address)
		).andExpect(status().isBadRequest());
		
		// ASSERT
		verify(firestationService, never()).deleteFirestation(null);
		
	}
}
