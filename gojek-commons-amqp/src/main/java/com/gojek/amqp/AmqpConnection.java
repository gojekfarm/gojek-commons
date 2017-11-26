/**
 *
 */
package com.gojek.amqp;

import java.util.Optional;
import java.util.function.Function;

import javax.inject.Singleton;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Holds a connection to the amqp server. Maintains a pool of channels to ensure the a channel is accessed by only one thread at any point of time  
 *
 * @author ganeshs
 *
 */
@Singleton
public class AmqpConnection {
	
	private Connection connection;

	private GenericObjectPool<Channel> pool;
	
	private Optional<ConnectionPoolListener> listener = Optional.empty();
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpConnection.class);
	
	/**
	 * @param listener
	 */
	public void register(ConnectionPoolListener listener) {
	    this.listener = Optional.of(listener);
	}
	
	/**
	 * @param connection
	 * @param maxChannels
	 * @param minChannels
	 * @param maxIdleChannels
	 */
	public void init(Connection connection, int maxChannels, int minChannels, int maxIdleChannels) {
		this.connection = connection;
		GenericObjectPoolConfig config = constructPoolConfig(maxChannels, minChannels, maxIdleChannels);
		this.pool = constructPool(connection, config);
		if (this.listener.isPresent()) {
		    this.listener.get().onPoolInitialized(pool);
		}
	}
	
	/**
	 * @param maxChannels
	 * @param minChannels
	 * @param maxIdleChannels
	 * @return
	 */
	protected GenericObjectPoolConfig constructPoolConfig(int maxChannels, int minChannels, int maxIdleChannels) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(maxIdleChannels);
		config.setMaxTotal(maxChannels);
		config.setMinIdle(minChannels);
		return config;
	}
	
	/**
	 * @param connection
	 * @param poolConfig
	 * @return
	 */
	protected GenericObjectPool<Channel> constructPool(Connection connection, GenericObjectPoolConfig poolConfig) {
		AmqpConnectionPooledObjectFactory pooledFactory = new AmqpConnectionPooledObjectFactory(connection);
		return new GenericObjectPool<>(pooledFactory, poolConfig);
	}
	
	/**
	 * Returns a connection
	 */
	protected Channel getChannel() {
		if (this.pool == null) {
			logger.error("Connection is not initialized yet");
			throw new AmqpException("Connection is not initialized yet");
		}
		try {
			return this.pool.borrowObject();
		} catch (Exception e) {
			logger.error("Failed while getting the connection from pool", e);
			throw new AmqpException("Failed while getting the connection from pool", e);
		}
	}
	
	/**
	 * @param channel
	 */
	protected void releaseChannel(Channel channel) {
		if (this.pool == null) {
			logger.error("Connection is not initialized yet");
			throw new AmqpException("Connection is not initialized yet");
		}
		this.pool.returnObject(channel);
	}
	
	/**
	 * Returns the underlying channel pool
	 *
	 * @return
	 */
	protected GenericObjectPool<Channel> getPool() {
		return this.pool;
	}
	
	/**
	 * @param executor
	 * @return
	 */
	public <T> T execute(Function<Channel, T> executor) {
		Channel channel = getChannel();
		try {
			return executor.apply(channel);
		} finally {
			if (channel != null) {
				releaseChannel(channel);
			}
		}
	}
	
	/**
	 * Closes the underlying connection and the channel pool
	 */
	public void close() {
		logger.info("Closing the amqp connection");
		try {
			if (this.connection != null) {
				this.connection.close();
			}
		} catch (Exception e) {
			logger.error("Failed while closing the connection");
		}
		if (this.pool != null) {
			this.pool.close();
		}
		if (this.listener.isPresent()) {
            this.listener.get().onPoolDestroyed(this.pool);
        }
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	public class AmqpConnectionPooledObjectFactory extends BasePooledObjectFactory<Channel> {
		
		private Connection connection;
		
		/**
		 * @param connection
		 */
		public AmqpConnectionPooledObjectFactory(Connection connection) {
			this.connection = connection;
		}
		
		@Override
		public Channel create() throws Exception {
			logger.info("Creating a new amqp connection");
			return connection.createChannel();
		}

		@Override
		public PooledObject<Channel> wrap(Channel channel) {
			return new DefaultPooledObject<Channel>(channel);
		}
		
		@Override
		public void destroyObject(PooledObject<Channel> p) throws Exception {
			logger.info("Destroying the connection");
			Channel channel = p.getObject();
			if (channel != null && channel.isOpen()) {
				channel.close();
			}
			super.destroyObject(p);
		}
		
		@Override
		public boolean validateObject(PooledObject<Channel> p) {
			logger.debug("Validating the connection");
			Channel channel = p.getObject();
			if (channel != null && channel.isOpen()) {
				return true;
			}
			return false;
		}
	}
	
    /**
     * @author ganeshs
     *
     */
    public static class ConnectionPoolListener {
        
        /**
         * Invoked when the pool is initialized
         *
         * @param pool
         */
        public void onPoolInitialized(GenericObjectPool<Channel> pool) {
        }
        
        /**
         * Invoked when the pool is closed
         *
         * @param pool
         */
        public void onPoolDestroyed(GenericObjectPool<Channel> pool) {
        }
    }
}
