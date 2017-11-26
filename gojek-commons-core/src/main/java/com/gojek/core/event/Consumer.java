/**
 *
 */
package com.gojek.core.event;

/**
 * @author ganeshs
 *
 */
public interface Consumer {
	
	/**
	 * @author ganeshs
	 *
	 */
	public enum Status {
		/**
		 * Acks the message
		 */
		success,
		
		/**
		 * Nacks and re-queues the message
		 */
		soft_failure,
		
		/**
		 * Nacks and moves to dead-letter queue
		 */
		hard_failure
	}
	
	/**
	 * Starts the consumer. Consumer will start receiving events only after this is invoked 
	 */
	void start();
	
	/**
	 * Stop the consumer. No more events after this is invoked
	 */
	void stop();
	
	/**
	 * Receive and handle the event
	 *
	 * @param event
	 * @return
	 */
	Status receive(Event event);
	
}
