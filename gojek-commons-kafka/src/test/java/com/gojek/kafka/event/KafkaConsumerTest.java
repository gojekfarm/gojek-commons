/**
 * 
 */
package com.gojek.kafka.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.core.event.Consumer;
import com.gojek.core.event.Consumer.Status;
import com.gojek.core.event.EventHandler;
import com.gojek.kafka.KafkaConsumerConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author ganesh.s
 *
 */
public class KafkaConsumerTest {

	private MockConsumer<String, String> consumer;
	
	private KafkaConsumer<String, String> kafkaConsumer;
	
	private EventHandler<String> eventHandler;
	
	private Consumer.ShutdownListener shutdownListener; 

	@BeforeMethod
	public void setup() {
		consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
		eventHandler = mock(EventHandler.class);
		when(eventHandler.getEventClass()).thenReturn(String.class);
		shutdownListener = mock(Consumer.ShutdownListener.class);
		kafkaConsumer = new KafkaConsumer<>(consumer, new KafkaConsumerConfiguration(Lists.newArrayList("test_topic"), true, 100L, 5), eventHandler, shutdownListener);
		TopicPartition partition = new TopicPartition("test_topic", 0);
		consumer.assign(Lists.newArrayList(partition));
		Map<TopicPartition, Long> beginningOffsets = Maps.newHashMap();
	    beginningOffsets.put(partition, 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);
	}
	
	@Test
	public void shouldStartConsumingMessagesOnStart() throws InterruptedException {
		when(eventHandler.handle("test_value", "test_topic", null, Maps.newHashMap())).thenReturn(Status.success);
		consumer.addRecord(new ConsumerRecord<String, String>("test_topic", 0, 0, "test_key", "test_value"));
		kafkaConsumer.start();
		Thread.sleep(50);
		verify(eventHandler).handle("test_value", "test_topic", null, Maps.newHashMap());
	}
	
}
