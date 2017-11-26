/**
 * 
 */
package com.gojek.amqp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author ganeshs
 *
 */
public class AmqpHealthCheckTest {
    
    private Channel channel;
    
    private AmqpConnection amqpConnection;
    
    private AmqpHealthCheck check;
    
    @BeforeMethod
    public void setup() throws Exception {
        Connection connection = mock(Connection.class);
        amqpConnection = new AmqpConnection();
        amqpConnection.init(connection, 2, 1, 1);
        channel = mock(Channel.class);
        when(connection.createChannel()).thenReturn(channel);
        check = new AmqpHealthCheck(amqpConnection);
    }

    @Test
    public void shouldReturnHealthy() throws Exception {
        when(channel.isOpen()).thenReturn(true);
        assertTrue(check.check().isHealthy());
    }
    
    @Test
    public void shouldReturnUnHealthy() throws Exception {
        when(channel.isOpen()).thenReturn(false);
        assertFalse(check.check().isHealthy());
    }
}
