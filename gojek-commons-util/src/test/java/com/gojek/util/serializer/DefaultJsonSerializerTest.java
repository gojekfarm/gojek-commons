/**
 *
 */
package com.gojek.util.serializer;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.time.Instant;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializerTest {

	@Test
	public void shouldSerializeObjectToJson() {
		Instant instant = Instant.ofEpochMilli(92554380000L);
		AnotherDummy anotherDummy = new AnotherDummy(new Dummy("test123", 1234L, instant));
		DefaultJsonSerializer serializer = new DefaultJsonSerializer();
		assertEquals(serializer.serialize(anotherDummy), "{\"dummy\":{\"string_value\":\"test123\",\"long_value\":1234,\"dummy_instant\":\"1972-12-07T05:33:00Z\"}}");
	}

	@Test
	public void shouldDeserializeJsonToObject() {
		DefaultJsonSerializer serializer = new DefaultJsonSerializer();
		AnotherDummy anotherDummy = serializer.deserialize("{\"dummy\":{\"string_value\":\"test123\",\"long_value\":1234,\"dummy_instant\":\"1972-12-07T05:33:00Z\"}}", AnotherDummy.class);
		assertEquals(anotherDummy.getDummy().getStringValue(), "test123");
		assertEquals(anotherDummy.getDummy().getLongValue(), Long.valueOf(1234));
		assertEquals(anotherDummy.getDummy().getDummyInstant(), Instant.ofEpochMilli(92554380000L));
	}

	@Test
	public void shouldDeserializeStreamToObject() {
		DefaultJsonSerializer serializer = new DefaultJsonSerializer();
		String input = "{\"dummy\":{\"string_value\":\"test123\",\"long_value\":1234,\"dummy_instant\":\"1972-12-07T05:33:00Z\"}}";
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
		AnotherDummy anotherDummy = serializer.deserialize(byteArrayInputStream, AnotherDummy.class);
		assertEquals(anotherDummy.getDummy().getStringValue(), "test123");
		assertEquals(anotherDummy.getDummy().getLongValue(), Long.valueOf(1234));
		assertEquals(anotherDummy.getDummy().getDummyInstant(), Instant.ofEpochMilli(92554380000L));
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

		private Instant dummyInstant;

		public Dummy() {
		}

		/**
		 * @param stringValue
		 * @param longValue
		 */
		public Dummy(String stringValue, Long longValue, Instant instant) {
			this.stringValue = stringValue;
			this.longValue = longValue;
			this.dummyInstant = instant;
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

		/**
		 * @return the instant
		 */
		public Instant getDummyInstant() {
			return dummyInstant;
		}

		/**
		 * @param dummyInstant
		 *            the dummyInstant to set
		 */
		public void setDummyInstant(Instant dummyInstant) {
			this.dummyInstant = dummyInstant;
		}
	}
}
