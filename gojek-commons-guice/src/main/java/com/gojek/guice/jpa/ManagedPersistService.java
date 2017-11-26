/**
 *
 */
package com.gojek.guice.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

import io.dropwizard.lifecycle.Managed;

/**
 * Initializes the Guice PersistService
 *
 * @author ganeshs
 *
 */
public class ManagedPersistService implements Managed {

	private PersistService persistService;
	
	private static final Logger logger = LoggerFactory.getLogger(ManagedPersistService.class);
	
	@Inject
	public ManagedPersistService(PersistService persistService) {
		this.persistService = persistService;
	}

	public void start() {
	    logger.info("Starting the persist service");
		persistService.start();
	}

	public void stop() {
	    logger.info("Stopping the persist service");
		persistService.stop();
	}
}