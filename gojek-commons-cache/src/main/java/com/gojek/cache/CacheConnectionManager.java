/**
 * 
 */
package com.gojek.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.cache.redis.JedisConnection;

import io.dropwizard.lifecycle.Managed;


/**
 * @author ganeshs
 *
 */
public class CacheConnectionManager implements Managed {

    private JedisConnection connection;

    private CacheConfiguration configuration;

    private static final Logger logger = LoggerFactory.getLogger(CacheConnectionManager.class);

    /**
     * @param configuration
     * @param connection
     */
    public CacheConnectionManager(CacheConfiguration configuration, JedisConnection connection) {
        this.configuration = configuration;
        this.connection = connection;
    }

    public void start() {
        try {
            this.connection.init(configuration);
        } catch (Exception e) {
            logger.error("Failed while initializing the jedis connection", e);
            throw new CacheException("Failed while initializing the jedis connection", e);
        }
    }

    public void stop() {
        try {
            connection.close();
        } catch (Exception e) {
            logger.error("Failed to Stop the Connection", e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
        result = prime * result + ((connection == null) ? 0 : connection.hashCode());
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
        CacheConnectionManager other = (CacheConnectionManager) obj;
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
        return true;
    }
}
