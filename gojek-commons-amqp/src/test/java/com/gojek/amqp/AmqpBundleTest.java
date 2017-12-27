/**
 * 
 */
package com.gojek.amqp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.gojek.amqp.event.EventHandler;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.Destination;
import com.gojek.core.event.Event;
import com.gojek.util.metrics.GenericObjectPoolGaugeSet;

import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class AmqpBundleTest {
    
    private AmqpConfiguration amqpConfiguration;
    
    private AmqpConnection connection;
    
    private AppConfiguration configuration;
    
    private Environment environment;
    
    private LifecycleEnvironment lifecycleEnvironment;

    private HealthCheckRegistry healthCheckRegistry;
    
    private MetricRegistry metricRegistry;
    
    private AmqpBundle<AppConfiguration> bundle;
    
    @BeforeMethod
    public void setup() {
        amqpConfiguration = mock(AmqpConfiguration.class);
        connection = new AmqpConnection();
        configuration = new AppConfiguration(amqpConfiguration);
        environment = mock(Environment.class);
        lifecycleEnvironment = mock(LifecycleEnvironment.class);
        healthCheckRegistry = mock(HealthCheckRegistry.class);
        metricRegistry = mock(MetricRegistry.class);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(environment.healthChecks()).thenReturn(healthCheckRegistry);
        when(environment.metrics()).thenReturn(metricRegistry);
        bundle = AmqpBundle.<AppConfiguration>builder().using(() -> connection).build();
    }

    @Test
    public void shouldRegisterAmqpConnectionManager() throws Exception {
        bundle.run(configuration, environment);
        verify(lifecycleEnvironment).manage(new AmqpConnectionManager(amqpConfiguration, connection));
    }
    
    @Test
    public void shouldRegisterAmqpConsumerContainerManager() throws Exception {
        Map<ConsumerConfiguration, EventHandler<?>> consumerHandlers = Maps.newHashMap();
        ConsumerConfiguration consumerConfiguration = new ConsumerConfiguration("some-queue", mock(Destination.class));
        EventHandler<Event> eventHandler = mock(EventHandler.class);
        consumerHandlers.put(consumerConfiguration, eventHandler);
        bundle = AmqpBundle.<AppConfiguration>builder().using(() -> connection).with((configuration -> consumerHandlers)).build();
        bundle.run(configuration, environment);
        verify(lifecycleEnvironment).manage(new AmqpConsumerContainer<Event>(consumerConfiguration, eventHandler, connection));
    }
    
    @Test
    public void shouldRegisterAmqpHealthCheck() throws Exception {
        bundle.run(configuration, environment);
        verify(healthCheckRegistry).register(eq(AmqpBundle.HEALTH_NAME), any(AmqpHealthCheck.class));
    }
    
    @Test
    public void shouldNotRegisterAmqpHealthCheck() throws Exception {
        bundle = AmqpBundle.<AppConfiguration>builder().using(() -> connection).disableHealthCheck().build();
        bundle.run(configuration, environment);
        verify(healthCheckRegistry, never()).register(eq(AmqpBundle.HEALTH_NAME), any(AmqpHealthCheck.class));
    }
    
    @Test
    public void shouldRegisterAmqpPoolMetrics() throws Exception {
        bundle.run(configuration, environment);
        connection.init(null, 1, 1, 1);
        verify(metricRegistry).registerAll(any(GenericObjectPoolGaugeSet.class));
    }
    
    @Test
    public void shouldNotRegisterAmqpPoolMetrics() throws Exception {
        bundle = AmqpBundle.<AppConfiguration>builder().using(() -> connection).disableMetrics().build();
        bundle.run(configuration, environment);
        connection.init(null, 1, 1, 1);
        verify(metricRegistry, never()).registerAll(any(GenericObjectPoolGaugeSet.class));
    }
    
    private static class AppConfiguration extends Configuration implements AmqpSupport {
        
        private AmqpConfiguration amqpConfiguration;
        
        /**
         * @param amqpConfiguration
         */
        public AppConfiguration(AmqpConfiguration amqpConfiguration) {
            this.amqpConfiguration = amqpConfiguration;
        }
        
        @Override
        public AmqpConfiguration getAmqpConfiguration() {
            return amqpConfiguration;
        }
    }
}
