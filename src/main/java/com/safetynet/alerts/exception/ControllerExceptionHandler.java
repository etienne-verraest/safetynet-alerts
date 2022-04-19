package com.safetynet.alerts.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ErrorMessage resourceNotFoundException(Exception ex, WebRequest request) {
	    
		ErrorMessage message = new ErrorMessage(
	        String.format("%s", HttpStatus.NOT_FOUND),
	        new Date(),
	        ex.getMessage());
	    
	    return message;
	}
	
	@ExceptionHandler(ResourceAlreadyExistingException.class)
	@ResponseStatus(value = HttpStatus.FOUND)
	public ErrorMessage resourceAlreadyExistingException(Exception ex, WebRequest request) {
		
		ErrorMessage message = new ErrorMessage(
		        String.format("%s", HttpStatus.FOUND),
		        new Date(),
		        ex.getMessage());
		
		return message;
	}
}
