/**
 * 
 */
package com.gojek.guice.jpa;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import com.gojek.core.CoreException;
import com.gojek.guice.GuiceBundle;
import com.gojek.guice.ManagedGuiceJpa;
import com.gojek.guice.util.GuiceUtil;
import com.gojek.jpa.JpaBundle;
import com.gojek.jpa.JpaSupport;
import com.gojek.jpa.ManagedJpa;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.persist.jpa.JpaPersistModule;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class GuiceJpaBundle<T extends Configuration & JpaSupport> extends GuiceBundle<T> {
    
    private JpaPersistModule jpaPersistModule;
    
    private String configFile;
    
    private String persistenceUnit;
    
    private boolean enableMetrics = true;
    
    private boolean enableHealthCheck = true;

    /**
     * @param configFile
     * @param persistenceUnit
     * @param enableMetrics
     * @param enableHealthCheck
     * @param stage
     * @param configClass
     * @param modules
     */
    public GuiceJpaBundle(String configFile, String persistenceUnit, boolean enableHealthCheck, boolean enableMetrics, Stage stage, Class<T> configClass, List<Module> modules) {
        super(stage, configClass, modules);
        Preconditions.checkNotNull(configFile);
        Preconditions.checkNotNull(persistenceUnit);
        this.configFile = configFile;
        this.persistenceUnit = persistenceUnit;
        this.enableHealthCheck = enableHealthCheck;
        this.enableMetrics = enableMetrics;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.jpaPersistModule = new JpaPersistModule(this.persistenceUnit);
        T config = createConfiguration((Bootstrap<T>) bootstrap);
        this.jpaPersistModule.properties(config.getJpaConfiguration().toAttributes());
        getModules().add(jpaPersistModule);
        super.initialize(bootstrap);
        bootstrap.addBundle((ConfiguredBundle) new JpaBundle<T>(persistenceUnit, enableHealthCheck, enableMetrics) {
            @Override
            protected ManagedJpa createManagedJpa(T configuration, Environment environment) {
                return new ManagedGuiceJpa(persistenceUnit, createOnStartCallable(configuration, environment));
            }
        });
    }
    
    @Override
    public void run(T configuration, Environment environment) throws Exception {
        super.run(configuration, environment);
        environment.lifecycle().manage(GuiceUtil.getInstance(ManagedPersistService.class));
        registerSessionFilters(configuration, environment);
    }
    
    protected void registerSessionFilters(T configuration, Environment environment) {
        OSIVPersistFilter osivPersitFilter = GuiceUtil.getInstance(OSIVPersistFilter.class);
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("osivPersistFilter", osivPersitFilter);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        // admin mapping
        filter = environment.admin().addFilter("osivPersistFilter", osivPersitFilter);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
    
    /**
     * @param configFile
     * @return
     */
    public static <T extends Configuration & JpaSupport> Builder<T> builder(String configFile) {
        return new Builder<T>(configFile);
    }
    
    private T createConfiguration(Bootstrap<T> bootstrap) {
        ConfigurationFactory<T> factory = bootstrap.getConfigurationFactoryFactory().create(getConfigurationClass(), bootstrap.getValidatorFactory().getValidator(), bootstrap.getObjectMapper(), "dw");
        try {
            return factory.build(bootstrap.getConfigurationSourceProvider(), configFile);
        } catch (ConfigurationException | IOException e) {
            throw new CoreException("Failed while creating the configuration object", e);
        }
    }
    
    /**
     * @author ganeshs
     *
     * @param <T>
     */
    public static class Builder<T extends Configuration & JpaSupport> {
        
        private String configFile;
        
        private String persistenceUnit;
        
        private boolean enableMetrics = true;
        
        private boolean enableHealthCheck = true;
        
        private List<Module> modules = Lists.newArrayList();
        
        private Class<T> configClass;
        
        /**
         * @param configFile
         */
        public Builder(String configFile) {
            this.configFile = configFile;
        }
        
        /**
         * @return the modules
         */
        protected List<Module> getModules() {
            return modules;
        }

        /**
         * @return the configClass
         */
        protected Class<T> getConfigClass() {
            return configClass;
        }

        /**
         * @param module
         * @return
         */
        public Builder<T> addModule(Module module) {
            this.modules.add(module);
            return this;
        }
        
        /**
         * @param configClass
         * @return
         */
        public Builder<T> using(Class<T> configClass) {
            this.configClass = configClass;
            return this;
        }
        
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
        public GuiceJpaBundle<T> build() {
            return build(Stage.PRODUCTION);
        }
        
        /**
         * @param stage
         * @return
         */
        public GuiceJpaBundle<T> build(Stage stage) {
            return new GuiceJpaBundle<>(configFile, persistenceUnit, enableHealthCheck, enableMetrics, stage, getConfigClass(), getModules());
        }
    }
}
