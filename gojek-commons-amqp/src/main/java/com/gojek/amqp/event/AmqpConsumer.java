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
import com.gojek.core.event.EventHandler;
import com.gojek.util.serializer.Serializer;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author ganeshs
 *
 */
public class AmqpConsumer<E> extends DefaultConsumer implements Consumer<E> {
	
	private EventHandler<E> handler;
	
	private String queueName;
	
	private Consumer.ShutdownListener shutdownListener;
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpConsumer.class);
	
	/**
	 * @param queueName
	 * @param channel
	 * @param handler
	 * @param shutdownListener
	 */
	public AmqpConsumer(String queueName, Channel channel, EventHandler<E> handler, Consumer.ShutdownListener shutdownListener) {
		super(channel);
		this.queueName = queueName;
		this.handler = handler;
		this.shutdownListener = shutdownListener;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		E event = null;
		try {
			event = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(new String(body), handler.getEventClass());
		} catch (Exception e) {
			logger.error("Failed while deserializing the event", e);
			getChannel().basicNack(envelope.getDeliveryTag(), false, false);
			return;
		}
		
		Status status = Status.soft_failure;
		try {
			E proxy = createProxy(handler.getEventClass(), event, envelope, properties.getHeaders());
			status = receive(proxy);
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
	
	/**
	 * @param eventClass
	 * @param event
	 * @param envelope
	 * @param headers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected E createProxy(Class<E> eventClass, E event, Envelope envelope, Map<String, Object> headers) {
		Class<E> proxyClass = (Class<E>) new ByteBuddy().subclass(eventClass).implement(EventWrapper.class)
				.method(ElementMatchers.named("getEvent").or(ElementMatchers.named("getEnvelope")).or(ElementMatchers.named("getHeaders")))
				.intercept(MethodDelegation.to(new EventInterceptor<E>(event, envelope, headers))).make()
				.load(eventClass.getClassLoader()).getLoaded();
		try {
			return proxyClass.newInstance();
		} catch (Exception e) {
			logger.error("Failed while creating a proxy for the event", e);
			throw new AmqpException("Failed while creating a proxy for the event", e);
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
	@SuppressWarnings("unchecked")
	public Status receive(E event) {
		EventWrapper<E> wrapper = (EventWrapper<E>) event;
		return this.handler.handle(wrapper.getEvent(), queueName, wrapper.getEnvelope().getRoutingKey(), wrapper.getHeaders());
	}
	
	/**
	 * @author ganesh.s
	 *
	 * @param <E>
	 */
	public static interface EventWrapper<E> {
		
		E getEvent();
		
		Envelope getEnvelope();
		
		Map<String, Object> getHeaders();
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	public static class EventInterceptor<E> {
		
		private E event;
		
		private Envelope envelope;
		
		private Map<String, Object> headers;
		
		/**
		 * @param event
		 */
		private EventInterceptor(E event, Envelope envelope, Map<String, Object> headers) {
			this.event = event;
			this.envelope = envelope;
			this.headers = headers;
		}

		/**
		 * @return the event
		 */
		public E getEvent() {
			return event;
		}

		/**
		 * @return the envelope
		 */
		public Envelope getEnvelope() {
			return envelope;
		}

		/**
		 * @return the headers
		 */
		public Map<String, Object> getHeaders() {
			return headers;
		}
	}
}
