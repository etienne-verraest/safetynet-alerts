package com.safetynet.alerts.exception;

public class ExceptionMessages {

	private ExceptionMessages() {}
	
	public static final String PERSON_NOT_FOUND = "This person was not found in database";
	
	public static final String FIRESTATION_NOT_FOUND = "This fire station doesn't exist";
	
	public static final String ALLERGY_NOT_FOUND = "This allergy was not found in database";
	
	public static final String MEDICATION_NOT_FOUND = "This medication was not found in database";
	
	public static final String PERSON_FOUND = "This person is already registered in database";
	
	public static final String FIRESTATION_FOUND = "This fire station is already registered in database";
	
	public static final String FIRESTATION_MALFORMED_REQUEST = "The request for this firestation is malformed";
	
	public static final String PERSON_MALFORMED_REQUEST = "The request for this person is malformed";
	
	public static final String ALLERGY_MALFORMED_REQUEST = "The request for this allergy is malformed";
	
	public static final String MEDICATION_MALFORMED_REQUEST = "The request for this medication is malformed";
	
	public static final String MEDICALRECORD_MALFORMED_REQUEST = "The request for this medical record is malformed";
}
