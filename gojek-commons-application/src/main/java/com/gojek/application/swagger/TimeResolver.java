/**
 *
 */
package com.gojek.application.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.Iterator;

import com.fasterxml.jackson.databind.type.SimpleType;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.util.Json;

/**
 * @author ganeshs
 *
 */
public class TimeResolver extends ModelResolver {

	public TimeResolver() {
	    super(Json.mapper());
    }

	@Override
    public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
		if (isTimeType(type)) {
			StringProperty property = new StringProperty();
			property.setDescription("HH:mm:ss");
			return property;
		}
		return super.resolveProperty(type, context, annotations, chain);
    }

	@Override
    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		if (isTimeType(type)) {
			return null;
		}
		return super.resolve(type, context, chain);
    }
	
	/**
	 * @param type
	 * @return
	 */
	protected boolean isTimeType(Type type) {
		return (type instanceof SimpleType) && (((SimpleType)type).getRawClass().equals(Time.class));
	}

}
