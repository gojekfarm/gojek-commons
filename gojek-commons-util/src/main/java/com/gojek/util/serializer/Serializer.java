/**
 *
 */
package com.gojek.util.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

/**
 * @author ganeshs
 *
 */
public abstract class Serializer {
	
	public final static Serializer DEFAULT_JSON_SERIALIZER = new DefaultJsonSerializer();
	
	public final static Serializer DEFAULT_YAML_SERIALIZER = new DefaultYamlSerializer();
	
	private static final Logger logger = LoggerFactory.getLogger(Serializer.class);

	public String serialize(Object object) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		serialize(object, stream);
		return new String(stream.toByteArray(), Charsets.UTF_8);
	}
	
	public abstract void serialize(Object object, OutputStream stream);
	
	public <T> T deserialize(String content, Class<T> targetClass) {
		return deserialize(new ByteArrayInputStream(content.getBytes(Charsets.UTF_8)), targetClass);
	}
	
	public abstract <T> T deserialize(InputStream stream, Class<T> type);
	
	public abstract <T> T deserialize(String data, Class<?> type, Class<?> parameterTypes);
	
	public abstract <T> T deserialize(InputStream stream, Class<?> type, Class<?> parameterTypes);
	
	/**
	 * @param stream
	 */
	protected void closeStream(Closeable stream) {
		try {
			stream.close();
		} catch (Exception e) {
			logger.trace("Failed while closing the stream", e);
		}
	}
	
}
