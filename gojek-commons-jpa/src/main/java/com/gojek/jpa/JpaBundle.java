/**
 * 
 */
package com.gojek.jpa;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.activejpa.jpa.EntityManagerProvider;
import org.activejpa.jpa.JPA;
import org.activejpa.utils.OpenSessionInViewFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.gojek.jpa.exceptions.ConstraintViolationExceptionHandler;
import com.gojek.jpa.exceptions.RollbackExceptionHandler;
import com.gojek.jpa.metrics.C3p0PoolGaugeSet;
import com.gojek.jpa.metrics.DatabaseHealthCheck;
import com.gojek.jpa.metrics.PooledDataSourceProxy;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class JpaBundle<T extends Configuration & JpaSupport> implements ConfiguredBundle<T> {
    
    private String persistenceUnit;
    
    private boolean enableHealthCheck;
    
    private boolean enableMetrics;
    
    private static final Logger logger = LoggerFactory.getLogger(JpaBundle.class);
    
    /**
     * @param persistenceUnit
     * @param enableHealthCheck
     * @param enableMetrics
     */
    public JpaBundle(String persistenceUnit, boolean enableHealthCheck, boolean enableMetrics) {
        this.persistenceUnit = persistenceUnit;
        this.enableHealthCheck = enableHealthCheck;
        this.enableMetrics = enableMetrics;
    }
    
    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.lifecycle().manage(createManagedJpa(configuration, environment));
        registerSessionFilters(configuration, environment);
        if (enableHealthCheck) {
            registerHealthChecks(configuration, environment);
        }
        registerExceptionMappers(configuration, environment);
    }
    
    /**
     * @param configuration
     * @param environment
     * @return
     */
    protected Callable<Void> createOnStartCallable(T configuration, Environment environment) {
        return () -> {
            if (enableMetrics) {
                registerMerics(configuration, environment);
            }
            return null;
        };
    }
    
    /**
     * @param configuration
     * @param environment
     * @return
     */
    protected ManagedJpa createManagedJpa(T configuration, Environment environment) {
        return new ManagedJpa(this.persistenceUnit, createOnStartCallable(configuration, environment));
    }
    
    /**
     * @param configuration
     * @param environment
     */
    protected void registerSessionFilters(T configuration, Environment environment) {
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("OSIVFilter", OpenSessionInViewFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter = environment.admin().addFilter("OSIVFilter", OpenSessionInViewFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
    
    /**
     * @param configuration
     * @param environment
     */
    private void registerExceptionMappers(T configuration, Environment environment) {
        environment.jersey().register(ConstraintViolationExceptionHandler.class);
        environment.jersey().register(RollbackExceptionHandler.class);
    }
    
    /**
     * @param configuration
     * @param environment
     */
    private void registerHealthChecks(T configuration, Environment environment) {
        environment.healthChecks().register("database", new DatabaseHealthCheck());
    }
    
    private void registerMerics(T configuration, Environment environment) {
        EntityManagerProvider provider = JPA.instance.getDefaultConfig().getEntityManagerProvider();
        environment.metrics().registerAll(new C3p0PoolGaugeSet(new PooledDataSourceProxy(provider)));
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        logger.info("Initializing the active jpa module");
        ActiveJpaAgentLoader.instance().loadAgent();
        
        bootstrap.addBundle((ConfiguredBundle) new MigrationsBundle<T>());
        
        // register hibernate module
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        bootstrap.getObjectMapper().registerModule(module);
    }
    
    /**
     * @return
     */
    public static <T extends Configuration & JpaSupport> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * @author ganeshs
     *
     */
    public static class Builder<T extends Configuration & JpaSupport> {
        
        private String persistenceUnit;
        
        private boolean enableMetrics = true;
        
        private boolean enableHealthCheck = true;
        
        /**
         * @return
         */
        public Builder<T> disableHealthCheck() {
            this.enableHealthCheck = false;
            return this;
        }
        
        /**
         * @return
         */
        public Builder<T> disableMetrics() {
            this.enableMetrics = false;
            return this;
        }
        
        /**
         * @param persistenceUnit
         * @return
         */
        public Builder<T> with(String persistenceUnit) {
            this.persistenceUnit = persistenceUnit;
            return this;
        }
        
        /**
         * @return
         */
        public JpaBundle<T> build() {
            return new JpaBundle<>(persistenceUnit, enableHealthCheck, enableMetrics);
        }
    }
}
