/**
 * 
 */
package com.gojek.cache;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.gojek.cache.redis.JedisConnection;
import com.gojek.util.metrics.GenericObjectPoolGaugeSet;

import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class CacheBundleTest {
    
    private AppConfiguration configuration;
    
    private CacheConfiguration cacheConfiguration;
    
    private JedisConnection connection;
    
    private Environment environment;
    
    private LifecycleEnvironment lifecycleEnvironment;
    
    private MetricRegistry metricRegistry;

    private HealthCheckRegistry healthCheckRegistry;
    
    private CacheBundle<AppConfiguration> bundle;
    
    @BeforeMethod
    public void setup() {
        cacheConfiguration = mock(CacheConfiguration.class);
        configuration = new AppConfiguration(cacheConfiguration);
        connection = new JedisConnection();
        environment = mock(Environment.class);
        lifecycleEnvironment = mock(LifecycleEnvironment.class);
        metricRegistry = mock(MetricRegistry.class);
        healthCheckRegistry = mock(HealthCheckRegistry.class);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(environment.healthChecks()).thenReturn(healthCheckRegistry);
        when(environment.metrics()).thenReturn(metricRegistry);
        bundle = CacheBundle.<AppConfiguration>builder().using(() -> connection).build();
    }

    @Test
    public void shouldRegisterCacheConnectionManager() throws Exception {
        bundle.run(configuration, environment);
        verify(lifecycleEnvironment).manage(new CacheConnectionManager(cacheConfiguration, connection));
    }
    
    @Test
    public void shouldRegisterCacheHealthCheck() throws Exception {
        bundle.run(configuration, environment);
        verify(healthCheckRegistry).register(eq(CacheBundle.HEALTH_NAME), any(CacheHealthCheck.class));
    }
    
    @Test
    public void shouldNotRegisterCacheHealthCheck() throws Exception {
        bundle = CacheBundle.<AppConfiguration>builder().disableHealthCheck().using(() -> connection).build();
        bundle.run(configuration, environment);
        verify(healthCheckRegistry, never()).register(eq(CacheBundle.HEALTH_NAME), any(CacheHealthCheck.class));
    }
    
    @Test
    public void shouldRegisterCachePoolMetrics() throws Exception {
        bundle.run(configuration, environment);
        connection.init(cacheConfiguration);
        verify(metricRegistry).registerAll(any(GenericObjectPoolGaugeSet.class));
    }
    
    @Test
    public void shouldNotRegisterCachePoolMetrics() throws Exception {
        bundle = CacheBundle.<AppConfiguration>builder().disableMetrics().using(() -> connection).build();
        bundle.run(configuration, environment);
        connection.init(cacheConfiguration);
        verify(metricRegistry, never()).registerAll(any(GenericObjectPoolGaugeSet.class));
    }
    
    private static class AppConfiguration extends Configuration implements CacheSupport {
        
        private CacheConfiguration cacheConfiguration;
        
        /**
         * @param cacheConfiguration
         */
        public AppConfiguration(CacheConfiguration cacheConfiguration) {
            this.cacheConfiguration = cacheConfiguration;
        }
        
        @Override
        public CacheConfiguration getCacheConfiguration() {
            return cacheConfiguration;
        }
    }
}
