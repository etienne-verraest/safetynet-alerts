package com.safetynet.alerts.exception;

public class ResourceMalformedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ResourceMalformedException(String message) {
		super(message);
	}
	
}
