/**
 * 
 */
package com.gojek.ds.service;

import javax.inject.Inject;

import com.gojek.core.event.QueuedProducer;
import com.gojek.ds.DSConfiguration;
import com.gojek.ds.QueueConfiguration;
import com.gojek.ds.events.DriverEvent;
import com.google.common.eventbus.Subscribe;

/**
 * @author ganeshs
 *
 */
public class EventPublisherService {

    private QueuedProducer producer;

    private QueueConfiguration configuration;

    /**
     * @param configuration
     * @param producer
     */
    @Inject
    public EventPublisherService(DSConfiguration configuration, QueuedProducer producer) {
        this.producer = producer;
        this.configuration = configuration.getQueueConfiguration();
    }

    /**
     * @param event
     */
    @Subscribe
    public void onDriverEvent(DriverEvent event) {
        producer.send(event, configuration.getDriverStatusDestination());
    }
}
