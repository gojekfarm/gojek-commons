/**
 *
 */
package com.gojek.amqp.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class AmqpProducerTest {

	private AmqpProducer producer;
	
	private Connection connection;
	
	private Channel channel;
	
	private Event event1;
	
	@BeforeMethod
	public void setup() throws Exception {
		event1 = new Event("dummy-id-1", "test-event", DateTime.now());
		connection = mock(Connection.class);
		AmqpConnection amqpConnection = new AmqpConnection();
		amqpConnection.init(connection, 1, 1, 1);
		channel = mock(Channel.class);
		when(connection.createChannel()).thenReturn(channel);
		producer = new AmqpProducer(amqpConnection);
	}
	
	@Test
	public void shouldFlushMessagesFromQueue() throws Exception {
		Destination destination1 = new Destination("test_exchange", "test.topic1");
		producer.send(event1, destination1);
		verify(channel).basicPublish(destination1.getExchange(), destination1.getRoutingKey(), null, Serializer.DEFAULT_JSON_SERIALIZER.serialize(event1).getBytes());
	}
	
}
