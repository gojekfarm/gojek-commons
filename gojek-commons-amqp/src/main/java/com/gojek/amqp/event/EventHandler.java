package com.gojek.amqp.event;

import java.util.Map;

import com.gojek.core.event.Consumer.Status;
import com.gojek.core.event.Event;

/**
 * @author ganeshs
 *
 */
public interface EventHandler {
	
	/**
	 * Handle the event
	 *
	 * @param event
	 * @param routingKey
	 * @return true to ack the event, false to nack the event
	 */
	Status handle(Event event, String queueName, String routingKey, Map<String, Object> headers);
}