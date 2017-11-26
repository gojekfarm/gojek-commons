/**
 * 
 */
package com.gojek.job;

import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class QuartzConfiguration {

	private Map<String, Object> properties = Maps.newHashMap();
	
	/**
	 * Default constructor
	 */
	public QuartzConfiguration() {
	    this(Maps.newHashMap());
    }
	
	/**
	 * @param properties
	 */
	public QuartzConfiguration(Map<String, Object> properties) {
	    this.properties = properties;
    }
	
	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	/**
	 * Converts the Map to Properties
	 * @return
	 */
	public Properties toProperties() {
		Properties props = new Properties();
		properties.forEach((key, value) -> {
			if (nonNull(value)) {
				props.put(key, value);
			} else {
				props.put(key, "");
			}
		});
		return props;
	}
}
