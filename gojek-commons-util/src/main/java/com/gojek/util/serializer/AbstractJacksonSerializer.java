/**
 *
 */
package com.gojek.util.serializer;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractJacksonSerializer extends Serializer {

	private ObjectMapper mapper;

	private final PropertyNamingStrategy propertyNamingStrategy;

	private static final Logger logger = LoggerFactory.getLogger(AbstractJacksonSerializer.class);

	protected AbstractJacksonSerializer(ObjectMapper mapper, final PropertyNamingStrategy propertyNamingStrategy) {
		this.mapper = mapper;
		this.propertyNamingStrategy = propertyNamingStrategy;
		init();
	}

	protected void init() {
		mapper.setSubtypeResolver(new DiscoverableSubtypeResolver());
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.GETTER, Visibility.PROTECTED_AND_PUBLIC);
		mapper.setVisibility(PropertyAccessor.SETTER, Visibility.PROTECTED_AND_PUBLIC);
		if (propertyNamingStrategy != null) {
			mapper.setPropertyNamingStrategy(propertyNamingStrategy);
		}
		registerModules(mapper);
		mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected void registerModules(ObjectMapper mapper) {
		mapper.registerModule(new JodaModule());
		mapper.registerModule(new PropertyFilterModule());
	}

	public <T> T deserialize(InputStream stream, Class<T> targetClass) {
		try {
			return mapper.readValue(stream, targetClass);
		} catch (Exception e) {
			throw new SerializerException("Failed while deserializing the buffer to type - " + targetClass, e);
		} finally {
			closeStream(stream);
		}
	}

	@Override
	public void serialize(Object object, OutputStream stream) {
		try {
			mapper.writeValue(stream, object);
		} catch (Exception e) {
			logger.error("Unable to serialize object", e);
			throw new SerializerException("Failed while serializing the object", e);
		} finally {
			closeStream(stream);
		}
	}

	/**
	 * @return the mapper
	 */
	protected ObjectMapper getMapper() {
		return mapper;
	}

	public <T> T deserialize(String data, JavaType type) {
		if (data == null) {
			return null;
		}
		try {
			return mapper.readValue(data, type);
		} catch (Exception e) {
			logger.error("Failed while deserializing the data to type - " + type, e);
			throw new SerializerException("Failed while deserializing the data to type - " + type, e);
		}
	}

	public <T> T deserialize(InputStream stream, JavaType type) {
		if (stream == null) {
			return null;
		}
		try {
			return mapper.readValue(stream, type);
		} catch (Exception e) {
			logger.error("Failed while deserializing the stream to type - " + type, e);
			throw new SerializerException("Failed while deserializing the stream to type - " + type, e);
		}
	}

	@Override
	public <T> T deserialize(String data, Class<?> type, Class<?> parameterTypes) {
		JavaType javaType = mapper.getTypeFactory().constructParametricType(type, parameterTypes);
		return deserialize(data, javaType);
	}

	@Override
	public <T> T deserialize(InputStream stream, Class<?> type, Class<?> parameterTypes) {
		JavaType javaType = mapper.getTypeFactory().constructParametricType(type, parameterTypes);
		return deserialize(stream, javaType);
	}
}