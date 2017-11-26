package com.gojek.util.retrypolicy;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * @author srinivas.iyengar
 *
 */
public class FixedRetryTest {

	private FixedRetry fixedRetry;

	@Test
	public void shouldAddParameters() {
		int sleepTime = 100;
		int retryCount = 2;
		fixedRetry = new FixedRetry(sleepTime, retryCount);
		assertEquals(fixedRetry.getSleepTime(), sleepTime);
		assertEquals(fixedRetry.getRetryCount(), retryCount);
	}
}
