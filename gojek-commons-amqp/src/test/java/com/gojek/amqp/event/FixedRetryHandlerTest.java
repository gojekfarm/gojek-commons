/**
 *
 */
package com.gojek.amqp.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Lists;
import com.gojek.amqp.AmqpConnection;
import com.gojek.core.event.Consumer.Status;
import com.gojek.core.event.Destination;
import com.gojek.core.event.Event;
import com.gojek.util.serializer.Serializer;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author ganeshs
 *
 */
public class FixedRetryHandlerTest {
	
	private FixedRetryHandler handler;
	
	private Channel channel;
	
	private AmqpConnection connection;
	
	private String queueName = "primary_queue";
	
	private String routingKey = "some_routing_key";
	
	private Map<String, Object> headers = Maps.newHashMap();
	
	private Destination retryDestination = new Destination("test_retry_exchange");
	
	private int maxRetries = 5;
	
	private Event event;

	@BeforeMethod
	public void setup() throws Exception {
		this.channel = mock(Channel.class);
		Connection conn = mock(Connection.class);
		when(conn.createChannel()).thenReturn(channel);
		connection = new AmqpConnection();
		connection.init(conn, 2, 2, 2);
		handler = spy(new FixedRetryHandler(connection, retryDestination, maxRetries));
		this.event = new Event("some_id", "some_type", DateTime.now());
	}

	@Test
	public void shouldReturnSuccessWhenNoFailures() {
		doReturn(Status.success).when(handler).handleInternal(event);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.success);
	}
	
	@Test
	public void shouldNotRetryWhenNoFailures() throws Exception {
		doReturn(Status.success).when(handler).handleInternal(event);
		handler.handle(event, queueName, routingKey, headers);
		verify(channel, never()).basicPublish(any(), any(), any(), any());
	}
	
	@Test
	public void shouldReturnHardFailureOnHardFailure() {
		doReturn(Status.hard_failure).when(handler).handleInternal(event);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.hard_failure);
	}
	
	@Test
	public void shouldNotRetryOnHardFailures() throws Exception {
		doReturn(Status.hard_failure).when(handler).handleInternal(event);
		handler.handle(event, queueName, routingKey, headers);
		verify(channel, never()).basicPublish(any(), any(), any(), any());
	}
	
	@Test
	public void shouldRetryOnSoftFailure() throws Exception {
		doReturn(Status.soft_failure).when(handler).handleInternal(event);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.success);
		verify(channel).basicPublish(eq(retryDestination.getExchange()), eq(routingKey), any(), eq(Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes()));
	}
	
	@Test
	public void shouldRetryOnFirstSoftFailure() throws Exception {
		doReturn(Status.soft_failure).when(handler).handleInternal(event);
		headers = headersWithDeathList(0);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.success);
		verify(channel).basicPublish(eq(retryDestination.getExchange()), eq(routingKey), any(), eq(Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes()));
	}
	
	@Test
	public void shouldRetryOnSecondSoftFailure() throws Exception {
		doReturn(Status.soft_failure).when(handler).handleInternal(event);
		headers = headersWithDeathList(1);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.success);
		verify(channel).basicPublish(eq(retryDestination.getExchange()), eq(routingKey), any(), eq(Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes()));
	}
	
	@Test
	public void shouldNotRetryOnSoftFailureIfRetryCountIsMoreThanMaxRetries() throws Exception {
		doReturn(Status.soft_failure).when(handler).handleInternal(event);
		headers = headersWithDeathList(maxRetries);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.hard_failure);
		verify(channel, never()).basicPublish(eq(retryDestination.getExchange()), eq(routingKey), any(), eq(Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes()));
	}
	
	@Test
	public void shouldReturnHardFailureIfPublishingToRetryQueueFails() throws Exception {
		doReturn(Status.soft_failure).when(handler).handleInternal(event);
		headers = headersWithDeathList(maxRetries);
		doThrow(Exception.class).when(channel).basicPublish(eq(retryDestination.getExchange()), eq(routingKey), any(), eq(Serializer.DEFAULT_JSON_SERIALIZER.serialize(event).getBytes()));
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.hard_failure);
	}
	
	@Test
	public void shouldCallMaxRetryExceededHandler() {
		doReturn(Status.soft_failure).when(handler).handleInternal(event);
		headers = headersWithDeathList(maxRetries);
		assertEquals(handler.handle(event, queueName, routingKey, headers), Status.hard_failure);
		verify(handler).handleMaxRetryExceeded(event);
	}
	
	private Map<String, Object> headersWithDeathList(int failureCount) {
		Map<String, Object> headers = Maps.newHashMap();
		List<Map<String, Object>> deathList = Lists.newArrayList();
		for (int i = 0; i < failureCount; i++) {
			Map<String, Object> map = Maps.newHashMap();
			map.put(FixedRetryHandler.ATTR_QUEUE, queueName);
			map.put(FixedRetryHandler.ATTR_DEATH_COUNT, 1L);
			deathList.add(map);
		}
		headers.put(FixedRetryHandler.HEADER_X_DEATH, deathList);
		return headers;
	}
}
