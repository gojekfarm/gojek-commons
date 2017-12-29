/**
 *
 */
package com.gojek.core.event;

/**
 * @author ganeshs
 *
 */
public interface Producer<E> {

	/**
	 * Send the event to the destination
	 *
	 * @param event
	 * @param destination
	 */
	void send(E event, Destination destination);
}
