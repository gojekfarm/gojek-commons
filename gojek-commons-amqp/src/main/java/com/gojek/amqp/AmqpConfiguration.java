/**
 *
 */
package com.gojek.amqp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Address;

import java.util.List;

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

	private Integer networkRecoveryInterval = 1000;

	private List<String> hosts = Lists.newArrayList();

	@JsonIgnore
	private Address[] addresses;

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
	 * @param networkRecoveryInterval
	 * @param hosts
	 */
	public AmqpConfiguration(String uri, boolean autoRecovery, Integer maxChannels, Integer minChannels, Integer maxIdleChannels,
							 Integer networkRecoveryInterval, List<String> hosts) {
		this.uri = uri;
		this.autoRecovery = autoRecovery;
		this.maxChannels = maxChannels;
		this.minChannels = minChannels;
		this.maxIdleChannels = maxIdleChannels;
		this.networkRecoveryInterval = networkRecoveryInterval;
		this.hosts = hosts;
	}

	/**s
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

	public Integer getNetworkRecoveryInterval() {
		return networkRecoveryInterval;
	}

	public void setNetworkRecoveryInterval(Integer networkRecoveryInterval) {
		this.networkRecoveryInterval = networkRecoveryInterval;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public Address[] getAddresses() {
		if (addresses == null) {
			addresses = new Address[hosts.size()];
			for(int i=0; i<addresses.length; i++) {
				addresses[i] = new Address(hosts.get(i));
			}
		}
		return addresses;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}
}