/**
 * 
 */
package com.gojek.kafka;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.Destination;

/**
 * @author ganesh.s
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class KafkaConsumerConfiguration extends ConsumerConfiguration {
	
	private List<String> topics;
	
	private boolean commitSync;
	
	private Long pollTimeout = DEFAULT_POLL_TIMEOUT;
	
	public static final Long DEFAULT_POLL_TIMEOUT = 100L;
	
	/**
	 * Default constructor
	 */
	public KafkaConsumerConfiguration() {
	}
	
	/**
	 * @param topics
	 * @param commitSync
	 * @param pollTimeout
	 */
	public KafkaConsumerConfiguration(List<String> topics, boolean commitSync, Long pollTimeout, int maxConsumers) {
		super(null, maxConsumers, null, 0);
		this.topics = topics;
		this.commitSync = commitSync;
		this.pollTimeout = pollTimeout;
	}

	/**
	 * @return the topics
	 */
	public List<String> getTopics() {
		return topics;
	}

	/**
	 * @param topics the topics to set
	 */
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	/**
	 * @return the commitSync
	 */
	public boolean isCommitSync() {
		return commitSync;
	}

	/**
	 * @param commitSync the commitSync to set
	 */
	public void setCommitSync(boolean commitSync) {
		this.commitSync = commitSync;
	}

	/**
	 * @return the pollTimeout
	 */
	public Long getPollTimeout() {
		return pollTimeout;
	}

	/**
	 * @param pollTimeout the pollTimeout to set
	 */
	public void setPollTimeout(Long pollTimeout) {
		this.pollTimeout = pollTimeout;
	}
	
	@Override
	@JsonIgnore
	public String getQueueName() {
		return super.getQueueName();
	}

	@Override
	@JsonIgnore
	public Destination getRetryDestination() {
		return super.getRetryDestination();
	}
	
	@Override
	@JsonIgnore
	public Integer getMaxRetries() {
		return super.getMaxRetries();
	}
}