/**
 *
 */
package com.gojek.jpa;

import java.util.concurrent.Callable;

import org.activejpa.jpa.JPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import io.dropwizard.lifecycle.Managed;

/**
 * @author ganeshs
 *
 */
public class ManagedJpa implements Managed {
	
	private String persistentUnit;
	
	private Callable<Void> onStart;

	private static final Logger logger = LoggerFactory.getLogger(ManagedJpa.class);
	
	/**
	 * @param persistentUnit
	 * @param onStart
	 */
	public ManagedJpa(String persistentUnit, Callable<Void> onStart) {
		this.persistentUnit = persistentUnit;
		this.onStart = onStart;
    }
	
	@Override
	public void start() throws Exception {
		initJpa();
		if (onStart != null) {
		    this.onStart.call();
		}
	}
	
	protected void initJpa() {
		logger.info("Registering persistence context to ActiveJPA");
		JPA.instance.addPersistenceUnit(persistentUnit, true);
	}
	
	protected String getPersistentUnit() {
		return persistentUnit;
	}

	@Override
	public void stop() throws Exception {
		if (! Strings.isNullOrEmpty(persistentUnit)) {
			logger.info("Closing JPA instance");
			JPA.instance.close();
		}
	}
}
