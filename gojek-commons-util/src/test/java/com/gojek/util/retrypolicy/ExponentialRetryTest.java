/**
 *
 */
package com.gojek.util.retrypolicy;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author gayatri.mali
 *
 */
public class ExponentialRetryTest {
	
	private ExponentialRetry exponentialRetry;
	
	@BeforeClass
	public void setup() {
		exponentialRetry = new ExponentialRetry(10000);
	}
	
	@Test
	public void getSleepTimeGivenRetryCountAndElapsedTime() {
		// TODO Auto-generated method stub
		int sleepTime = exponentialRetry.getSleepTime(1, 1000);
		assertEquals(sleepTime, 19000);
	}
	
	@Test
	public void getSleepTimeGivenRetryCount() {
		// TODO Auto-generated method stub
		int sleepTime = exponentialRetry.getSleepTime(2);
		assertEquals(sleepTime, 40000);
	}
	
	@Test
	public void getSleepTimeIfRetryCountExceedsMaxValue() {
		// TODO Auto-generated method stub
		int sleepTime = exponentialRetry.getSleepTime(15);
		assertEquals(sleepTime, 10240000);
	}
	
	@Test
	public void getSleepTimeIfCalculatedSleepTimeExceedsMaxValue() {
		// TODO Auto-generated method stub
		ExponentialRetry retry = new ExponentialRetry(2100000);
		int sleepTime = retry.getSleepTime(12);
		assertEquals(sleepTime, 2147483647);
	}
	
}
