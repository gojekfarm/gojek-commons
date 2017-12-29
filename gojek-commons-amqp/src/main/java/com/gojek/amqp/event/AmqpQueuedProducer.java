/**
 *
 */
package com.gojek.amqp.event;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.gojek.amqp.AmqpConnection;
import com.gojek.amqp.AmqpException;
import com.gojek.core.event.Destination;
import com.gojek.core.event.QueuedProducer;
import com.gojek.util.serializer.Serializer;

/**
 * QueuedProducer implementation using AMQP connection
 *
 * @author ganeshs
 */
@Singleton
public class AmqpQueuedProducer<E> extends QueuedProducer<E> {
	
	private AmqpConnection connection;
	
	/**
	 * @param connection
	 */
	@Inject
	public AmqpQueuedProducer(AmqpConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public void flushInternal() {
		connection.execute((channel) -> {
			try {
				for (Entry<Destination, List<E>> entry : getQueues().entrySet()) {
					Destination destination = entry.getKey();
					for (E event : entry.getValue()) {
						String value = Serializer.DEFAULT_JSON_SERIALIZER.serialize(event);
						channel.basicPublish(destination.getExchange(), destination.getRoutingKey(), null, value.getBytes());
					}
				}
			} catch (Exception e) {
				throw new AmqpException("Failed while flushing the messages", e);
			}
			return null;
		});
	}
}
