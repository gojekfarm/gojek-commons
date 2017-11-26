package com.gojek.util.retrypolicy;

/**
 * @author srinivas.iyengar
 */
public class FixedRetry {

	private final int sleepTime;
	private final int retryCount;

	/**
	 * @param sleepTime
	 *            sleep time between retries.
	 * @param retryCount
	 *            No of retries to attempt.
	 */
	public FixedRetry(final int sleepTime, final int retryCount) {
		super();
		this.sleepTime = sleepTime;
		this.retryCount = retryCount;
	}

	/**
	 * @return the sleepTime
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return retryCount;
	}

}
