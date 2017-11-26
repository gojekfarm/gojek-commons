/**
 *
 */
package com.gojek.amqp.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.core.event.Consumer.Status;
import com.gojek.core.event.Event;
import com.gojek.util.serializer.Serializer;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author ganeshs
 *
 */
public class AmqpConsumerTest {
	
	private AmqpConsumer consumer;
	
	private Channel channel;
	
	private EventHandler handler;
	
	private Envelope envelope;
	
	private Event event;
	
	private String queueName = "test_queue";
	
	private BasicProperties properties;
	
	@BeforeMethod
	public void setup() {
		channel = mock(Channel.class);
		handler = mock(EventHandler.class);
		consumer = new AmqpConsumer(queueName, channel, handler, null);
		envelope = new Envelope(1234L, true, "some_exchange", "some_routing_key");
		event = new Event("12345", "some_type", DateTime.now());
		properties = new BasicProperties.Builder().build();
	}
	
	@Test
	public void shouldStartConsumer() throws IOException {
		consumer.start();
		verify(channel).basicConsume("test_queue", false, consumer);
	}
	
	@Test
	public void shouldCloseChannelOnStop() throws IOException, TimeoutException {
		consumer.stop();
		verify(channel).close();
	}
	
	@Test
	public void shouldCallHandlerOnMessageDelivery() throws IOException {
		when(handler.handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders())).thenReturn(Status.success);
		consumer.handleDelivery("some_consumer_tag", envelope, properties, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes());
		verify(handler).handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders());
	}
	
	@Test
	public void shouldAckMessageWhenHandlerReturnsSuccess() throws IOException {
		when(handler.handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders())).thenReturn(Status.success);
		consumer.handleDelivery("some_consumer_tag", envelope, properties, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes());
		verify(channel).basicAck(envelope.getDeliveryTag(), false);
	}
	
	@Test
	public void shouldNackAndRequeueMessageWhenHandlerReturnsSoftFailure() throws IOException {
		when(handler.handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders())).thenReturn(Status.soft_failure);
		consumer.handleDelivery("some_consumer_tag", envelope, properties, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes());
		verify(channel).basicNack(envelope.getDeliveryTag(), false, true);
	}
	
	@Test
	public void shouldNackAndNotRequeueMessageWhenHandlerReturnsHardFailure() throws IOException {
		when(handler.handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders())).thenReturn(Status.hard_failure);
		consumer.handleDelivery("some_consumer_tag", envelope, properties, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes());
		verify(channel).basicNack(envelope.getDeliveryTag(), false, false);
	}
	
	@Test
	public void shouldNackAndNotRequeueMessageWhenEventDeserializationFails() throws IOException {
		consumer.handleDelivery("some_consumer_tag", envelope, properties, Serializer.DEFAULT_JSON_SERIALIZER.serialize("some_event").getBytes());
		verify(channel).basicNack(envelope.getDeliveryTag(), false, false);
	}
	
	@Test
	public void shouldNotCallHandlerWhenEventDeserializationFails() throws IOException {
		when(handler.handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders())).thenReturn(Status.success);
		consumer.handleDelivery("some_consumer_tag", envelope, properties, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes());
		verify(handler).handle(event, queueName, envelope.getRoutingKey(), properties.getHeaders());
	}
	
	@Test
	public void shouldCallShutdownHandlerWhenItsNotApplicationInitiated() {
		final AtomicBoolean handlerInvoked = new AtomicBoolean();
		consumer = new AmqpConsumer("test_queue", channel, handler, c -> {
			handlerInvoked.set(c == consumer);
		});
		ShutdownSignalException exception = new ShutdownSignalException(false, false, null, this);
		consumer.handleShutdownSignal("some_consumer_tag", exception);
		assertTrue(handlerInvoked.get());
	}
	
	@Test
	public void shouldNotCallShutdownHandlerWhenItsApplicationInitiated() {
		final AtomicBoolean handlerInvoked = new AtomicBoolean();
		consumer = new AmqpConsumer("test_queue", channel, handler, c -> {
			handlerInvoked.set(true);
		});
		ShutdownSignalException exception = new ShutdownSignalException(false, true, null, this);
		consumer.handleShutdownSignal("some_consumer_tag", exception);
		assertFalse(handlerInvoked.get());
	}
}
