/**
 *
 */
package com.gojek.jpa.entity;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JsonConverterTest {

	@Test
	public void shouldConvertToDatabaseColumn() {
		JsonConverter converter = new JsonConverter();
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "value");
		assertEquals(converter.convertToDatabaseColumn(map), "{\"name\":\"value\"}");
	}
	
	@Test
	public void shouldConvertToEntityAttribute() {
		JsonConverter converter = new JsonConverter();
		Map<String, String> map = converter.convertToEntityAttribute("{\"name\":\"value\"}");
		assertEquals(map.get("name"), "value");
	}
}
