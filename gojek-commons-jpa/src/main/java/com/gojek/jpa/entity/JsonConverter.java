/**
 *
 */
package com.gojek.jpa.entity;

import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.gojek.util.serializer.DefaultJsonSerializer;
import com.gojek.util.serializer.Serializer;

/**
 * @author ganeshs
 *
 */
@Converter
@SuppressWarnings("rawtypes")
public class JsonConverter implements AttributeConverter<Map, String> {
	
	private Serializer serializer;
	
	/**
	 * Default constructor
	 */
	public JsonConverter() {
		this(new DefaultJsonSerializer());
	}
	
	/**
	 * @param serializer
	 */
	public JsonConverter(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public String convertToDatabaseColumn(Map value) {
		return serializer.serialize(value);
	}

	@Override
	public Map convertToEntityAttribute(String value) {
		return serializer.deserialize(value, Map.class);
	}

}
