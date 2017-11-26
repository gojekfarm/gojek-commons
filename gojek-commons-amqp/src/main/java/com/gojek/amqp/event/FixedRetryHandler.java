/**
 *
 */
package com.gojek.amqp.event;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.amqp.AmqpConnection;
import com.gojek.core.event.Consumer.Status;
import com.gojek.core.event.Destination;
import com.gojek.core.event.Event;
import com.gojek.util.serializer.Serializer;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * <p>Event handler with capabilities to retry at fixed intervals. This works by publishing the failed message to a retry exchange. 
 * The retry exchange should be bound to a queue configured to expire messages with a ttl and x-dead-letter-exchange set to primary queue</p>  
 *
 * @author ganeshs
 */
public class FixedRetryHandler implements EventHandler {
	
	private AmqpConnection connection;
	
	private int maxRetries;
	
	private Destination retryDestination;
	
	public static final String HEADER_X_DEATH = "x-death";
	
	public static final String ATTR_DEATH_COUNT = "count";
	
	public static final String ATTR_QUEUE = "queue";
	
	private static final Logger logger = LoggerFactory.getLogger(FixedRetryHandler.class);

	/**
	 * @param connection
	 * @param retryDestination
	 * @param maxRetries
	 */
	public FixedRetryHandler(AmqpConnection connection, Destination retryDestination, int maxRetries) {
		this.connection = connection;
		this.retryDestination = retryDestination;
		this.maxRetries = maxRetries;
	}

	@Override
	public final Status handle(Event event, String queueName, String routingKey, Map<String, Object> headers) {
		Status status = handleInternal(event);
		if (status == Status.soft_failure) {
			return retry(event, queueName, routingKey, headers);
		}
		return status;
	}
	
	/**
	 * Retry the event
	 *
	 * @param event
	 * @param headers
	 */
	protected Status retry(Event event, String queueName, String routingKey, Map<String, Object> headers) {
		int failureCount = getFailureCount(queueName, headers);
		if (failureCount >= maxRetries) {
			try {
				handleMaxRetryExceeded(event);
			} catch (Exception e) {
				logger.error("Failed while execting the callback handler", e);
			}
			return Status.hard_failure;
		}
		String value = Serializer.DEFAULT_JSON_SERIALIZER.serialize(event);
		BasicProperties properties = new BasicProperties.Builder().headers(headers).build();
		
		try {
			return connection.execute(channel -> {
				try {
					channel.basicPublish(retryDestination.getExchange(), routingKey, properties, value.getBytes());
				} catch (Exception e) {
					logger.error("Failed while publishing the message to retry exchange. Returning hard failure", e);
					return Status.hard_failure;
				}
				return Status.success;
			});
		} catch (Exception e) {
			logger.error("Failed while retrying the event. Returning hard failure", e);
			return Status.hard_failure;
		}
	}

	/**
	 * Override this method in case if you need any actions to be performed after fixed retries
	 *
	 * @param event
	 */
	protected void handleMaxRetryExceeded(Event event) {
		logger.info("Maximum retries exceeded for event {} id {}", event.getType(), event.getEntityId());
	}
	
	@SuppressWarnings("unchecked")
	protected int getFailureCount(String queueName, Map<String, Object> headers) {
		if (headers == null || headers.get(HEADER_X_DEATH) == null) {
			return 0;
		}
		List<Map<String, Object>> deathList = (List<Map<String, Object>>) headers.get(HEADER_X_DEATH);
		return deathList.stream().mapToInt(item -> ((Long) item.get(ATTR_DEATH_COUNT)).intValue()).sum();
	}

	/**
	 * Sub-classes should override this method to handle the event
	 *
	 * @param event
	 * @return
	 */
	protected Status handleInternal(Event event) {
		return Status.success;
	}
}
