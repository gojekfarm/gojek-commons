/**
 *
 */
package com.gojek.jpa;

import org.flywaydb.core.Flyway;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class MigrationsBundle<T extends Configuration & JpaSupport> implements ConfiguredBundle<T> {

	@Override
    public void run(T configuration, Environment environment) throws Exception {
		JpaConfiguration jpaConfiguration = configuration.getJpaConfiguration();
		if (jpaConfiguration.isMigrate()) {
		    Flyway flyway = new Flyway();
		    flyway.setDataSource(jpaConfiguration.getUrl(), jpaConfiguration.getUser(), jpaConfiguration.getPassword());
		    flyway.migrate();
		}
    }
	
	@Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

}
