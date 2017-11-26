/**
 *
 */
package com.gojek.guice.util;

import com.google.inject.Injector;

/**
 * @author ganeshs
 *
 */
public class GuiceUtil {
	
	private static Injector injector;

	/**
	 * @param inj
	 */
	public synchronized static void load(Injector inj) {
		if (injector == null) {
			injector = inj;
		}
	}
	
	/**
	 * Returns the bean for the given class
	 *
	 * @param clazz
	 * @return
	 */
	public static <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}
	
	/**
	 * Resets the injector
	 */
	public static void reset() {
	    injector = null;
	}
}
