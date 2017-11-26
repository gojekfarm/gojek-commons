/**
 *
 */
package com.gojek.amqp.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.amqp.AmqpConnection;
import com.gojek.core.event.Destination;
import com.gojek.core.event.Event;
import com.gojek.util.serializer.Serializer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author ganeshs
 *
 */
public class AmqpQueuedProducerTest {

	private AmqpQueuedProducer producer;
	
	private Connection connection;
	
	private Channel channel;
	
	private Event event1;
	
	private Event event2;
	
	@BeforeMethod
	public void setup() throws Exception {
		event1 = new Event("dummy-id-1", "test-event", DateTime.now());
		event2 = new Event("dummy-id-2", "test-event", DateTime.now());
		connection = mock(Connection.class);
		AmqpConnection amqpConnection = new AmqpConnection();
		amqpConnection.init(connection, 1, 1, 1);
		channel = mock(Channel.class);
		when(connection.createChannel()).thenReturn(channel);
		producer = new AmqpQueuedProducer(amqpConnection);
	}
	
	@Test
	public void shouldEnqueueMessagesOnSend() throws Exception {
		Destination destination = new Destination("test_exchange", "test.topic");
		producer.send(event1, destination);
		producer.send(event2, destination);
		List<Event> events = producer.getQueue(destination);
		verify(channel, never()).basicPublish(eq(destination.getExchange()), eq(destination.getRoutingKey()), any(), any());
		assertEquals(events.size(), 2);
		assertTrue(events.contains(event1));
		assertTrue(events.contains(event2));
	}
	
	@Test
	public void shouldEnqueueMessagesToMultipleTopicsOnSend() {
		Destination destination1 = new Destination("test_exchange", "test.topic1");
		Destination destination2 = new Destination("test_exchange", "test.topic2");
		producer.send(event1, destination1);
		producer.send(event2, destination2);
		List<Event> topic1Events = producer.getQueue(destination1);
		List<Event> topic2Events = producer.getQueue(destination2);
		assertTrue(topic1Events.contains(event1));
		assertTrue(topic2Events.contains(event2));
	}
	
	@Test
	public void shouldFlushMessagesFromQueue() throws Exception {
		Destination destination1 = new Destination("test_exchange", "test.topic1");
		Destination destination2 = new Destination("test_exchange", "test.topic2");
		producer.send(event1, destination1);
		producer.send(event2, destination2);
		producer.flush();
		verify(channel).basicPublish(destination1.getExchange(), destination1.getRoutingKey(), null, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event1).getBytes());
		verify(channel).basicPublish(destination2.getExchange(), destination2.getRoutingKey(), null, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event2).getBytes());
		assertTrue(producer.getQueue(destination1).isEmpty());
		assertTrue(producer.getQueue(destination2).isEmpty());
	}
	
	@Test
	public void shouldClearMessagesFromQueue() throws Exception {
		Destination destination1 = new Destination("test_exchange", "test.topic1");
		Destination destination2 = new Destination("test_exchange", "test.topic2");
		producer.send(event1, destination1);
		producer.send(event2, destination2);
		producer.clear();
		verify(channel, never()).basicPublish(any(), any(), any(), any());
		assertTrue(producer.getQueue(destination1).isEmpty());
		assertTrue(producer.getQueue(destination2).isEmpty());
	}
	
	@Test
	public void shouldEnqueueMessagesAtThreadScope() throws InterruptedException {
		Destination destination = new Destination("test_exchange", "test.topic");
		AtomicInteger counter = new AtomicInteger();
		Thread thread1 = new Thread(() -> {
			producer.send(event1, destination);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			assertTrue(producer.getQueue(destination).contains(event1));
			assertFalse(producer.getQueue(destination).contains(event2));
			counter.incrementAndGet();
		});
		Thread thread2 = new Thread(() -> {
			producer.send(event2, destination);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			assertTrue(producer.getQueue(destination).contains(event2));
			assertFalse(producer.getQueue(destination).contains(event1));
			counter.incrementAndGet();
		});
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		assertEquals(counter.get(), 2, "Not all the threads completed successfully");
	}
	
	@Test
	public void shouldClearMessagesAtThreadScope() throws InterruptedException {
		Destination destination = new Destination("test_exchange", "test.topic");
		AtomicInteger counter = new AtomicInteger();
		Thread thread1 = new Thread(() -> {
			producer.send(event1, destination);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			producer.clear();
			assertTrue(producer.getQueue(destination).isEmpty());
			counter.incrementAndGet();
		});
		Thread thread2 = new Thread(() -> {
			producer.send(event2, destination);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			assertEquals(producer.getQueue(destination).size(), 1);
			assertTrue(producer.getQueue(destination).contains(event2));
			counter.incrementAndGet();
		});
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		assertEquals(counter.get(), 2, "Not all the threads completed successfully");
	}
	
	@Test
	public void shouldFlushMessagesAtThreadScope() throws InterruptedException {
		Destination destination = new Destination("test_exchange", "test.topic");
		AtomicInteger counter = new AtomicInteger();
		Thread thread1 = new Thread(() -> {
			producer.send(event1, destination);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			producer.flush();
			assertTrue(producer.getQueue(destination).isEmpty());
			counter.incrementAndGet();
		});
		Thread thread2 = new Thread(() -> {
			producer.send(event2, destination);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			assertEquals(producer.getQueue(destination).size(), 1);
			assertTrue(producer.getQueue(destination).contains(event2));
			counter.incrementAndGet();
		});
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		assertEquals(counter.get(), 2, "Not all the threads completed successfully");
	}
}
