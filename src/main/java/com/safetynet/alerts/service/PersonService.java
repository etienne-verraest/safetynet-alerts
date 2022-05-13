package com.safetynet.alerts.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceAlreadyExistingException;
import com.safetynet.alerts.exception.ResourceMalformedException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.mapper.PersonId;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.dto.AdultsDto;
import com.safetynet.alerts.model.dto.ChildAlertDto;
import com.safetynet.alerts.model.dto.ChildrenDto;
import com.safetynet.alerts.model.dto.PersonsInFireAlertDto;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.AgeCalculator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Service
@Slf4j
public class PersonService {

	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Get a person from database using first name and last name
	 *
	 * 
	 * @param firstName 			First name of the person
	 * @param lastName  			Last name of the person
	 * @return 						a person if first and last name matches someone, otherwise returns null
	 */
	public Person getPersonFromDatabase(String firstName, String lastName) {
		PersonId id = new PersonId(firstName, lastName);
		if (personRepository.findPersonById(id) != null) {
			log.info("[PERSON] Fetching person from database : {} {}", firstName, lastName);
			return personRepository.findPersonById(id);
		}

		return null;
	}

	/**
	 * Get every person in the database
	 * 
	 * @return 						a list of Person
	 */
	public List<Person> getPeople() {
		log.info("[PERSON] Fetching people from database");
		return personRepository.findAll();

	}

	/**
	 * Creates and populate a person in database
	 * 
	 * @param person 				A Person object
	 * @return 						A person is saved in database
	 */
	public Person createPerson(Person personEntity) {

		String firstName = personEntity.getId().getFirstName();
		String lastName = personEntity.getId().getLastName();

		if (getPersonFromDatabase(firstName, lastName) == null) {
			log.info("[PERSON] Adding person to database : {} {}", firstName, lastName);
			return personRepository.save(personEntity);
		}

		log.error("[PERSON] Person with name '{} {}' is already registered in database", firstName, lastName);
		throw new ResourceAlreadyExistingException(ExceptionMessages.PERSON_FOUND);
	}

	/**
	 * Update person fields and save it in a database
	 * 
	 * @param person 				A person object
	 * @return 						An updated person object
	 */
	public Person updatePerson(Person personEntity) {

		String firstName = personEntity.getId().getFirstName();
		String lastName = personEntity.getId().getLastName();

		Person person = getPersonFromDatabase(firstName, lastName);
		if (person != null) {

			// Avoiding deletion of allergies and medications when updating the person
			personEntity.setAllergies(person.getAllergies());
			personEntity.setMedications(person.getMedications());

			// Updating the person
			log.info("[PERSON] Updating person in database : {} {}", firstName, lastName);
			return personRepository.save(personEntity);

		}

		log.error("[PERSON] Person with name '{} {}' was not found in database", firstName, lastName);
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Delete a person from database, only if the person exists
	 * 
	 * @param firstName 			String : First name of the person
	 * @param lastName  			String : Last name of the person
	 * @return 						true if the person exists, otherwise returns false
	 */
	public void deletePerson(String firstName, String lastName) {

		Person person = getPersonFromDatabase(firstName, lastName);
		if (person != null) {
			log.info("[PERSON] Deleted '{} {}' from database", firstName, lastName);
			personRepository.delete(person);
			return;
		}

		log.error("[PERSON] Person with name '{} {}' was not found in database", firstName, lastName);
		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
	
	/**
	 * Get list of emails for a given city
	 * 
	 * @param city					String : the name of the city
	 * @return						List<String> of emails for the given city
	 */
	public List<String> getEmailsByCity(String city) {
		log.info("[COMMUNITY EMAIL] Getting emails of {} residents", city);
		return personRepository.findEmailByCity(city);
	}
	
	/**
	 * Get every person for a given address
	 * 
	 * @param address				List<String> : the desired addresses
	 * @return						List<Person> of people living at this address
	 */
	public List<Person> findPersonByAddresses(List<String> addresses) {
		
		if(!addresses.isEmpty()) {
			
			List<Person> persons = new ArrayList<Person>();
			addresses.stream().forEach(address -> {
				persons.addAll(personRepository.findAllByAddress(address));
				log.info("[PERSON] Getting people living at address : {}", address);
			});
			
			return persons;
		}
		
		log.error("[PERSON] No addresses were specified for the request");
		throw new ResourceMalformedException("There are no addresses specified for this request");
	}
	
	/**
	 * Get a list of person concerned by a fire alert. The request must return :
	 *  - Residents for the given address
	 *  - Fire station number
	 *  - Ages
	 *  - Medical record
	 * 
	 * @param address				String : the address concerned by the fire alert
	 * @return						List<PersonsInFireAlertDto> containing requested informations
	 */
	public List<PersonsInFireAlertDto> getPersonsConcernedByFireAlertAtAddress(String address) {
		
		if(address != null) {
			
			List<Person> persons = findPersonByAddresses(Arrays.asList(address));
			
			// Map list of persons to PersonByAddressDto
			List<PersonsInFireAlertDto> dto = persons.stream().map(p -> modelMapper.map(p, PersonsInFireAlertDto.class)).collect(Collectors.toList());
			
			// For each persons, we calculate their ages
			dto.forEach(p -> {
				p.setAge(AgeCalculator.calculateAge(p.getBirthdate()));
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
	 * @param address				String : the address concerned by the childAlert
	 * @return						ChildAlertDto containing requested informations
	 */
	public ChildAlertDto getChildrensAtAddress(String address) {
		
		if(address != null) {
			
			List<Person> persons = findPersonByAddresses(Arrays.asList(address));
			
			// Mapping children informations 
			List<ChildrenDto> children = persons.stream().filter(p -> AgeCalculator.calculateAge(p.getBirthdate()) <= 18)
			.map(p -> modelMapper.map(p, ChildrenDto.class)).collect(Collectors.toList());
			
			// If there are childrens we need to get a list of others people living at the address, else we can return an empty list
			if(children.size() > 0) {			
				
				// Calculating age for each children
				children.forEach(c -> c.setAge(AgeCalculator.calculateAge(c.getBirthdate())));
				
				// Mapping adults informations
				List<AdultsDto> adults = persons.stream().filter(p -> AgeCalculator.calculateAge(p.getBirthdate()) > 18)
						.map(p -> modelMapper.map(p, AdultsDto.class)).collect(Collectors.toList());
				
				ChildAlertDto childAlert = new ChildAlertDto(address, children, adults);
				log.info("[CHILD ALERT] Found {} children for address : {}", children.size(), address);
				return childAlert;
			}		
			
			log.error("[CHILD ALERT] No children were found for the specified address");
			throw new ResourceNotFoundException("No children were found for the specified address");
		}
		
		throw new ResourceMalformedException("Address for the child alert request is malformed");
	}
}
