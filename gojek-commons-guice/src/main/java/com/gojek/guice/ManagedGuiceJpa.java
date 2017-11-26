/**
 *
 */
package com.gojek.guice;

import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.activejpa.jpa.EntityManagerProvider;
import org.activejpa.jpa.JPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.guice.util.GuiceUtil;
import com.gojek.jpa.ManagedJpa;

/**
 * @author ganeshs
 *
 */
public class ManagedGuiceJpa extends ManagedJpa {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagedGuiceJpa.class);
	
	/**
	 * @param persistentUnit
	 * @param onStart
	 */
	public ManagedGuiceJpa(String persistentUnit, Callable<Void> onStart) {
		super(persistentUnit, onStart);
    }
	
	@Override
	protected void initJpa() {
		logger.info("Registering persistence context to ActiveJPA");
		JPA.instance.addPersistenceUnit(getPersistentUnit(), new EntityManagerProvider() {
			@Override
			public EntityManagerFactory getEntityManagerFactory() {
				return GuiceUtil.getInstance(EntityManagerFactory.class);
			}
			
			@Override
			public EntityManager getEntityManager() {
				return GuiceUtil.getInstance(EntityManager.class);
			}
		}, true);
	}
}
