/**
 *
 */
package com.gojek.amqp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author ganeshs
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AmqpConfiguration {
	
	private String uri = "amqp://guest:guest@localhost:5672/vhost";
	
	private boolean autoRecovery = true;
	
	private Integer maxChannels = 10;
	
	private Integer minChannels = 5;
	
	private Integer maxIdleChannels = 5;
	
	/**
	 * Default Constructor
	 */
	public AmqpConfiguration() {
    }

	/**
     * @param uri
     * @param autoRecovery
     * @param maxChannels
     * @param minChannels
     * @param maxIdleChannels
     */
    public AmqpConfiguration(String uri, boolean autoRecovery, Integer maxChannels, Integer minChannels, Integer maxIdleChannels) {
        this.uri = uri;
        this.autoRecovery = autoRecovery;
        this.maxChannels = maxChannels;
        this.minChannels = minChannels;
        this.maxIdleChannels = maxIdleChannels;
    }

    /**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the autoRecovery
	 */
	public boolean isAutoRecovery() {
		return autoRecovery;
	}

	/**
	 * @param autoRecovery the autoRecovery to set
	 */
	public void setAutoRecovery(boolean autoRecovery) {
		this.autoRecovery = autoRecovery;
	}

	/**
	 * @return the maxChannels
	 */
	public Integer getMaxChannels() {
		return maxChannels;
	}

	/**
	 * @param maxChannels the maxChannels to set
	 */
	public void setMaxChannels(Integer maxChannels) {
		this.maxChannels = maxChannels;
	}

	/**
	 * @return the minChannels
	 */
	public Integer getMinChannels() {
		return minChannels;
	}

	/**
	 * @param minChannels the minChannels to set
	 */
	public void setMinChannels(Integer minChannels) {
		this.minChannels = minChannels;
	}

	/**
	 * @return the maxIdleChannels
	 */
	public Integer getMaxIdleChannels() {
		return maxIdleChannels;
	}

	/**
	 * @param maxIdleChannels the maxIdleChannels to set
	 */
	public void setMaxIdleChannels(Integer maxIdleChannels) {
		this.maxIdleChannels = maxIdleChannels;
	}

}
