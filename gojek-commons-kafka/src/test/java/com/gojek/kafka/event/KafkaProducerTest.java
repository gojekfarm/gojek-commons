/**
 * 
 */
package com.gojek.kafka.event;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.core.event.Destination;

/**
 * @author ganesh.s
 *
 */
public class KafkaProducerTest {
	
	private KafkaProducer<String, String> producer;
	
	private MockProducer<String, String> mockProducer;

	@BeforeMethod
	public void setup() {
		mockProducer = spy(new MockProducer<String, String>(false, new StringSerializer(), new StringSerializer()));
		this.producer = new KafkaProducer<String, String>(mockProducer);
	}
	
	@Test
	public void shouldSendMessageToTopic() {
		producer.send("some_message", new Destination("test_topic"));
		ProducerRecord<String, String> record = mockProducer.history().get(0);
		assertEquals(record.value(), "some_message");
		assertNull(record.key());
	}
	
	@Test
	public void shouldSendMessageToTopicAndFlushThem() {
		producer.send("some_message", new Destination("test_topic"));
		verify(mockProducer).flush();
	}
	
	public void shouldSendMessageWithKey() {
		producer.send("some_key", "some_message", "test_topic");
		ProducerRecord<String, String> record = mockProducer.history().get(0);
		assertEquals(record.value(), "some_message");
		assertEquals(record.key(), "some_key");
	}
}
