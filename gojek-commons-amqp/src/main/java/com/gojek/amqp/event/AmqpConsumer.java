/**
 *
 */
package com.gojek.amqp.event;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.amqp.AmqpException;
import com.gojek.core.event.Consumer;
import com.gojek.core.event.Event;
import com.gojek.util.serializer.Serializer;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author ganeshs
 *
 */
public class AmqpConsumer extends DefaultConsumer implements Consumer {
	
	private EventHandler handler;
	
	private String queueName;
	
	private ShutdownListener shutdownListener;
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpConsumer.class);
	
	/**
	 * @param queueName
	 * @param channel
	 * @param handler
	 * @param shutdownListener
	 */
	public AmqpConsumer(String queueName, Channel channel, EventHandler handler, ShutdownListener shutdownListener) {
		super(channel);
		this.queueName = queueName;
		this.handler = handler;
		this.shutdownListener = shutdownListener;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		Event event = null;
		try {
			event = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(new String(body), Event.class);
		} catch (Exception e) {
			logger.error("Failed while deserializing the event", e);
			getChannel().basicNack(envelope.getDeliveryTag(), false, false);
			return;
		}
		
		Status status = Status.soft_failure;
		try {
			status = receive(EventWrapper.wrap(event, envelope, properties.getHeaders()));
		} catch (Exception e) {
			logger.error("Failed while handling the event", e);
		} finally {
			switch (status) {
			case success:
				getChannel().basicAck(envelope.getDeliveryTag(), false);
				break;
			case soft_failure:
				getChannel().basicNack(envelope.getDeliveryTag(), false, true);
				break;
			case hard_failure:
			default:
				getChannel().basicNack(envelope.getDeliveryTag(), false, false);
				break;
			}
		}
	}
	
	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		String hardError = sig.isHardError() ? "connection" : "channel";
		String applInit = sig.isInitiatedByApplication() ? "application" : "broker";
		logger.error("Connectivity to MQ has failed.  It was caused by " + applInit + " at the " + hardError + " level.  Reason received " + sig.getReason());
		
		if (! sig.isInitiatedByApplication()) {
			if (this.shutdownListener != null) {
				this.shutdownListener.handleShutdown(this);
			}
		}
	}
	
	@Override
	public void start() {
		try {
			getChannel().basicConsume(queueName, false, this);
		} catch (IOException e) {
			logger.error("Failed while starting the consumer", e);
			throw new AmqpException("Failed while starting the consumer", e);
		}
	}
	
	@Override
	public void stop() {
		try {
			Channel channel = getChannel();
			if (channel != null) {
				channel.close();
			}
		} catch (Exception e) {
			logger.error("Failed while stopping the consumer", e);
		}
	}
	
	@Override
	public Status receive(Event event) {
		EventWrapper wrapper = (EventWrapper) event;
		return this.handler.handle(wrapper.event, queueName, wrapper.envelope.getRoutingKey(), wrapper.headers);
	}
	
	/**
	 * Listener for consumer shutdown by broker
	 *
	 * @author ganeshs
	 *
	 */
	public static interface ShutdownListener {
		
		/**
		 * @param consumer
		 */
		void handleShutdown(DefaultConsumer consumer);
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	private static class EventWrapper extends Event {
		
		private Event event;
		
		private Envelope envelope;
		
		private Map<String, Object> headers;
		
		/**
		 * @param event
		 */
		private EventWrapper(Event event, Envelope envelope, Map<String, Object> headers) {
			this.event = event;
			this.envelope = envelope;
			this.headers = headers;
		}
		
		/**
		 * @param event
		 * @param envelope
		 * @param headers
		 * @return
		 */
		public static EventWrapper wrap(Event event, Envelope envelope, Map<String, Object> headers) {
			return new EventWrapper(event, envelope, headers); 
		}
	}
}
