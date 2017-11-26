/**
 *
 */
package com.gojek.application;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.gojek.application.filter.CorsFilter;
import com.gojek.application.filter.InternalRequestFilter;
import com.gojek.application.filter.RequestContextFilter;
import com.gojek.application.filter.RequestTrackingFilter;
import com.gojek.application.filter.ResponseTransformationFilter;
import com.gojek.application.metrics.EnhancedMetricRegistry;
import com.gojek.application.swagger.SwaggerBundle;
import com.gojek.application.swagger.SwaggerBundleConfiguration;
import com.gojek.util.serializer.PropertyFilterModule;

import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganesh
 *
 */
public abstract class BaseApplication<T extends BaseConfiguration> extends Application<T> {
    
    private boolean useEnahancedMetricRegistry;
    
    /**
     * Default constructor
     */
    public BaseApplication() {
        this(true);
    }
    
    /**
     * @param useEnahancedMetricRegistry
     */
    public BaseApplication(boolean useEnahancedMetricRegistry) {
        this.useEnahancedMetricRegistry = useEnahancedMetricRegistry;
    }
	
	@Override
	public void initialize(Bootstrap<T> bootstrap) {
		ConfigurationSourceProvider provider = new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false));
    	bootstrap.setConfigurationSourceProvider(provider);
    	
    	MetricRegistry registry = SharedMetricRegistries.getOrCreate("default");
    	if (useEnahancedMetricRegistry) {
    	    registry = new EnhancedMetricRegistry(registry);
    	}
        bootstrap.setMetricRegistry(registry);
    	
		addBundles(bootstrap);
		registerJacksonModules(bootstrap.getObjectMapper());
		super.initialize(bootstrap);
	}
	
	/**
	 * Register the modules to object mapper
	 *
	 * @param mapper
	 */
	protected void registerJacksonModules(ObjectMapper mapper) {
		mapper.registerModule(new PropertyFilterModule());
		mapper.registerModule(new JodaModule());
	}
	
	/**
	 * Add the custom bundle
	 *
	 * @param bootstrap
	 */
	protected void addBundles(Bootstrap<T> bootstrap) {
	    // Set the configuration in the context. This needs to be set before {@link BaseApplication.run(BaseConfiguration, Environment)} is called  
	    bootstrap.addBundle(new ConfiguredBundle<T>() {
	        @Override
	        public void initialize(Bootstrap<?> bootstrap) {
	        }
	        @Override
	        public void run(T configuration, Environment environment) throws Exception {
	            AppContext.init(configuration);
	        }
        });
		bootstrap.addBundle(new SwaggerBundle<T>() {
	        @Override
	        protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(T configuration) {
	            return configuration.getSwaggerBundleConfiguration();
	        }
	    });
	}
	
	/**
	 * Gets the http port from the configuration
	 *
	 * @param configuration
	 * @return
	 */
	protected int getPort(T configuration) {
		int httpPort = 0;
		ServerFactory serverFactory = configuration.getServerFactory();
		if (serverFactory instanceof DefaultServerFactory) {
			for (ConnectorFactory connector : ((DefaultServerFactory) serverFactory).getApplicationConnectors()) {
			    if (connector.getClass().isAssignableFrom(HttpConnectorFactory.class)) {
			        httpPort = ((HttpConnectorFactory) connector).getPort();
			        break;
			    }
			}
		} else if (serverFactory instanceof SimpleServerFactory) {
			HttpConnectorFactory connector = (HttpConnectorFactory) ((SimpleServerFactory)serverFactory).getConnector();
			if (connector.getClass().isAssignableFrom(HttpConnectorFactory.class)) {
			    httpPort = connector.getPort();
			}
		}
		return httpPort;
	}

	@Override
	public void run(T configuration, Environment environment) throws Exception {
		environment.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		environment.jersey().register(ValidationConfigurationContextResolver.class);
		
		registerFilters(configuration, environment);
		registerExceptionMappers(configuration, environment);
		registerManagedObjects(configuration, environment);
		registerResources(configuration, environment);
		registerHealthChecks(configuration, environment);
	}
	
	/**
	 * Registers the filters
	 *
	 * @param environment
	 */
	protected void registerFilters(T configuration, Environment environment) {
		environment.jersey().register(CorsFilter.class);
		environment.jersey().register(RequestTrackingFilter.class);
		environment.jersey().register(new InternalRequestFilter(configuration.getInternalUrlPrefix()));
		environment.jersey().register(RequestContextFilter.class);
		environment.jersey().register(ResponseTransformationFilter.class);
	}

	/**
	 * Registers the exception mappers
	 *
	 * @param environment
	 */
	protected void registerExceptionMappers(T configuration, Environment environment) {
		environment.jersey().register(WebApplicationExceptionMapper.class);
	}
	
	/**
	 * Registers the managed objects
	 *
	 * @param environment
	 */
	protected void registerManagedObjects(T configuration, Environment environment) {
	}
	
	/**
	 * Registers the resources
	 *
	 * @param environment
	 */
	protected void registerResources(T configuration, Environment environment) {
	}
	
	/**
	 * Register health checks
	 *
	 * @param configuration
	 * @param environment
	 */
	protected void registerHealthChecks(T configuration, Environment environment) {
	}
	
	
}
