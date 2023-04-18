/**
 *
 */
package com.gojek.core.event;

/**
 * @author ganeshs
 *
 */
public class ConsumerConfiguration {

    private Destination retryDestination;
    
    private String queueName;

    private Integer prefetchCount = 0;
    
    private Integer maxRetries = DEFAULT_MAX_RETRIES;
    
    private Integer maxQueueConsumers = DEFAULT_MAX_QUEUE_CONSUMERS;
    
    public static final int DEFAULT_MAX_QUEUE_CONSUMERS = 5;
    
    public static final int DEFAULT_MAX_RETRIES = 5;
    
    /**
     * Default constructor
     */
    public ConsumerConfiguration() {
    }

    /**
     * @param queueName
     * @param retryDestination
     */
    public ConsumerConfiguration(String queueName, Destination retryDestination) {
        this.queueName = queueName;
        this.retryDestination = retryDestination;
    }

    /**
     * @param queueName
     * @param maxQueueConsumers
     * @param retryDestination
     * @param maxRetries
     */
    public ConsumerConfiguration(String queueName, Integer maxQueueConsumers, Destination retryDestination, Integer maxRetries) {
        this(queueName, retryDestination);
        this.maxQueueConsumers = maxQueueConsumers;
        this.maxRetries = maxRetries;
    }

    /**
     * @return the retryDestination
     */
    public Destination getRetryDestination() {
        return retryDestination;
    }

    /**
     * @param retryDestination the retryDestination to set
     */
    public void setRetryDestination(Destination retryDestination) {
        this.retryDestination = retryDestination;
    }

    /**
     * @return the queueName
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * @param queueName the queueName to set
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * @return the maxRetries
     */
    public Integer getMaxRetries() {
        return maxRetries;
    }

    /**
     * @param maxRetries the maxRetries to set
     */
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * @return the maxQueueConsumers
     */
    public Integer getMaxQueueConsumers() {
        return maxQueueConsumers;
    }

    /**
     * @param maxQueueConsumers the maxQueueConsumers to set
     */
    public void setMaxQueueConsumers(Integer maxQueueConsumers) {
        this.maxQueueConsumers = maxQueueConsumers;
    }

    public Integer getPrefetchCount() {
        return prefetchCount;
    }

    public void setPrefetchCount(Integer prefetchCount) {
        this.prefetchCount = prefetchCount;
    }
}
