package com.gojek.amqp.event;

import java.util.Map;

import com.gojek.core.event.Consumer.Status;

/**
 * @author ganeshs
 *
 */
public interface EventHandler<E> {
	
	/**
	 * Handle the event
	 *
	 * @param event
	 * @param routingKey
	 * @return true to ack the event, false to nack the event
	 */
	Status handle(E event, String queueName, String routingKey, Map<String, Object> headers);
	
	/**
	 * Returns the event class
	 * 
	 * @return
	 */
	Class<E> getEventClass();
}