/**
 * 
 */
package com.gojek.job;

import java.util.function.Supplier;

import org.quartz.spi.JobFactory;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class JobBundle<T extends Configuration & JobSupport> implements ConfiguredBundle<T> {
    
    private JobFactory jobFactory;
    
    private Supplier<JobManager> supplier;
    
    /**
     * @param jobFactory
     */
    public JobBundle(JobFactory jobFactory, Supplier<JobManager> supplier) {
        this.jobFactory = jobFactory;
        if (supplier == null) {
            supplier = () -> new JobManager();
        }
        this.supplier = supplier;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
    
    @Override
    public void run(T configuration, Environment environment) throws Exception {
        JobMetricsCollector collector = new JobMetricsCollector();
        JobManager manager = supplier.get();
        manager.init(configuration.getQuartzConfiguration(), configuration.getJobConfiguration(), this.jobFactory, collector);
        environment.lifecycle().manage(manager);
        environment.lifecycle().addServerLifecycleListener(manager);
        collector.init(manager, environment.metrics());
    }
    
    /**
     * @return
     */
    public static <T extends Configuration & JobSupport> Builder<T> builder() {
        return new Builder<>();
    }
    
    public static class Builder<T extends Configuration & JobSupport> {
        
        private JobFactory jobFactory;
        
        private Supplier<JobManager> supplier;
        
        /**
         * @param supplier
         * @return
         */
        public Builder<T> using(Supplier<JobManager> supplier) {
            this.supplier = supplier;
            return this;
        }
        
        /**
         * @param factory
         * @return
         */
        public Builder<T> with(JobFactory factory) {
            this.jobFactory = factory;
            return this;
        }
        
        /**
         * @return
         */
        public JobBundle<T> build() {
            return new JobBundle<T>(this.jobFactory, this.supplier);
        }
    }
}
