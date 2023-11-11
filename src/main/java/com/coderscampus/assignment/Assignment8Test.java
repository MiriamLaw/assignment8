package com.coderscampus.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class Assignment8Test {

	@Test
	public void testGetNumbers() {
		Assignment8 assignment8 = new Assignment8();
		List<Integer> result = assignment8.getNumbers();

		assertNotNull(result, "Result should not be null");
		assertEquals(1000, result.size(), "Result should contain 1000 numbers");
	}

	@Test
	public void testGetNumbersWithRange() {
		Assignment8 assignment8 = new Assignment8();
		List<Integer> result = assignment8.getNumbers(0, 1000);

		assertNotNull(result, "Result should not be null");
		assertEquals(1000, result.size(), "Result should contain 1000 numbers");
	}
	
	@Test
	public void testGetNumbersWithFewerThan1000Numbers() {
		Assignment8 assignment8 = new Assignment8();

		int remainingNumbers = 500;
		
		List<Integer> result = assignment8.getNumbers();
		
		assertNotNull(result, "Result should not be null");
		assertTrue(result.size() <= 1000, "result should contain fewer than or equal to 1000 numbers");
	}
	
	@Test
	public void testGetNumbersWithRangeFewerThan1000Numbers() {
		Assignment8 assignment8 = new Assignment8();

		int start = 500;
		int end = 700;
		
		List<Integer> result = assignment8.getNumbers(start, end);
		
		assertNotNull(result, "Result should not be null");
		assertEquals(end - start, result.size(), "Result should contain remaining numbers in the specified range");

	}

}
