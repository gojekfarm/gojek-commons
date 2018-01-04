/**
 * 
 */
package com.gojek.kafka.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.google.common.base.Stopwatch;
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
	
	private KafkaConsumerConfiguration configuration;

	@BeforeMethod
	public void setup() {
		consumer = spy(new MockConsumer<>(OffsetResetStrategy.EARLIEST));
		eventHandler = mock(EventHandler.class);
		when(eventHandler.getEventClass()).thenReturn(String.class);
		shutdownListener = mock(Consumer.ShutdownListener.class);
		TopicPartition partition = new TopicPartition("test_topic", 0);
		consumer.assign(Lists.newArrayList(partition));
		Map<TopicPartition, Long> beginningOffsets = Maps.newHashMap();
	    beginningOffsets.put(partition, 0L);
	    consumer.updateBeginningOffsets(beginningOffsets);
	    configuration = new KafkaConsumerConfiguration(Lists.newArrayList("test_topic"), true, 100L, 5);
	    kafkaConsumer = new KafkaConsumer<>(consumer, configuration, eventHandler, shutdownListener);
	}
	
	@Test
	public void shouldStartConsumingMessagesOnStart() throws InterruptedException {
		when(eventHandler.handle("test_value", "test_topic", null, Maps.newHashMap())).thenReturn(Status.success);
		consumer.addRecord(new ConsumerRecord<String, String>("test_topic", 0, 0, "test_key", "test_value"));
		kafkaConsumer.start();
		Thread.sleep(50);
		verify(eventHandler).handle("test_value", "test_topic", null, Maps.newHashMap());
	}
	
	@Test
	public void shouldCommitAsync() throws InterruptedException {
		configuration.setCommitSync(false);
		when(eventHandler.handle("test_value", "test_topic", null, Maps.newHashMap())).thenReturn(Status.success);
		consumer.addRecord(new ConsumerRecord<String, String>("test_topic", 0, 0, "test_key", "test_value"));
		kafkaConsumer.start();
		Thread.sleep(50);
		verify(consumer).commitAsync();
	}
	
	@Test
	public void shouldCommitSync() throws InterruptedException {
		configuration.setCommitSync(true);
		when(eventHandler.handle("test_value", "test_topic", null, Maps.newHashMap())).thenReturn(Status.success);
		consumer.addRecord(new ConsumerRecord<String, String>("test_topic", 0, 0, "test_key", "test_value"));
		kafkaConsumer.start();
		Thread.sleep(50);
		verify(consumer).commitSync();
	}
	
	@Test
	public void shouldWakeupAndCloseConsumerOnStop() throws InterruptedException {
		kafkaConsumer.start();
		Thread.sleep(50);
		kafkaConsumer.stop();
		verify(consumer).wakeup();
		verify(consumer).close();
		assertFalse(kafkaConsumer.isRunning());
	}
	
	@Test
	public void shouldWakeupFromPollOnStop() throws InterruptedException {
		configuration.setPollTimeout(5000L);
		kafkaConsumer.start();
		Thread.sleep(50);
		Stopwatch watch = Stopwatch.createStarted();
		kafkaConsumer.stop();
		assertTrue(watch.stop().elapsed(TimeUnit.MILLISECONDS) < configuration.getPollTimeout());
		assertFalse(kafkaConsumer.isRunning());
	}
	
	@Test
	public void shouldCloseConsumerIfPollThrowsException() throws InterruptedException {
		configuration.setPollTimeout(5000L);
		doThrow(Exception.class).when(consumer).poll(configuration.getPollTimeout());
		kafkaConsumer.start();
		Thread.sleep(50);
		verify(consumer).close();
		assertFalse(kafkaConsumer.isRunning());
	}
	
	@Test
	public void shouldCheckIsRunning() {
		kafkaConsumer.start();
		assertTrue(kafkaConsumer.isRunning());
		kafkaConsumer.stop();
		assertFalse(kafkaConsumer.isRunning());
	}
	
	@Test
	public void shouldStartOnNewThread() {
		ExecutorService executor = mock(ExecutorService.class);
		kafkaConsumer = new KafkaConsumer<>(consumer, configuration, eventHandler, shutdownListener, executor);
		kafkaConsumer.start();
		verify(executor).execute(any(Runnable.class));
	}
	
	@Test
	public void shouldWaitForExecutorToShutdownOnStop() throws InterruptedException {
		ExecutorService executor = mock(ExecutorService.class);
		kafkaConsumer = new KafkaConsumer<>(consumer, configuration, eventHandler, shutdownListener, executor);
		kafkaConsumer.stop();
		verify(executor).awaitTermination(1000, TimeUnit.MILLISECONDS);
	}
	
}
