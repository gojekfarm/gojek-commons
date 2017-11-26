/**
 *
 */
package com.gojek.jpa;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gojek.jpa.entity.SnakeCaseNamingStrategy;
import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class JpaConfiguration {

    private String url;
    
    private String user;
    
    private String password;
    
    private boolean migrate;
    
    private String driverClass;
    
    private Map<String, Object> properties = Maps.newHashMap();

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the migrate
     */
    public boolean isMigrate() {
        return migrate;
    }

    /**
     * @param migrate the migrate to set
     */
    public void setMigrate(boolean migrate) {
        this.migrate = migrate;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
	
	public Map<String, Object> toAttributes() {
		Map<String, Object> attributes = Maps.newHashMap();
		attributes.put(AvailableSettings.DRIVER, getDriverClass());
		attributes.put(AvailableSettings.URL, getUrl());
		attributes.put(AvailableSettings.USER, getUser());
		attributes.put(AvailableSettings.PASS, getPassword());
		attributes.put(AvailableSettings.FORMAT_SQL, true);
		attributes.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		attributes.put(AvailableSettings.CONNECTION_HANDLING, "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");
		attributes.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, SnakeCaseNamingStrategy.class.getName());
		attributes.putAll(getProperties());
		return attributes;
	}
}
