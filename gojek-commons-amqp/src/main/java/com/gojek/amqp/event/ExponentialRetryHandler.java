package com.gojek.amqp.event;

import com.gojek.amqp.AmqpConnection;
import com.gojek.core.event.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by satvik on 04/02/18.
 */
public class ExponentialRetryHandler<E> extends FixedRetryHandler<E> {

    private static final Logger logger = LoggerFactory.getLogger(ExponentialRetryHandler.class);

    private static final int EXPONENT = 2;

    /**
     * @param connection
     * @param retryDestination
     * @param eventClass
     * @param maxRetries
     */
    public ExponentialRetryHandler(AmqpConnection connection, Destination retryDestination, Class<E> eventClass, int maxRetries) {
        super(connection, retryDestination, eventClass, maxRetries);
    }

    /**
     * @param failureCount
     * @param maxRetries
     */
    protected void waitBetweenRetries(int failureCount, int maxRetries) {
        if (failureCount == 0) {
            return;
        }
        try {
            Thread.sleep((long) (Math.pow(EXPONENT, failureCount) * 1000));
        } catch (InterruptedException ie) {
            logger.warn("Interrrupted when waiting between retries.");
        }
    }
}
