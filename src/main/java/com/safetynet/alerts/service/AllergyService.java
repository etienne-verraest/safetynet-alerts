package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Allergy;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.AllergyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AllergyService {

	@Autowired
	AllergyRepository allergyRepository;

	/**
	 * Get every allergies for a given person
	 * 
	 * @param personEntity A Person Object (must be an entity)
	 * @return List<Allergy> a list of allergies
	 */
	public List<Allergy> getAllPersonAllergies(Person personEntity) {

		if (personEntity != null) {
			log.info("[ALLERGIES] Getting allergies of {} {}", personEntity.getId().getFirstName(),
					personEntity.getId().getLastName());
			return allergyRepository.findAllByPerson(personEntity);
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Get a single allergy for a person
	 * 
	 * @param personEntity A Person Object (must be entity)
	 * @param allergyName  A String with the desired allergy name
	 * @return The desired allergy, if it exists in database
	 */
	public Allergy getPersonAllergy(Person personEntity, String allergyName) {

		if (personEntity != null) {
			return allergyRepository.findByPersonAndName(personEntity, allergyName);
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Delete a single allergy for a person
	 * 
	 * @param personEntity A Person Object (must be entity)
	 * @param allergyName  A String with the desired allergy name
	 */
	public void deletePersonAllergy(Person personEntity, String allergyName) {

		if (personEntity != null) {
			String firstName = personEntity.getId().getFirstName();
			String lastName = personEntity.getId().getLastName();

			// Checking if the allergy exists in database
			if (getPersonAllergy(personEntity, allergyName) != null) {

				// Deleting the allergy
				allergyRepository.deleteByPersonAndName(personEntity, allergyName);
				log.info("[ALLERGIES] Deleted allergy '{}' for {} {}", allergyName, firstName, lastName);
				return;
			}

			log.error("[ALLERGIES] Allergy '{}' was not found for {} {}", allergyName, firstName, lastName);
			throw new ResourceNotFoundException(ExceptionMessages.ALLERGY_NOT_FOUND);
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Save person allergies
	 * 
	 * @param personEntity A Person Object (must be entity)
	 * @param allergies    A List<Allergy> we want to save to our Person
	 * @return List<Allergy> containg all saved allergies
	 */
	public List<Allergy> savePersonAllergies(Person personEntity, List<Allergy> allergies) {

		// Checking if person exists in database
		if (personEntity != null) {

			// Getting saved allergies and adding the new ones (list will be empty if
			// allergies are non-existent)
			List<Allergy> personAllergies = personEntity.getAllergies();
			personAllergies.addAll(allergies);

			// Setting person allergy
			personEntity.setAllergies(personAllergies);

			allergies.stream().forEach(allergy -> {
				log.info("[ALLERGIES] Adding allergy '{}' to {} {}", allergy.getName(),
						personEntity.getId().getFirstName(), personEntity.getId().getLastName());
			});

			// Saving the new allergies
			allergyRepository.saveAll(personAllergies);
			return personAllergies;
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
}
