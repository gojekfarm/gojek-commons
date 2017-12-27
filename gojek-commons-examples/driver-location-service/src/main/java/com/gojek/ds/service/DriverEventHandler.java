/**
 * 
 */
package com.gojek.ds.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.amqp.AmqpConnection;
import com.gojek.amqp.event.FixedRetryHandler;
import com.gojek.core.event.Consumer.Status;
import com.gojek.core.event.Event;
import com.gojek.ds.DSConfiguration;
import com.gojek.ds.events.DriverActiveEvent;
import com.gojek.ds.events.DriverBusyEvent;
import com.gojek.ds.events.DriverInActiveEvent;
import com.gojek.guice.jpa.ActiveJpaGuiceExecutionWrapper;

/**
 * 
 * @author ganeshs
 *
 */
public class DriverEventHandler extends FixedRetryHandler<Event> {
    
    private ActiveJpaGuiceExecutionWrapper executionWrapper;
    
    private static final Logger logger = LoggerFactory.getLogger(DriverEventHandler.class);

    /**
     * @param connection
     * @param retryDestination
     * @param maxRetries
     */
    @Inject
    public DriverEventHandler(DSConfiguration configuration, AmqpConnection connection) {
        super(connection, configuration.getQueueConfiguration().getDriverConsumerConfiguration().getRetryDestination(), Event.class, configuration.getQueueConfiguration().getDriverConsumerConfiguration().getMaxRetries());
        this.executionWrapper = new ActiveJpaGuiceExecutionWrapper();
    }
    
    @Override
    protected Status handleInternal(Event event) {
        try {
            // Any jpa operations should be wrapped under the execution wrapper for proper handling on entity manager
            return executionWrapper.execute((optional) -> {
                switch(event.getType()) {
                case DriverActiveEvent.TYPE:
                    break;
                case DriverInActiveEvent.TYPE:
                    break;
                case DriverBusyEvent.TYPE:
                    break;
                }
                return Status.success;
            });
        } catch (Exception e) {
            logger.error("Failed while handling the event - " + event.getEventId(), e);
            return Status.soft_failure;
        }
    }
}