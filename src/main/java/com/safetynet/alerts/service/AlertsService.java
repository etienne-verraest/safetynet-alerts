package com.safetynet.alerts.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.response.AdultsResponse;
import com.safetynet.alerts.model.response.ChildAlertResponse;
import com.safetynet.alerts.model.response.ChildrenResponse;
import com.safetynet.alerts.model.response.FireAlertResponse;
import com.safetynet.alerts.model.response.FirestationResponse;
import com.safetynet.alerts.model.response.FloodAlertGroupByAddress;
import com.safetynet.alerts.model.response.FloodAlertResponse;
import com.safetynet.alerts.model.response.PersonByFirestationResponse;
import com.safetynet.alerts.model.response.PersonFloodAlertResponse;
import com.safetynet.alerts.util.AgeCalculator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlertsService {

	@Autowired
	private PersonService personService;

	@Autowired
	private FirestationService firestationService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Get phone numbers for a given station number
	 * 
	 * @param firestationNumber				Integer : number of the station
	 * @return								List<String> containing phone numbers
	 */
	public List<String> getPhoneAlert(Integer firestationNumber) {
		// Get persons served by the firestation number
		List<String> addresses = firestationService.getAddressesFromFirestationNumber(firestationNumber);
		List<Person> persons = personService.findPersonByAddresses(addresses);
		return persons.stream().map(p -> p.getPhone()).collect(Collectors.toList());
	}

	/**
	 * Get a list of person concerned by a fire alert. The request must return :
	 *  - Residents for the given address
	 *  - Fire station number
	 *  - Ages
	 *  - Medical record
	 * 
	 * @param address						String : the address concerned by the fire alert
	 * @return								List<FireAlertResponse> containing requested informations
	 */
	public List<FireAlertResponse> getFireAlert(String address) {

		if (address != null) {

			List<Person> persons = personService.findPersonByAddresses(Arrays.asList(address));
			Integer firestationNumber = firestationService.getFirestationNumber(address);

			// Map list of persons in PersonFireAlertDto
			List<FireAlertResponse> dto = persons.stream().map(p -> modelMapper.map(p, FireAlertResponse.class))
					.collect(Collectors.toList());

			// For each persons, we calculate their ages and set their correct station
			// number +
			// We set proper first name and last name instead of personId
			dto.forEach(p -> {
				p.setAge(AgeCalculator.calculateAge(p.getBirthdate()));
				p.setStationNumber(firestationNumber);
				p.setFirstName(p.getId().getFirstName());
				p.setLastName(p.getId().getLastName());
			});

			// We return the DTO with requested informations
			log.info("[FIRE ALERT] Getting people living at address : {}", address);
			return dto;
		}

		log.error("[PERSON] Address for the fire alert request is malformed");
		throw new ResourceMalformedException("Address for the fire alert request is malformed");
	}

	/**
	 * Get a list of children at a given address. The request must return :
	 *  - Children for the given address
	 *  - Age of every children
	 *  - First name and last name of persons with age over 18 ("relatives")
	 * 
	 * @param address						String : the address concerned by the child alert
	 * @return								ChildAlertResponse containing requested informations
	 */
	public ChildAlertResponse getChildAlert(String address) {

		if (address != null) {

			List<Person> persons = personService.findPersonByAddresses(Arrays.asList(address));

			// Getting persons with age inferior or equals to 18, we then map information if
			// that's the case
			List<ChildrenResponse> children = persons.stream()
					.filter(p -> AgeCalculator.calculateAge(p.getBirthdate()) <= 18)
					.map(p -> modelMapper.map(p, ChildrenResponse.class)).collect(Collectors.toList());

			// If there are childrens we need to get a list of others people living at the
			// address
			if (children.size() > 0) {

				// Setting first name and last name instead of using personId, also calculating
				// age
				children.forEach(c -> {
					c.setAge(AgeCalculator.calculateAge(c.getBirthdate()));
					c.setFirstName(c.getId().getFirstName());
					c.setLastName(c.getId().getLastName());
				});

				// Mapping adults informations
				List<AdultsResponse> adults = persons.stream()
						.filter(p -> AgeCalculator.calculateAge(p.getBirthdate()) > 18)
						.map(p -> modelMapper.map(p, AdultsResponse.class)).collect(Collectors.toList());

				// Setting first name and last name instead of using personId
				adults.forEach(a -> {
					a.setFirstName(a.getId().getFirstName());
					a.setLastName(a.getId().getLastName());
				});

				log.info("[CHILD ALERT] Found {} children for address : {}", children.size(), address);
				return new ChildAlertResponse(address, children, adults);
			}

			log.info("[CHILD ALERT] No children were found for address : {}", address);
			throw new ResourceNotFoundException("No children were found for the specified address");
		}

		throw new ResourceMalformedException("Address for the child alert request is null");
	}

	/**
	 * This method proccesses a list of persons found for a given station number. It should return informations about :
	 * - First name and last name
	 * - Address and phone number
	 * - Number of children
	 * - Number of adults
	 * 
	 * @param stationNumber					Integer : the requested station number
	 * @return								FirestationResponse : contains datas requested
	 */
	public FirestationResponse getFirestationAlert(Integer stationNumber) {

		if (stationNumber != null && stationNumber >= 0) {

			// Getting addresses
			List<String> addresses = firestationService.getAddressesFromFirestationNumber(stationNumber);

			// Find persons living at addresses
			List<Person> personsFound = personService.findPersonByAddresses(addresses);

			// Mapping found persons
			List<PersonByFirestationResponse> persons = personsFound.stream()
					.map(p -> modelMapper.map(p, PersonByFirestationResponse.class)).collect(Collectors.toList());

			// Setting first name and last name instead of using personId
			persons.forEach(p -> {
				p.setFirstName(p.getId().getFirstName());
				p.setLastName(p.getId().getLastName());
			});

			// Counting children
			Integer childrenNumber = persons.stream().filter(p -> AgeCalculator.calculateAge(p.getBirthdate()) <= 18)
					.collect(Collectors.toList()).size();

			// Counting adults
			Integer adultsNumber = persons.size() - childrenNumber;

			log.info("[FIRESTATION] Found {} persons", persons.size());
			return new FirestationResponse(persons, childrenNumber, adultsNumber);

		}
		throw new ResourceMalformedException("The given firestation number is incorrect (null or < 0)");
	}

	/**
	 * This method proccesses flood alert for a given list of station numbers.
	 * It gets persons and then regroups people living at the same address.
	 * This endpoint must :
	 * - List of every address served by firestation numbers
	 * - Group persons living at the same address, with the following informations :
	 * 		- First name and last name
	 * 		- Phone number
	 * 		- Age
	 * 		- MedicalRecord	
	 * 
	 * 
	 * @param stationsNumbers				List<Integer> of stationsNumbers
	 * @return								FloodAlertResponse containing requested informations
	 */
	public FloodAlertResponse getFloodAlert(List<Integer> stationsNumbers) {

		if (!stationsNumbers.isEmpty()) {

			// Getting addresses
			List<String> addresses = new ArrayList<String>();
			stationsNumbers.stream().forEach(n -> {
				addresses.addAll(firestationService.getAddressesFromFirestationNumber(n));
			});

			// Find every person living at the addresses
			List<Person> personsFound = personService.findPersonByAddresses(addresses);

			// Mapping found persons to the requested response
			List<PersonFloodAlertResponse> persons = personsFound.stream()
					.map(p -> modelMapper.map(p, PersonFloodAlertResponse.class)).collect(Collectors.toList());

			// We need to calculate the age for each person (+ we also set first name and last name)
			persons.forEach(p -> {
				p.setAge(AgeCalculator.calculateAge(p.getBirthdate()));
				p.setFirstName(p.getId().getFirstName());
				p.setLastName(p.getId().getLastName());
			});

			// We then regroup every persons by their address (address, List<Person>)
			List<FloodAlertGroupByAddress> personsByAddress = new ArrayList<>();
			addresses.forEach(a -> {
				List<PersonFloodAlertResponse> personsFilteredByAddress = persons.stream().filter(p -> p.getAddress().equals(a)).collect(Collectors.toList());
				personsByAddress.add(new FloodAlertGroupByAddress(a, personsFilteredByAddress));
			});

			
			// Returning the result
			log.info("[FLOOD ALERT] Found {} persons for stations numbers {}", personsFound.size(), stationsNumbers);
			return new FloodAlertResponse(addresses, personsByAddress);
		}

		throw new ResourceMalformedException("The given firestation numbers are incorrect");
	}
}
