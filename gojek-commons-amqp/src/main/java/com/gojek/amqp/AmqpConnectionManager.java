/**
 *
 */
package com.gojek.amqp;

import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.ConnectionFactory;

import io.dropwizard.lifecycle.Managed;

/**
 * @author ganeshs
 *
 */
public class AmqpConnectionManager implements Managed {
	
	private AmqpConfiguration configuration;
	
	private AmqpConnection connection;
	
	private ThreadFactory threadFactory;
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpConnectionManager.class);
	
	/**
	 * @param configuration
	 * @param connection
	 */
	public AmqpConnectionManager(AmqpConfiguration configuration, AmqpConnection connection) {
		this(configuration, connection, null);
	}
	
	/**
     * @param configuration
     * @param connection
     * @param threadFactory
     */
    public AmqpConnectionManager(AmqpConfiguration configuration, AmqpConnection connection, ThreadFactory threadFactory) {
        this.configuration = configuration;
        this.connection = connection;
        this.threadFactory = threadFactory;
    }

	/**
	 * Starts the manager
	 */
	public void start() {
		try {
			ConnectionFactory factory = createConnectionFactory();
			this.connection.init(factory.newConnection(configuration.getAddresses()), configuration.getMaxChannels(), configuration.getMinChannels(), configuration.getMaxIdleChannels());
		} catch (Exception e) {
			logger.error("Failed while initializing the amqp connection", e);
			throw new AmqpException("Failed while initializing the amqp connection", e);
		}
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	protected ConnectionFactory createConnectionFactory() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(configuration.getUri());
		if (threadFactory != null) {
		    factory.setThreadFactory(threadFactory);
		}
		factory.setNetworkRecoveryInterval(configuration.getNetworkRecoveryInterval());
		factory.setAutomaticRecoveryEnabled(configuration.isAutoRecovery());
		return factory;
	}
	
	/**
	 * Returns the managed connection
	 *
	 * @return
	 */
	public AmqpConnection getConnection() {
	    return connection;
	}

	/**
	 * Stops the manager
	 */
	public void stop() {
		if (connection != null) {
			this.connection.close();
		}
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
        result = prime * result + ((connection == null) ? 0 : connection.hashCode());
        result = prime * result + ((threadFactory == null) ? 0 : threadFactory.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AmqpConnectionManager other = (AmqpConnectionManager) obj;
        if (configuration == null) {
            if (other.configuration != null)
                return false;
        } else if (!configuration.equals(other.configuration))
            return false;
        if (connection == null) {
            if (other.connection != null)
                return false;
        } else if (!connection.equals(other.connection))
            return false;
        if (threadFactory == null) {
            if (other.threadFactory != null)
                return false;
        } else if (!threadFactory.equals(other.threadFactory))
            return false;
        return true;
    }
}
