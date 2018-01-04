/**
 *
 */
package com.gojek.kafka;

import com.gojek.core.CoreException;

/**
 * @author ganeshs
 *
 */
public class KafkaException extends CoreException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public KafkaException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public KafkaException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public KafkaException(Throwable cause) {
		super(cause);
	}

}
