/**
 *
 */
package com.gojek.application.swagger;

import java.util.Iterator;
import java.util.Map;

import org.javalite.common.Inflector;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Maps;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;

/**
 * This class takes care of converting the model property names from camelCase to under_score.
 *
 * @author ganeshs
 *
 */
public class UnderscorePropertyNameResolver extends ModelResolver {

    private Map<JavaType, Map<String, Property>> resolvedProperties = Maps.newHashMap();

    /**
     * Default constructor
     */
    public UnderscorePropertyNameResolver() {
        super(Json.mapper());
    }

    @Override
    public Model resolve(JavaType type, ModelConverterContext context, Iterator<ModelConverter> next) {
        Model model = super.resolve(type, context, next);
        if (model != null && model.getProperties() != null) {
            Map<String, Property> properties = resolvedProperties.get(type);
            if (properties == null) {
                properties = Maps.newHashMap();
                for (Property property : model.getProperties().values()) {
                    String name = Inflector.underscore(property.getName());
                    property.setName(name);
                    properties.put(name, property);
                }
                resolvedProperties.put(type, properties);
            }
            model.getProperties().clear();
            model.setProperties(properties);
        }
        return model;
    }
}
