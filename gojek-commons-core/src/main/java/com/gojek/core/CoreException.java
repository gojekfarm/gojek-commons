/**
 *
 */
package com.gojek.core;

/**
 * @author ganeshs
 *
 */
public class CoreException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public CoreException() {
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CoreException(String message, Throwable cause,
						boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CoreException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CoreException(Throwable cause) {
		super(cause);
	}

}
