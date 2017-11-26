/**
 *
 */
package com.gojek.util.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializer extends AbstractJacksonSerializer {

	/**
	 * Default Constructor.
	 */
	public DefaultJsonSerializer() {
		this(new ObjectMapper(), PropertyNamingStrategy.SNAKE_CASE);
	}

	/**
	 * @param mapper
	 *            ObjectMapper.
	 * @param propertyNamingStrategy
	 *            {@link PropertyNamingStrategy}
	 */
	public DefaultJsonSerializer(final ObjectMapper mapper, final PropertyNamingStrategy propertyNamingStrategy) {
		super(mapper, propertyNamingStrategy);
	}
}
