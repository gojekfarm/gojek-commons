/**
 * 
 */
package com.gojek.kafka.event;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.core.event.Consumer;
import com.gojek.core.event.EventHandler;
import com.gojek.kafka.KafkaConsumerConfiguration;
import com.gojek.kafka.KafkaException;
import com.google.common.collect.Maps;

/**
 * @author ganesh.s
 *
 */
public class KafkaConsumer<K, E> implements Consumer<E> {
	
	private org.apache.kafka.clients.consumer.Consumer<K, E> consumer;
	
	private EventHandler<E> handler;
	
	private KafkaConsumerConfiguration configuration;
	
	private Boolean running = false;
	
	private Consumer.ShutdownListener shutdownListener;
	
	private ExecutorService executor;
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

	/**
	 * @param configs
	 * @param configuration
	 * @param handler
	 */
	public KafkaConsumer(Map<String, Object> configs, KafkaConsumerConfiguration configuration, EventHandler<E> handler, Consumer.ShutdownListener shutdownListener) {
		this(createConsumer(configs, configuration), configuration, handler, shutdownListener);
	}
	
	/**
	 * @param configs
	 * @param configuration
	 * @param handler
	 */
	public KafkaConsumer(Map<String, Object> configs, KafkaConsumerConfiguration configuration, EventHandler<E> handler, ThreadFactory threadFactory, Consumer.ShutdownListener shutdownListener) {
		this(createConsumer(configs, configuration), configuration, handler, threadFactory, shutdownListener);
	}
	
	/**
	 * @param consumer
	 * @param configuration
	 * @param handler
	 * @param shutdownListener
	 */
	public KafkaConsumer(org.apache.kafka.clients.consumer.Consumer<K, E> consumer, KafkaConsumerConfiguration configuration, EventHandler<E> handler, Consumer.ShutdownListener shutdownListener) {
		this(consumer, configuration, handler, Executors.defaultThreadFactory(), shutdownListener);
	}
	
	/**
	 * @param consumer
	 * @param configuration
	 * @param handler
	 * @param threadFactory
	 * @param shutdownListener
	 */
	public KafkaConsumer(org.apache.kafka.clients.consumer.Consumer<K, E> consumer, KafkaConsumerConfiguration configuration, EventHandler<E> handler, ThreadFactory threadFactory, Consumer.ShutdownListener shutdownListener) {
		this(consumer, configuration, handler, shutdownListener, Executors.newSingleThreadExecutor(threadFactory));
	}
	
	/**
	 * @param consumer
	 * @param configuration
	 * @param handler
	 * @param shutdownListener
	 * @param executor
	 */
	public KafkaConsumer(org.apache.kafka.clients.consumer.Consumer<K, E> consumer, KafkaConsumerConfiguration configuration, EventHandler<E> handler, Consumer.ShutdownListener shutdownListener, ExecutorService executor) {
		this.consumer = consumer;
		this.configuration = configuration;
		this.handler = handler;
		this.shutdownListener = shutdownListener;
		this.executor = executor;
	}
	
	/**
	 * @param configs
	 * @param configuration
	 * @return
	 */
	private static <K, E> org.apache.kafka.clients.consumer.KafkaConsumer<K, E> createConsumer(Map<String, Object> configs, KafkaConsumerConfiguration configuration) {
		try {
			if (configuration.getKeyDeserializer() != null) {
				configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, configuration.getKeyDeserializer().getName());
			}
			if (configuration.getValueDeserializer() != null) {
				configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, configuration.getValueDeserializer().getName());
			}
			org.apache.kafka.clients.consumer.KafkaConsumer<K, E> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<K, E>(configs);
			consumer.subscribe(configuration.getTopics());
			return consumer;
		} catch (Exception e) {
			logger.error("Failed while creating a consumer", e);
			throw new KafkaException("Failed while creating a consumer", e);
		}
	}
	
	/**
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	@Override
	public void start() {
		logger.info("Starting the kafka consumer");
		running = true;
		
		executor.execute(() -> {
			while (running) {
				Optional<ConsumerRecords<K, E>> records = Optional.empty();
				try {
					records = Optional.of(this.consumer.poll(configuration.getPollTimeout()));
					if (records.get().count() == 0) {
						continue;
					}
				} catch (WakeupException e) {
					logger.info("Got a wakeup call. Doing nothing");
				} catch (Exception e) {
					logger.error("Error while polling. Breaking the loop", e);
					break;
				}
				
				if (records != null) {
					records.orElse(ConsumerRecords.empty()).forEach(record -> {
						Status status = Status.soft_failure;
						try {
							status = receive(record);
						} catch (Exception e) {
							logger.error("Failed while handling the event", e);
						} finally {
							if (status == null && status != Status.success) {
								logger.info("Failed while handling the event. Status - {}", status); 
							}
						}
					});
				}
				
				if (configuration.isCommitSync()) {
					this.consumer.commitSync();
				} else {
					this.consumer.commitAsync();
				}
			}
			try {
				// Ensure running is set to false
				running = false;
				this.consumer.close();
			} finally {
				this.shutdownListener.handleShutdown(this);
			}
		});
	}
	
	@Override
	public void stop() {
		logger.info("Stopping the kafka consumer");
		running = false;	
		this.consumer.wakeup();
		try {
			executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("Error while waiting for executor to shutdown", e);
		}
	}
	
	/**
	 * @param record
	 * @return
	 */
	public Status receive(ConsumerRecord<K, E> record) {
		return this.handler.handle(record.value(), record.topic(), null, Maps.newHashMap());
	}
}
