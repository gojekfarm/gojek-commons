/**
 *
 */
package com.gojek.amqp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.amqp.event.AmqpConsumer;
import com.gojek.core.event.Consumer;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.EventHandler;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;

import io.dropwizard.lifecycle.Managed;

/**
 * @author ganeshs
 *
 */
public class AmqpConsumerContainer<E> implements Consumer.ShutdownListener, Managed {
	
	private String queueName;
	
	private int maxConsumers;

	private Integer prefetchCount;
	
	private List<AmqpConsumer<E>> consumers;
	
	private EventHandler<E> handler;
	
	private AmqpConnection connection;
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpConsumerContainer.class);
	
	/**
	 * @param configuration
	 * @param handler
	 * @param connection
	 */
	public AmqpConsumerContainer(ConsumerConfiguration configuration, EventHandler<E> handler, AmqpConnection connection) {
		this.queueName = configuration.getQueueName();
		this.maxConsumers = configuration.getMaxQueueConsumers();
		this.prefetchCount = configuration.getPrefetchCount();
		this.handler = handler;
		this.connection = connection;
		this.consumers = Lists.newArrayList();
	}
	
	/**
	 * Starts the container
	 */
	public void start() {
		logger.info("Starting the container");
		if (connection == null) {
			throw new IllegalStateException("Connection is not set");
		}
		for (int i = 0; i < maxConsumers; i++) {
			addConsumer(connection.getChannel());
		}
	}
	
	/**
	 * Starts the consumer
	 *
	 * @param channel
	 */
	protected void addConsumer(Channel channel) {
		AmqpConsumer<E> consumer = createConsumer(channel);
		consumer.start();
		this.consumers.add(consumer);
	}
	
	/**
	 * @param channel
	 * @return
	 */
	protected AmqpConsumer<E> createConsumer(Channel channel) {
		return new AmqpConsumer<E>(queueName, prefetchCount, channel, handler, this);
	}
	
	/**
	 * Stops the container
	 */
	public void stop() {
		logger.info("Stopping the container");
		for (AmqpConsumer<E> consumer : this.consumers) {
			consumer.stop();
		}
	}

	@Override
	public synchronized void handleShutdown(Consumer<?> consumer) {
		logger.info("Removing the shutdown consumer and adding a new one as a replacement");
		this.consumers.remove(consumer);
		if (connection != null) {
			this.addConsumer(connection.getChannel());
		}
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connection == null) ? 0 : connection.hashCode());
        result = prime * result + ((handler == null) ? 0 : handler.hashCode());
        result = prime * result + ((queueName == null) ? 0 : queueName.hashCode());
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
        AmqpConsumerContainer<E> other = (AmqpConsumerContainer<E>) obj;
        if (connection == null) {
            if (other.connection != null)
                return false;
        } else if (!connection.equals(other.connection))
            return false;
        if (handler == null) {
            if (other.handler != null)
                return false;
        } else if (!handler.equals(other.handler))
            return false;
        if (queueName == null) {
            if (other.queueName != null)
                return false;
        } else if (!queueName.equals(other.queueName))
            return false;
        return true;
    }
}
