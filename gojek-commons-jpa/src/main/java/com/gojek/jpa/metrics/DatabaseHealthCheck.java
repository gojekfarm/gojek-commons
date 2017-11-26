package com.gojek.jpa.metrics;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.activejpa.jpa.EntityManagerProvider;
import org.activejpa.jpa.JPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

/**
 * @author ganeshs
 */
public class DatabaseHealthCheck extends HealthCheck {

    private static final String queryStr = "SELECT 1";
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheck.class);

    @Override
    protected Result check() throws Exception {
        EntityManagerProvider provider = JPA.instance.getDefaultConfig().getEntityManagerProvider();
        EntityManager manager = provider.getEntityManager();

        Query query = manager.createNativeQuery(queryStr);
        try {
            query.getResultList();
            return Result.healthy("Connection successful to database.");
        } catch (Exception e) {
            logger.error("Not able to connect to database. ERROR: {}", e);
            return Result.unhealthy("Not able to connect database. ERROR: " + e.getMessage());
        }
    }
}
