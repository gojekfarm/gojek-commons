/**
 *
 */
package com.gojek.application;

/**
 * @author ganeshs
 *
 */
public class AppContext {

	private static BaseConfiguration configuration;
	
	/**
	 * @param config
	 */
	public static <T extends BaseConfiguration> void init(T config) {
		configuration = config;
	}

	/**
	 * @return the configuration
	 */
	@SuppressWarnings("unchecked")
    public static <T extends BaseConfiguration> T getConfig() {
		return (T) configuration;
	}
}
