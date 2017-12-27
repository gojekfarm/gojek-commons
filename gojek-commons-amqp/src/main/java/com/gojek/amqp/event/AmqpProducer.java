/**
 *
 */
package com.gojek.amqp.event;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.gojek.amqp.AmqpConnection;
import com.gojek.amqp.AmqpException;
import com.gojek.core.event.Destination;
import com.gojek.core.event.Producer;
import com.gojek.util.serializer.Serializer;

/**
 * @author ganeshs
 *
 */
@Singleton
public class AmqpProducer<E> implements Producer<E> {
    
    private AmqpConnection connection;
    
    /**
     * @param connection
     */
    @Inject
    public AmqpProducer(AmqpConnection connection) {
        this.connection = connection;
    }

    @Override
    public void send(E event, Destination destination) {
        connection.execute((channel) -> {
            try {
                String value = Serializer.DEFAULT_JSON_SERIALIZER.serialize(event);
                channel.basicPublish(destination.getExchange(), destination.getRoutingKey(), null, value.getBytes());
            } catch (Exception e) {
                throw new AmqpException("Failed while sending the messages", e);
            }
            return null;
        });
    }

}
