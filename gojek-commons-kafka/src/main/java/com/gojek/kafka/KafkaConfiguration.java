/**
 * 
 */
package com.gojek.kafka;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author ganesh.s
 *
 */
public class KafkaConfiguration {

	private Map<String, Object> configs = Maps.newHashMap();

	/**
	 * @return the configs
	 */
	public Map<String, Object> getConfigs() {
		return configs;
	}

	/**
	 * @param configs the configs to set
	 */
	public void setConfigs(Map<String, Object> configs) {
		this.configs = configs;
	}
}
