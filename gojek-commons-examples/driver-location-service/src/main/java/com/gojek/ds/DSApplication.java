/**
 * 
 */
package com.gojek.ds;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.amqp.AmqpBundle;
import com.gojek.amqp.AmqpConnection;
import com.gojek.amqp.event.AmqpQueuedProducer;
import com.gojek.amqp.event.EventHandler;
import com.gojek.application.BaseApplication;
import com.gojek.cache.CacheBundle;
import com.gojek.cache.redis.JedisConnection;
import com.gojek.core.event.ConsumerConfiguration;
import com.gojek.core.event.CoreEventBus;
import com.gojek.core.event.QueuedProducer;
import com.gojek.ds.resource.DriverResource;
import com.gojek.ds.service.DriverEventHandler;
import com.gojek.ds.service.EventPublisherService;
import com.gojek.guice.jpa.GuiceJpaBundle;
import com.gojek.guice.util.GuiceUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author ganeshs
 *
 */
public class DSApplication extends BaseApplication<DSConfiguration> {
    
    private String configFile;
    
    private static final String ENV_CONFIG_FILE = "CONFIG_FILE_PATH";
    
    private static final Logger logger = LoggerFactory.getLogger(DSApplication.class);

    /**
     * @param configFile
     */
    public DSApplication(String configFile) {
        this.configFile = configFile;
    }
    
    @Override
    protected void registerResources(DSConfiguration configuration, Environment environment) {
        super.registerResources(configuration, environment);
        environment.jersey().register(DriverResource.class);
        
        CoreEventBus.instance().register(EventPublisherService.class);
    }
    
    @Override
    protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
        super.addBundles(bootstrap);
        bootstrap.addBundle(GuiceJpaBundle.<DSConfiguration>builder(this.configFile).with("ds").using(DSConfiguration.class).addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(QueuedProducer.class).to(AmqpQueuedProducer.class);
            }
        }).build());
        bootstrap.addBundle(CacheBundle.<DSConfiguration>builder().using(() -> GuiceUtil.getInstance(JedisConnection.class)).build());
        bootstrap.addBundle(AmqpBundle.<DSConfiguration>builder().using(() -> GuiceUtil.getInstance(AmqpConnection.class)).with(configuration -> {
            Map<ConsumerConfiguration, EventHandler<?>> consumers = Maps.newHashMap();
            consumers.put(configuration.getQueueConfiguration().getDriverConsumerConfiguration(), GuiceUtil.getInstance(DriverEventHandler.class));
            return consumers;
        }).build());
    }

    public static void main(String[] args) throws Exception {
        String configFile = System.getenv(ENV_CONFIG_FILE);
        if (Strings.isNullOrEmpty(configFile)) {
            logger.info("ENV variable {} not set", ENV_CONFIG_FILE);
            configFile = args.length > 0 ? args[0] : "src/main/resources/ds.yml";
        } else {
            logger.info("ENV variable {} set to {}", ENV_CONFIG_FILE, configFile);
        }
        new DSApplication(configFile).run("server", configFile);
    }
    
}
