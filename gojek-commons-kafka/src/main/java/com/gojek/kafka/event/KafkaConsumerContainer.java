/**
 *
 */
package com.gojek.kafka.event;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.core.event.Consumer;
import com.gojek.core.event.Consumer.ShutdownListener;
import com.gojek.core.event.EventHandler;
import com.gojek.kafka.KafkaConsumerConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.dropwizard.lifecycle.Managed;

/**
 * @author ganeshs
 *
 */
public class KafkaConsumerContainer<K, E> implements ShutdownListener, Managed {
	
	private Map<String, Object> kafkaConfig = Maps.newHashMap();
	
	private KafkaConsumerConfiguration configuration;
	
	private List<KafkaConsumer<K, E>> consumers;
	
	private EventHandler<E> handler;
	
	private boolean stopped;
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerContainer.class);
	
	/**
	 * @param kafkaConfig
	 * @param configuration
	 * @param handler
	 */
	public KafkaConsumerContainer(Map<String, Object> kafkaConfig, KafkaConsumerConfiguration configuration, EventHandler<E> handler) {
		this.kafkaConfig = kafkaConfig;
		this.configuration = configuration;
		this.handler = handler;
		this.consumers = Lists.newArrayList();
	}
	
	/**
	 * Starts the container
	 */
	public void start() {
		logger.info("Starting the container");
		for (int i = 0; i < configuration.getMaxQueueConsumers(); i++) {
			addConsumer();
		}
	}
	
	/**
	 * Adds the consumer
	 */
	protected void addConsumer() {
		KafkaConsumer<K, E> consumer = createConsumer();
		consumer.start();
		this.consumers.add(consumer);
	}
	
	/**
	 * @return
	 */
	protected KafkaConsumer<K, E> createConsumer() {
		return new KafkaConsumer<>(kafkaConfig, configuration, handler, this);
	}
	
	/**
	 * Stops the container
	 */
	public void stop() {
		logger.info("Stopping the container");
		stopped = true;
		for (KafkaConsumer<K, E> consumer : this.consumers) {
			consumer.stop();
		}
		this.consumers.clear();
	}

	@Override
	public synchronized void handleShutdown(Consumer<?> consumer) {
		logger.info("Removing the shutdown consumer and adding a new one as a replacement");
		this.consumers.remove(consumer);
		if (! stopped) {
			this.addConsumer();
		}
	}
	
	/**
	 * For unit testing
	 * 
	 * @return
	 */
	List<KafkaConsumer<K, E>> getConsumers() {
		return consumers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
		result = prime * result + ((consumers == null) ? 0 : consumers.hashCode());
		result = prime * result + ((handler == null) ? 0 : handler.hashCode());
		result = prime * result + ((kafkaConfig == null) ? 0 : kafkaConfig.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KafkaConsumerContainer<K, E> other = (KafkaConsumerContainer<K, E>) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		if (consumers == null) {
			if (other.consumers != null)
				return false;
		} else if (!consumers.equals(other.consumers))
			return false;
		if (handler == null) {
			if (other.handler != null)
				return false;
		} else if (!handler.equals(other.handler))
			return false;
		if (kafkaConfig == null) {
			if (other.kafkaConfig != null)
				return false;
		} else if (!kafkaConfig.equals(other.kafkaConfig))
			return false;
		return true;
	}
}
