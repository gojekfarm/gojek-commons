/**
 *
 */
package com.gojek.guice.util;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.JerseyGuiceModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;

/**
 * Utility for unit testing
 *
 * @author ganeshs
 *
 */
public class JerseyUtils {

	/**
	 * Setup Guice in jersey
	 */
	public static void setupGuice() {
		JerseyGuiceUtils.install(new ServiceLocatorGenerator() {
			@Override
			public ServiceLocator create(String name, ServiceLocator parent) {
				if (!name.startsWith("__HK2_")) {
					return null;
				}
				List<Module> modules = new ArrayList<>();
				modules.add(new JerseyGuiceModule(name));
				modules.add(new ServletModule());
				return Guice.createInjector(modules).getInstance(ServiceLocator.class);
			}
		});
	}
	
	/**
	 * Reset Guice in jersey
	 */
	public static void resetGuice() {
		JerseyGuiceUtils.reset();
	}
}
