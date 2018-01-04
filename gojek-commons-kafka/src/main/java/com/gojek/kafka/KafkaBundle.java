/**
 * 
 */
package com.gojek.kafka;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.gojek.core.event.EventHandler;
import com.gojek.kafka.event.KafkaConsumerContainer;
import com.google.common.collect.Maps;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class KafkaBundle<T extends Configuration & KafkaSupport> implements ConfiguredBundle<T> {
    
    private boolean enableHealthCheck;
    
    private boolean enableMetrics;
    
    private Function<T, Map<KafkaConsumerConfiguration, EventHandler<?>>> consumers;
    
    public static final String HEALTH_NAME = "kafka";
    
    /**
     * @param enableHealthCheck
     * @param enableMetrics
     * @param consumers
     */
    private KafkaBundle(boolean enableHealthCheck, boolean enableMetrics, Function<T, Map<KafkaConsumerConfiguration, EventHandler<?>>> consumers) {
        this.enableHealthCheck = enableHealthCheck;
        this.enableMetrics = enableMetrics;
        if (consumers == null) {
            consumers = (configuration) -> Maps.newHashMap();
        }
        this.consumers = consumers;
    }
    
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void run(T configuration, Environment environment) throws Exception {
        for (Entry<KafkaConsumerConfiguration, EventHandler<?>> entry : consumers.apply(configuration).entrySet()) {
            environment.lifecycle().manage(new KafkaConsumerContainer(configuration.getKafkaConfiguration().getConfigs(), entry.getKey(), entry.getValue()));
        }
        if (enableHealthCheck) {
            environment.healthChecks().register(HEALTH_NAME, new KafkaHealthCheck());
        }
    }
    
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
    
    /**
     * @return
     */
    public static <T extends Configuration & KafkaSupport> Builder<T> builder() {
        return new Builder<T>();
    }

    /**
     * @author ganeshs
     *
     * @param <T>
     */
    public static class Builder<T extends Configuration & KafkaSupport> {
        
        private boolean enableHealthCheck = true;
        
        private boolean enableMetrics = true;
        
        private Function<T, Map<KafkaConsumerConfiguration, EventHandler<?>>> consumers;
        
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
         * @param consumers
         * @return
         */
        public Builder<T> with(Function<T, Map<KafkaConsumerConfiguration, EventHandler<?>>> consumers) {
            this.consumers = consumers;
            return this;
        }
        
        /**
         * @return
         */
        public KafkaBundle<T> build() {
            return new KafkaBundle<>(enableHealthCheck, enableMetrics, consumers);
        }
    }
}
