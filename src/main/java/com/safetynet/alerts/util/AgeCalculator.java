package com.safetynet.alerts.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeCalculator {

	public static Integer calculateAge(String birthdate) {
		
		// parse birthdate to the correct formatting
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate from = LocalDate.parse(birthdate, formatter);
	
		// Calculates the time elapsed between 2 dates
		Period age = Period.between(from, LocalDate.now());
		return age.getYears();
	}
	
}
