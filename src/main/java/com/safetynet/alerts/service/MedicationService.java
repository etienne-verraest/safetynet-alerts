package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.exception.ExceptionMessages;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Medication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MedicationService {

	@Autowired
	MedicationRepository medicationRepository;

	/**
	 * Get every medications for a given person
	 * 
	 * @param personEntity			A Person Object
	 * @return						List<Medication> a list of medications
	 */
	public List<Medication> getAllPersonMedications(Person personEntity) {

		if (personEntity != null) {
			log.info("[MEDICATIONS] Getting medications of {} {}", personEntity.getId().getFirstName(),
					personEntity.getId().getLastName());
			return medicationRepository.findAllByPerson(personEntity);
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Get a single medication for a given person
	 * 
	 * @param personEntity			A Person Object
	 * @param medication			String : name and posology of the medication
	 * @return						The Medication if it exists
	 */	
	public Medication getPersonMedication(Person personEntity, String medication) {

		if (personEntity != null) {
			return medicationRepository.findByPersonAndNamePosology(personEntity, medication);
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}
	
	/**
	 * Delete a single medication for a person
	 * 
	 * @param personEntity 			A Person Object (must be entity)
	 * @param namePosology 			A String with the desired namePosology
	 */
	public void deletePersonMedication(Person personEntity, String namePosology) {

		if (personEntity != null) {
			
			String firstName = personEntity.getId().getFirstName();
			String lastName = personEntity.getId().getLastName();

			if (getPersonMedication(personEntity, namePosology) != null) {

				medicationRepository.deleteByPersonAndNamePosology(personEntity, namePosology);
				log.info("[MEDICATION] Deleted medication '{}' for {} {}", namePosology, firstName, lastName);
				return;
			}

			log.error("[MEDICATION] Medication '{}' was not found for {} {}", namePosology, firstName, lastName);
			throw new ResourceNotFoundException(ExceptionMessages.MEDICATION_NOT_FOUND);
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

	/**
	 * Save person medications
	 * 
	 * @param personEntity			A Person Object
	 * @param medications			A List<Medication> that we want to save for a person
	 * @return						A List<Medication> containing the new medications
	 */
	public List<Medication> savePersonMedications(Person personEntity, List<Medication> medications) {

		if (personEntity != null) {

			// Processing the new medications
			medications.stream()
					.filter(medication -> getPersonMedication(personEntity, medication.getNamePosology()) == null)
					.forEach(medication -> {
						medication.setPerson(personEntity);
						medicationRepository.save(medication);
						log.info("[MEDICATIONS] Adding medication '{}' to {} {}", medication.getNamePosology(),
								personEntity.getId().getFirstName(), personEntity.getId().getLastName());
					});
			
			return medications;
		}

		throw new ResourceNotFoundException(ExceptionMessages.PERSON_NOT_FOUND);
	}

}
