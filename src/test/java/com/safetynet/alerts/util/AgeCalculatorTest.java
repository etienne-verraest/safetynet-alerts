package com.safetynet.alerts.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AgeCalculatorTest {

	@Test
	void testCalculateAge_ShouldReturn_Over18() {
		
		// ARRANGE
		String birthdate = "16/02/1998";
		
		// ACT
		Integer age = AgeCalculator.calculateAge(birthdate);
		
		// ASSERT
		assertThat(age).isGreaterThan(18);
	}

}
