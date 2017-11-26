/**
 * 
 */
package com.gojek.amqp;

import com.codahale.metrics.health.HealthCheck;

/**
 * @author ganeshs
 *
 */
public class AmqpHealthCheck extends HealthCheck {
    
    private AmqpConnection connection;
    
    /**
     * @param connection
     */
    public AmqpHealthCheck(AmqpConnection connection) {
        this.connection = connection;
    }

    @Override
    protected Result check() throws Exception {
        return connection.execute(channel -> {
            return channel.isOpen() ? Result.healthy() : Result.unhealthy("Broker is down");
        });
    }
}