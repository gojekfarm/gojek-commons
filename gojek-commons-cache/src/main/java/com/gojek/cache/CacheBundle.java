/**
 * 
 */
package com.gojek.cache;

import java.util.function.Supplier;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.gojek.cache.redis.JedisConnection;
import com.gojek.cache.redis.JedisConnection.ConnectionPoolListener;
import com.gojek.util.metrics.GenericObjectPoolGaugeSet;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.Jedis;

/**
 * @author ganeshs
 *
 */
public class CacheBundle<T extends Configuration & CacheSupport> implements ConfiguredBundle<T> {
    
    private boolean enableHealthCheck;
    
    private boolean enableMetrics;
    
    private Supplier<JedisConnection> supplier;
    
    public static final String HEALTH_NAME = "cache";
    
    public static final String METRIC_NAME = "cache.pool";
    
    /**
     * @param enableHealthCheck
     * @param enableMetrics
     * @param supplier
     */
    private CacheBundle(boolean enableHealthCheck, boolean enableMetrics, Supplier<JedisConnection> supplier) {
        this.enableHealthCheck = enableHealthCheck;
        this.enableMetrics = enableMetrics;
        if (supplier == null) {
            supplier = () -> new JedisConnection();
        }
        this.supplier = supplier;
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        JedisConnection connection = supplier.get();
        connection.register(new ConnectionPoolListener() {
            public void onPoolInitialized(GenericObjectPool<Jedis> pool) {
                if (enableMetrics) {
                    environment.metrics().registerAll(new GenericObjectPoolGaugeSet<>(METRIC_NAME, pool));
                }
            }
        });
        environment.lifecycle().manage(new CacheConnectionManager(configuration.getCacheConfiguration(), connection));
        if (enableHealthCheck) {
            environment.healthChecks().register(HEALTH_NAME, new CacheHealthCheck(connection));
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
    
    /**
     * @return
     */
    public static <T extends Configuration & CacheSupport> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * @author ganeshs
     *
     * @param <T>
     */
    public static class Builder<T extends Configuration & CacheSupport> {
        
        private boolean enableHealthCheck = true;
        
        private boolean enableMetrics = true;
        
        private Supplier<JedisConnection> supplier;
        
        /**
         * @return
         */
        public Builder<T> disableHealthCheck() {
            enableHealthCheck = false;
            return this;
        }
        
        /**
         * @return
         */
        public Builder<T> disableMetrics() {
            enableMetrics = false;
            return this;
        }
        
        /**
         * @param supplier
         * @return
         */
        public Builder<T> using(Supplier<JedisConnection> supplier) {
            this.supplier = supplier;
            return this;
        }
        
        /**
         * @return
         */
        public CacheBundle<T> build() {
            return new CacheBundle<>(enableHealthCheck, enableMetrics, supplier);
        }
    }
}
