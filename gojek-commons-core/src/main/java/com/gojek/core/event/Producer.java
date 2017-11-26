/**
 *
 */
package com.gojek.core.event;

/**
 * @author ganeshs
 *
 */
public interface Producer {

	/**
	 * Send the event to the destination
	 *
	 * @param event
	 * @param destination
	 */
	void send(Event event, Destination destination);
}
