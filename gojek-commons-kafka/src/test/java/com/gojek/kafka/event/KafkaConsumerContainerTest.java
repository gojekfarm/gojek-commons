/**
 * 
 */
package com.gojek.kafka.event;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.core.event.EventHandler;
import com.gojek.kafka.KafkaConsumerConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author ganesh.s
 *
 */
public class KafkaConsumerContainerTest {
	
	private KafkaConsumerContainer<String, String> container;
	
	private Map<String, Object> kafkaConfigs;
	
	private KafkaConsumerConfiguration configuration;
	
	private EventHandler<String> eventHandler;
	
	private List<KafkaConsumer<String, String>> consumers;
	
	@BeforeMethod
	public void setup() {
		kafkaConfigs = Maps.newHashMap();
		configuration = new KafkaConsumerConfiguration(Lists.newArrayList("test_topic"), true, 100L, 3);
		container = spy(new KafkaConsumerContainer<>(kafkaConfigs, configuration, eventHandler));
		consumers = IntStream.range(0, configuration.getMaxQueueConsumers()).mapToObj(i -> mock(KafkaConsumer.class)).collect(Collectors.toList());
		doReturn(consumers.get(0), consumers.subList(1, consumers.size()).toArray(new KafkaConsumer[0])).when(container).createConsumer();
	}

	@Test
	public void shouldStartConsumers() {
		container.start();
		consumers.forEach(consumer -> verify(consumer).start());
	}
	
	@Test
	public void shouldStopConsumers() {
		container.start();
		container.stop();
		consumers.forEach(consumer -> verify(consumer).stop());
		assertTrue(container.getConsumers().isEmpty());
	}
	
	@Test
	public void shouldReplaceConsumerOnShutdown() {
		container.start();
		KafkaConsumer<String, String> consumer = consumers.get(0);
		container.handleShutdown(consumer);
		assertFalse(container.getConsumers().contains(consumer));
		assertEquals(container.getConsumers().size(), consumers.size());
	}
	
	@Test
	public void shouldNotHandleConsumerShutdownIfExplicitlyStopped() {
		container.start();
		container.stop();
		KafkaConsumer<String, String> consumer = consumers.get(0);
		container.handleShutdown(consumer);
		assertTrue(container.getConsumers().isEmpty());
	}
}
