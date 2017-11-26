/**
 * 
 */
package com.gojek.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gojek.amqp.AmqpConfiguration;
import com.gojek.amqp.AmqpSupport;
import com.gojek.application.BaseConfiguration;
import com.gojek.cache.CacheConfiguration;
import com.gojek.cache.CacheSupport;
import com.gojek.jpa.JpaConfiguration;
import com.gojek.jpa.JpaSupport;

/**
 * @author ganeshs
 *
 */
public class DSConfiguration extends BaseConfiguration implements CacheSupport, AmqpSupport, JpaSupport {
    
    @JsonProperty("amqp")
    private AmqpConfiguration amqpConfiguration;
    
    @JsonProperty("cache")
    private CacheConfiguration cacheConfiguration;
    
    @JsonProperty("queue")
    private QueueConfiguration queueConfiguration;
    
    private JpaConfiguration jpaConfiguration;

    @Override
    public AmqpConfiguration getAmqpConfiguration() {
        return amqpConfiguration;
    }

    @Override
    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    /**
     * @return the queueConfiguration
     */
    public QueueConfiguration getQueueConfiguration() {
        return queueConfiguration;
    }

    /**
     * @param queueConfiguration the queueConfiguration to set
     */
    public void setQueueConfiguration(QueueConfiguration queueConfiguration) {
        this.queueConfiguration = queueConfiguration;
    }

    /**
     * @param amqpConfiguration the amqpConfiguration to set
     */
    public void setAmqpConfiguration(AmqpConfiguration amqpConfiguration) {
        this.amqpConfiguration = amqpConfiguration;
    }

    /**
     * @param cacheConfiguration the cacheConfiguration to set
     */
    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }
    
    @JsonProperty("jpa")
    public JpaConfiguration getJpaConfiguration() {
        return jpaConfiguration;
    }
    
    /**
     * @param configuration
     */
    public void setJpaConfiguration(JpaConfiguration configuration) {
        this.jpaConfiguration = configuration;
    }

}
