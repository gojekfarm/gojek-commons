/**
 * 
 */
package com.gojek.cache.redis;

import java.util.Optional;
import java.util.function.Function;

import javax.inject.Singleton;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.cache.CacheConfiguration;
import com.gojek.cache.CacheException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author ganeshs
 *
 */
@Singleton
public class JedisConnection {

    private MyJedisPool pool;
    
    private Optional<ConnectionPoolListener> listener = Optional.empty();

    private static final Logger logger = LoggerFactory.getLogger(JedisConnection.class);
    
    /**
     * @param listener
     */
    public void register(ConnectionPoolListener listener) {
        this.listener = Optional.of(listener);
    }

    /**
     * @param maxConnections
     * @param minConnections
     * @param maxIdleConnections
     * @param commandTimeoutInMs
     * @return
     */
    protected JedisPoolConfig constructPoolConfig(int maxConnections, int minConnections, int maxIdleConnections, long commandTimeoutInMs) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdleConnections);
        config.setMaxTotal(maxConnections);
        config.setMinIdle(minConnections);
        config.setMaxWaitMillis(commandTimeoutInMs);
        return config;
    }
    
    /**
     * @param configuration
     */
    public void init(CacheConfiguration configuration) {
        JedisPoolConfig poolConfig = constructPoolConfig(configuration.getMaxConnections(), configuration.getMinConnections(), configuration.getMaxIdleConnections(), configuration.getCommandTimeoutInMs());
        this.pool = constructPool(configuration, poolConfig);
        if (listener.isPresent()) {
            listener.get().onPoolInitialized(pool.getPool());
        }
    }

    /**
     * @param configuration
     * @param poolConfig
     * @return
     */
    protected MyJedisPool constructPool(CacheConfiguration configuration, JedisPoolConfig poolConfig){
        return new MyJedisPool(poolConfig, configuration.getHost(), configuration.getPort(), configuration.getTimeout(), configuration.getPassword());
    }

    /**
     * 
     */
    public void close() {
        if (this.pool != null) {
            this.pool.close();
            if (listener.isPresent()) {
                listener.get().onPoolDestroyed(((MyJedisPool)pool).getPool());
            }
        }
    }

    /**
     * @param resource
     */
    protected void releaseResource(Jedis resource) {
        resource.close();
    }
    
    /**
     * @return
     */
    protected GenericObjectPool<Jedis> getPool() {
        return ((MyJedisPool) this.pool).getPool();
    }

    protected Jedis getResource() {
        if (this.pool == null) {
            logger.error("Connection is not initialized yet");
            throw new CacheException("Connection is not initialized yet");
        }
        try {
            return this.pool.getResource();
        } catch (Exception e) {
            logger.error("Failed while getting the connection from pool", e);
            throw new CacheException("Failed while getting the connection from pool", e);
        }
    }

    public <T> T execute(Function<Jedis, T> function) {
        Jedis resource = getResource();
        try {
            return function.apply(resource);
        } finally {
            if (resource != null) {
                releaseResource(resource);
            }
        }
    }
    
    /**
     * @author ganeshs
     *
     */
    public static class MyJedisPool extends JedisPool {

        /**
         * @param poolConfig
         * @param host
         * @param port
         * @param timeout
         * @param password
         */
        public MyJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password) {
            super(poolConfig, host, port, timeout, password);
        }

        /**
         * @return
         */
        protected GenericObjectPool<Jedis> getPool() {
            return internalPool;
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
        public void onPoolInitialized(GenericObjectPool<Jedis> pool) {
        }
        
        /**
         * Invoked when the pool is closed
         *
         * @param pool
         */
        public void onPoolDestroyed(GenericObjectPool<Jedis> pool) {
        }
    }
}

