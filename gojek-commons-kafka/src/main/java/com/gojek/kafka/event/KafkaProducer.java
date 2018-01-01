/**
 * 
 */
package com.gojek.kafka.event;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.gojek.core.event.Destination;
import com.gojek.core.event.Producer;

/**
 * @author ganesh.s
 *
 */
public class KafkaProducer<K, E> implements Producer<E> {
	
	private org.apache.kafka.clients.producer.Producer<K, E> producer;
	
	/**
	 * @param configs
	 */
	public KafkaProducer(Map<String, Object> configs) {
		this(configs, null, null);
	}
	
	/**
	 * @param producer
	 */
	public KafkaProducer(org.apache.kafka.clients.producer.Producer<K, E> producer) {
		this.producer = producer;
	}
	
	/**
	 * @param configs
	 * @param keySerializer
	 * @param valueSerializer
	 */
	public KafkaProducer(Map<String, Object> configs, Class<?> keySerializer, Class<?> valueSerializer) {
		if (keySerializer != null) {
			configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
		}
		if (valueSerializer != null) {
			configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
		}
		this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(configs);
	}

	@Override
	public void send(E event, Destination destination) {
		send(null, event, destination.getExchange());
	}
	
	/**
	 * @param key
	 * @param value
	 * @param topic
	 */
	public void send(K key, E value, String topic) {
		ProducerRecord<K, E> record = null;
		if (key == null) {
			record = new ProducerRecord<K, E>(topic, value);
		} else {
			record = new ProducerRecord<K, E>(topic, key, value);
		}
		producer.send(record);
		producer.flush();
	}
}
