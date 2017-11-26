/**
 * 
 */
package com.gojek.guice;

import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Path;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.jersey.server.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.guice.util.GuiceUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.palominolabs.metrics.guice.MetricsInstrumentationModule;
import com.squarespace.jersey2.guice.JerseyGuiceModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {
    
    private Class<T> configurationClass;
    
    private T configuration;
    
    private Stage stage;
    
    private List<Module> modules = Lists.newArrayList();
    
    private Injector injector;
    
    private static final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);
    
    /**
     * @param configurationClass
     * @param modules
     */
    public GuiceBundle(Stage stage, Class<T> configurationClass, List<Module> modules) {
        Preconditions.checkNotNull(stage);
        Preconditions.checkNotNull(configurationClass);
        this.stage = stage;
        this.configurationClass = configurationClass;
        if (modules != null) {
            this.modules = modules;
        }
    }
    
    /**
     * @return the configurationClass
     */
    public Class<T> getConfigurationClass() {
        return configurationClass;
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @return the modules
     */
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(configurationClass).toProvider(new EnvironmentProvider());
            }
        });
        this.modules.add(new MetricsInstrumentationModule(bootstrap.getMetricRegistry()));
        this.modules.add(new ServletModule());
        injector = Guice.createInjector(stage, modules);
        JerseyGuiceUtils.install(new ServiceLocatorGenerator() {
            @Override
            public ServiceLocator create(String name, ServiceLocator parent) {
                if (!name.startsWith("__HK2_Generated_")) {
                    return null;
                }

                return injector.createChildInjector(new JerseyGuiceModule(name))
                        .getInstance(ServiceLocator.class);
            }
        });
        GuiceUtil.load(injector);
    }
    
    @Override
    public void run(T configuration, Environment environment) throws Exception {
        this.configuration = configuration;
        registerGuiceBound(injector, environment.jersey());
        registerGuiceFilter(environment);
        environment.servlets().addServletListeners(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injector;
            }
        });
    }
    
    /**
     * Registers any Guice-bound providers or root resources.
     */
    private static void registerGuiceBound(Injector injector, final JerseyEnvironment environment) {
        while (injector != null) {
            for (Key<?> key : injector.getBindings().keySet()) {
                Type type = key.getTypeLiteral().getType();
                if (type instanceof Class) {
                    Class<?> c = (Class) type;
                    if (isProviderClass(c)) {
                        logger.info("Registering {} as a provider class", c.getName());
                        environment.register(c);
                    } else if (isRootResourceClass(c)) {
                        // Jersey rejects resources that it doesn't think are acceptable. Including abstract classes and interfaces, even if there is a valid Guice binding.
                        if (Resource.isAcceptable(c)) {
                            logger.info("Registering {} as a root resource class", c.getName());
                            environment.register(c);
                        } else {
                            logger.warn("Class {} was not registered as a resource. Bind a concrete implementation instead.", c.getName());
                        }
                    }

                }
            }
            injector = injector.getParent();
        }
    }

    private static boolean isProviderClass(Class<?> c) {
        return c != null && c.isAnnotationPresent(javax.ws.rs.ext.Provider.class);
    }

    private static boolean isRootResourceClass(Class<?> c) {
        if (c == null) {
            return false;
        }

        if (c.isAnnotationPresent(Path.class)) {
            return true;
        }

        for (Class i : c.getInterfaces()) {
            if (i.isAnnotationPresent(Path.class)) {
                return true;
            }
        }

        return false;
    }

    private static void registerGuiceFilter(Environment environment) {
        environment.servlets().addFilter("Guice Filter", GuiceFilter.class).addMappingForUrlPatterns(null, false, "/*");
    }
    
    /**
     * @author ganeshs
     *
     * @param <T>
     */
    public static class Builder<T extends Configuration> {
        
        private List<Module> modules = Lists.newArrayList();
        
        private Class<T> configClass;
        
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
        public GuiceBundle<T> build() {
            return build(Stage.PRODUCTION);
        }
        
        /**
         * @param stage
         * @return
         */
        public GuiceBundle<T> build(Stage stage) {
            return new GuiceBundle<>(stage, configClass, modules);
        }
    }
    
    private class EnvironmentProvider implements Provider<T> {
        @Override
        public T get() {
            if (configuration == null) {
                throw new ProvisionException("Dropwizard environment is not yet intialized");
            }
            return configuration;
        }
    }
}
