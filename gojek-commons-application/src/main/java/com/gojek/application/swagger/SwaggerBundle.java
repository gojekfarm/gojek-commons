/**
 *
 */
package com.gojek.application.swagger;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.swagger.converter.ModelConverters;
import io.swagger.jackson.ModelResolver;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

/**
 * A {@link io.dropwizard.ConfiguredBundle} that provides hassle-free configuration of Swagger and Swagger UI on top of Dropwizard.
 */
public abstract class SwaggerBundle<T extends Configuration> implements ConfiguredBundle<T> {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
        ModelConverters.getInstance().addConverter(new ModelResolver(bootstrap.getObjectMapper()));
        ModelConverters.getInstance().addConverter(new TimeResolver());
        ModelConverters.getInstance().addConverter(new UnderscorePropertyNameResolver());
    }

    @Override
    public void run(T config, Environment environment) throws Exception {
        final SwaggerBundleConfiguration configuration = getSwaggerBundleConfiguration(config);
        if (configuration == null) {
            throw new IllegalStateException("You need to provide an instance of SwaggerBundleConfiguration");
        }

        if (!configuration.isEnabled()) {
            return;
        }

        final ConfigurationHelper configurationHelper = new ConfigurationHelper(config, configuration);
        new AssetsBundle("/swagger-static", configurationHelper.getSwaggerUriPath(), null, "swagger-assets").run(environment);

        configuration.build(configurationHelper.getUrlPattern());

        environment.jersey().register(new ApiListingResource());
        environment.jersey().register(new SwaggerSerializers());
        environment.jersey().register(new SwaggerResource(configurationHelper.getUrlPattern(), configuration.getSwaggerViewConfiguration()));
    }

    protected abstract SwaggerBundleConfiguration getSwaggerBundleConfiguration(T configuration);
}
