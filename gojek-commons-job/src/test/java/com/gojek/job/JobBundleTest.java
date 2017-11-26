/**
 * 
 */
package com.gojek.job;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.quartz.spi.JobFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.MetricRegistry;

import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class JobBundleTest {
    
    private Environment environment;
    
    private LifecycleEnvironment lifecycle;
    
    private AppConfiguration config;
    
    private JobManager manager;
    
    @BeforeMethod
    public void setup() {
        environment = mock(Environment.class);
        lifecycle = mock(LifecycleEnvironment.class);
        when(environment.lifecycle()).thenReturn(lifecycle);
        when(environment.metrics()).thenReturn(new MetricRegistry());
        config = new AppConfiguration(new JobConfiguration(), new QuartzConfiguration());
        manager = new JobManager();
    }

    @Test
    public void shouldAddJobManagerToLifeCycle() throws Exception {
        JobBundle<AppConfiguration> bundle = spy(JobBundle.<AppConfiguration>builder().using(() -> manager).build());
        bundle.run(config, environment);
        verify(lifecycle).manage(manager);
    }
    
    @Test
    public void shouldAddJobManagerToServerLifecycle() throws Exception {
        JobManager manager = mock(JobManager.class);
        JobBundle<AppConfiguration> bundle = spy(JobBundle.<AppConfiguration>builder().using(() -> manager).build());
        bundle.run(config, environment);
        verify(lifecycle).addServerLifecycleListener(manager);
    }
    
    @Test
    public void shouldInitJobManger() throws Exception {
        JobFactory factory = mock(JobFactory.class);
        JobBundle<AppConfiguration> bundle = spy(JobBundle.<AppConfiguration>builder().using(() -> manager).with(factory).build());
        bundle.run(config, environment);
        assertNotNull(manager.getTriggerListener());
        assertEquals(manager.getJobFactory(), factory);
    }
    
    private static class AppConfiguration extends Configuration implements JobSupport {
        
        private JobConfiguration jobConfiguration;
        
        private QuartzConfiguration quartzConfiguration;
        
        /**
         * @param jobConfiguration
         * @param quartzConfiguration
         */
        public AppConfiguration(JobConfiguration jobConfiguration, QuartzConfiguration quartzConfiguration) {
            this.jobConfiguration = jobConfiguration;
            this.quartzConfiguration = quartzConfiguration;
        }

        @Override
        public JobConfiguration getJobConfiguration() {
            return jobConfiguration;
        }
        
        @Override
        public QuartzConfiguration getQuartzConfiguration() {
            return quartzConfiguration;
        }
    }
}
