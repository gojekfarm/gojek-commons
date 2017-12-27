/**
 *
 */
package com.gojek.amqp;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.amqp.event.AmqpConsumer;
import com.gojek.amqp.event.EventHandler;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.Event;
import com.rabbitmq.client.Channel;

/**
 * @author ganeshs
 *
 */
public class AmqpConsumerContainerTest {

	private AmqpConsumerContainer<Event> container;
	
	private EventHandler<Event> handler;
	
	private int maxConsumers;
	
	private AmqpConnection connection;
	
	private Channel channel;
	
	private ConsumerConfiguration consumerConfig;
	
	@BeforeMethod
	public void setup() {
		maxConsumers = 5;
		handler = mock(EventHandler.class);
		consumerConfig = new ConsumerConfiguration("test_queue", null);
		consumerConfig.setMaxQueueConsumers(maxConsumers);
		connection = mock(AmqpConnection.class);
		container = spy(new AmqpConsumerContainer<Event>(consumerConfig, handler, connection));
		when(connection.getChannel()).thenReturn(channel);
	}
	
	@Test
	public void shouldCreateConsumer() {
		AmqpConsumer<Event> consumer = container.createConsumer(channel);	
		assertEquals(consumer.getChannel(), channel);
	}
	
	@Test
	public void shouldStartConsumers() {
		AmqpConsumer<Event> consumer = mock(AmqpConsumer.class);
		doReturn(consumer).when(container).createConsumer(channel);
		container.start();
		verify(consumer, times(5)).start();
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldNotStartIfConnectionIsNotSet() {
	    container = new AmqpConsumerContainer<Event>(consumerConfig, handler, null);
		container.start();
	}
	
	@Test
	public void shouldStopConsumers() {
		AmqpConsumer<Event> consumer = mock(AmqpConsumer.class);
		doReturn(consumer).when(container).createConsumer(channel);
		container.start();
		container.stop();
		verify(consumer, times(5)).stop();
	}
	
	@Test
	public void shouldHandleConsumerShutdown() {
		AmqpConsumer<Event> consumer = mock(AmqpConsumer.class);
		AmqpConsumer<Event> newConsumer = mock(AmqpConsumer.class);
		doReturn(newConsumer).when(container).createConsumer(channel);
		container.handleShutdown(consumer);
		verify(newConsumer).start();
	}
}
