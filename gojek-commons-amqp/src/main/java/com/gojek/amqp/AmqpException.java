/**
 *
 */
package com.gojek.amqp;

import com.gojek.core.CoreException;

/**
 * @author ganeshs
 *
 */
public class AmqpException extends CoreException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public AmqpException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AmqpException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AmqpException(Throwable cause) {
		super(cause);
	}

}
