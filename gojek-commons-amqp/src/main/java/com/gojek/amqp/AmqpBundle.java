/**
 * 
 */
package com.gojek.amqp;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.gojek.amqp.AmqpConnection.ConnectionPoolListener;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.EventHandler;
import com.gojek.util.metrics.GenericObjectPoolGaugeSet;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class AmqpBundle<T extends Configuration & AmqpSupport> implements ConfiguredBundle<T> {
    
    private boolean enableHealthCheck;
    
    private boolean enableMetrics;
    
    private Supplier<AmqpConnection> supplier;
    
    private Function<T, Map<ConsumerConfiguration, EventHandler<?>>> consumers;
    
    public static final String HEALTH_NAME = "amqp";
    
    public static final String METRIC_NAME = "amqp.pool";
    
    /**
     * @param enableHealthCheck
     * @param enableMetrics
     * @param supplier
     * @param consumers
     */
    private AmqpBundle(boolean enableHealthCheck, boolean enableMetrics, Supplier<AmqpConnection> supplier, Function<T, Map<ConsumerConfiguration, EventHandler<?>>> consumers) {
        this.enableHealthCheck = enableHealthCheck;
        this.enableMetrics = enableMetrics;
        if (supplier == null) {
            supplier = () -> new AmqpConnection();
        }
        if (consumers == null) {
            consumers = (configuration) -> Maps.newHashMap();
        }
        this.supplier = supplier;
        this.consumers = consumers;
    }
    
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void run(T configuration, Environment environment) throws Exception {
        AmqpConnection connection = supplier.get();
        connection.register(new ConnectionPoolListener() {
            public void onPoolInitialized(GenericObjectPool<Channel> pool) {
                if (enableMetrics) {
                    environment.metrics().registerAll(new GenericObjectPoolGaugeSet<>(METRIC_NAME, pool));
                }
            }
        });
        environment.lifecycle().manage(new AmqpConnectionManager(configuration.getAmqpConfiguration(), connection));
        for (Entry<ConsumerConfiguration, EventHandler<?>> entry : consumers.apply(configuration).entrySet()) {
            environment.lifecycle().manage(new AmqpConsumerContainer(entry.getKey(), entry.getValue(), connection));
        }
        if (enableHealthCheck) {
            environment.healthChecks().register(HEALTH_NAME, new AmqpHealthCheck(connection));
        }
    }
    
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
    
    /**
     * @return
     */
    public static <T extends Configuration & AmqpSupport> Builder<T> builder() {
        return new Builder<T>();
    }

    /**
     * @author ganeshs
     *
     * @param <T>
     */
    public static class Builder<T extends Configuration & AmqpSupport> {
        
        private boolean enableHealthCheck = true;
        
        private boolean enableMetrics = true;
        
        private Supplier<AmqpConnection> supplier;
        
        private Function<T, Map<ConsumerConfiguration, EventHandler<?>>> consumers;
        
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
        public Builder<T> using(Supplier<AmqpConnection> supplier) {
            this.supplier = supplier;
            return this;
        }
        
        /**
         * @param consumers
         * @return
         */
        public Builder<T> with(Function<T, Map<ConsumerConfiguration, EventHandler<?>>> consumers) {
            this.consumers = consumers;
            return this;
        }
        
        /**
         * @return
         */
        public AmqpBundle<T> build() {
            return new AmqpBundle<>(enableHealthCheck, enableMetrics, supplier, consumers);
        }
    }
}
