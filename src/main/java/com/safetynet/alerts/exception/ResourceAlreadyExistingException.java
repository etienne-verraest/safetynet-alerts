package com.safetynet.alerts.exception;

public class ResourceAlreadyExistingException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ResourceAlreadyExistingException(String message) {
		super(message);
	}

}
