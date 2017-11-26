/**
 *
 */
package com.gojek.application;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

/**
 * @author ganeshs
 *
 */
public class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

    @Context
    private ResourceContext resourceContext;

    @Override
    public ValidationConfig getContext(final Class<?> type) {
        return new ValidationConfig()
                .constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class));
    }
}
