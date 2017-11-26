/**
 * 
 */
package com.gojek.cache;

import com.codahale.metrics.health.HealthCheck;
import com.gojek.cache.redis.JedisConnection;

/**
 * @author ganeshs
 *
 */
public class CacheHealthCheck extends HealthCheck {
    
    private JedisConnection connection;
    
    /**
     * @param connection
     */
    public CacheHealthCheck(JedisConnection connection) {
        this.connection = connection;
    }

    @Override
    protected Result check() throws Exception {
        return connection.execute(jedis -> {
            return jedis.isConnected() ? Result.healthy() : Result.unhealthy("Redis is down");
        });
    }

}
