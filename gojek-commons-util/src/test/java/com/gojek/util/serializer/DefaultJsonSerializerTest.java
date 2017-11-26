/**
 *
 */
package com.gojek.util.serializer;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializerTest {

	@Test
	public void shouldSerializeObjectToJson() {
		AnotherDummy anotherDummy = new AnotherDummy(new Dummy("test123", 1234L));
		DefaultJsonSerializer serializer = new DefaultJsonSerializer();
		assertEquals(serializer.serialize(anotherDummy), "{\"dummy\":{\"string_value\":\"test123\",\"long_value\":1234}}");
	}

	@Test
	public void shouldDeserializeJsonToObject() {
		DefaultJsonSerializer serializer = new DefaultJsonSerializer();
		AnotherDummy anotherDummy = serializer.deserialize("{\"dummy\":{\"string_value\":\"test123\",\"long_value\":1234}}", AnotherDummy.class);
		assertEquals(anotherDummy.getDummy().getStringValue(), "test123");
		assertEquals(anotherDummy.getDummy().getLongValue(), Long.valueOf(1234));
	}

	@Test
	public void shouldDeserializeStreamToObject() {
		DefaultJsonSerializer serializer = new DefaultJsonSerializer();
		String input = "{\"dummy\":{\"string_value\":\"test123\",\"long_value\":1234}}";
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
		AnotherDummy anotherDummy = serializer.deserialize(byteArrayInputStream, AnotherDummy.class);
		assertEquals(anotherDummy.getDummy().getStringValue(), "test123");
		assertEquals(anotherDummy.getDummy().getLongValue(), Long.valueOf(1234));
	}

	public static class AnotherDummy {

		private Dummy dummy;

		public AnotherDummy() {
		}

		/**
		 * @param dummy
		 */
		public AnotherDummy(Dummy dummy) {
			this.dummy = dummy;
		}

		/**
		 * @return the dummy
		 */
		public Dummy getDummy() {
			return dummy;
		}

		/**
		 * @param dummy
		 *            the dummy to set
		 */
		public void setDummy(Dummy dummy) {
			this.dummy = dummy;
		}
	}

	/**
	 * @author ganeshs
	 *
	 */
	public static class Dummy {

		private String stringValue;

		private Long longValue;

		public Dummy() {
		}

		/**
		 * @param stringValue
		 * @param longValue
		 */
		public Dummy(String stringValue, Long longValue) {
			this.stringValue = stringValue;
			this.longValue = longValue;
		}

		/**
		 * @return the stringValue
		 */
		public String getStringValue() {
			return stringValue;
		}

		/**
		 * @param stringValue
		 *            the stringValue to set
		 */
		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}

		/**
		 * @return the longValue
		 */
		public Long getLongValue() {
			return longValue;
		}

		/**
		 * @param longValue
		 *            the longValue to set
		 */
		public void setLongValue(Long longValue) {
			this.longValue = longValue;
		}
	}
}
