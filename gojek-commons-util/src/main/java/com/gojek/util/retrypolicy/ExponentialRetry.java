/**
 *
 */
package com.gojek.util.retrypolicy;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gayatri.mali
 *
 */
public class ExponentialRetry {
	
	private static int MAX_RETRY_COUNT = 10;
	
	private static int MAX_SLEEP_TIME = Integer.MAX_VALUE;
	
	private static final Random random = new Random();
	
	private final int baseSleepTime;
	
	private static final Logger logger = LoggerFactory.getLogger(ExponentialRetry.class);

	/**
	 * @param baseSleepTime
	 */
	public ExponentialRetry(int baseSleepTime) {
		this.baseSleepTime = baseSleepTime;
	}

	private int validateMaxRetries(int maxRetryCount) {
		maxRetryCount = Math.abs(maxRetryCount);
		if(maxRetryCount > MAX_RETRY_COUNT) {
			logger.warn("Retry count was greater than max possible value : " + MAX_RETRY_COUNT + ", Assigned it to " + MAX_RETRY_COUNT);
			maxRetryCount = MAX_RETRY_COUNT;
		}
		return maxRetryCount;
	}
	
	// Get sleep time in millisecond, given retryCount and maximum possible value 
	public int getSleepTime(int retryCount, int elaspedTime) {
		retryCount = validateMaxRetries(retryCount);
		int sleepTime = baseSleepTime * Math.max(1, (1 << retryCount));
		return validateSleepTime(sleepTime - elaspedTime);
	}
	
	private int validateSleepTime(int sleepTime) {
		if(sleepTime < 0 || sleepTime > MAX_SLEEP_TIME) {
			logger.warn("Sleep time was greater than max possible value : " + MAX_SLEEP_TIME + ", Assigned it to " + MAX_SLEEP_TIME) ;
			sleepTime = MAX_SLEEP_TIME; 
		}
		return sleepTime;
	}

	// Get sleep time in millisecond, given retryCount
	public int getSleepTime(int retryCount) {
		return getSleepTime(retryCount, 0);
	}
}
