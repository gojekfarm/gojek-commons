/**
 * 
 */
package com.gojek.ds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.Destination;

/**
 * @author ganeshs
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class QueueConfiguration {
	
	private Destination driverStatusDestination;
	
	@JsonProperty("driverConsumer")
	private ConsumerConfiguration driverConsumerConfiguration;

    /**
     * @return the driverStatusDestination
     */
    public Destination getDriverStatusDestination() {
        return driverStatusDestination;
    }

    /**
     * @param driverStatusDestination the driverStatusDestination to set
     */
    public void setDriverStatusDestination(Destination driverStatusDestination) {
        this.driverStatusDestination = driverStatusDestination;
    }

    /**
     * @return the driverConsumerConfiguration
     */
    public ConsumerConfiguration getDriverConsumerConfiguration() {
        return driverConsumerConfiguration;
    }

    /**
     * @param driverConsumerConfiguration the driverConsumerConfiguration to set
     */
    public void setDriverConsumerConfiguration(ConsumerConfiguration driverConsumerConfiguration) {
        this.driverConsumerConfiguration = driverConsumerConfiguration;
    }
}
