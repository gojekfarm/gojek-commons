/**
 *
 */
package com.gojek.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gojek.application.swagger.SwaggerBundleConfiguration;

import io.dropwizard.Configuration;

/**
 * @author ganeshs
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class BaseConfiguration extends Configuration {
	
	@JsonProperty("swagger")
	private SwaggerBundleConfiguration swaggerBundleConfiguration;
	
	private String internalUrlPrefix = "internal/";

	/**
	 * @return the swaggerBundleConfiguration
	 */
	public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
		return swaggerBundleConfiguration;
	}

	/**
	 * @param swaggerBundleConfiguration the swaggerBundleConfiguration to set
	 */
	public void setSwaggerBundleConfiguration(SwaggerBundleConfiguration swaggerBundleConfiguration) {
		this.swaggerBundleConfiguration = swaggerBundleConfiguration;
	}

    /**
     * @return the internalUrlPrefix
     */
    public String getInternalUrlPrefix() {
        return internalUrlPrefix;
    }

    /**
     * @param internalUrlPrefix the internalUrlPrefix to set
     */
    public void setInternalUrlPrefix(String internalUrlPrefix) {
        this.internalUrlPrefix = internalUrlPrefix;
    }

}
