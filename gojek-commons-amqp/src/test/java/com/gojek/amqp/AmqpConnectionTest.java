/**
 *
 */
package com.gojek.amqp;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.amqp.AmqpConnection.ConnectionPoolListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author ganeshs
 *
 */
public class AmqpConnectionTest {

	private AmqpConnection amqpConnection;
	
	private Connection connection;
	
	private GenericObjectPool<Channel> pool;
	
	@BeforeMethod
	public void setup() throws Exception {
		connection = mock(Connection.class);
		when(connection.createChannel()).thenReturn(mock(Channel.class));
		amqpConnection = spy(new AmqpConnection());
		GenericObjectPoolConfig poolConfig = mock(GenericObjectPoolConfig.class);
		doReturn(poolConfig).when(amqpConnection).constructPoolConfig(1, 1, 1);
		pool = mock(GenericObjectPool.class);
		when(pool.borrowObject()).thenReturn(mock(Channel.class));
		doReturn(pool).when(amqpConnection).constructPool(connection, poolConfig);
		amqpConnection.init(connection, 1, 1, 1);
	}
	
	@Test
	public void shouldGetChannel() {
		assertNotNull(amqpConnection.getChannel());
	}
	
	@Test
	public void shouldReleaseChannel() throws Exception {
		Channel returnedChannel = amqpConnection.getChannel();
		amqpConnection.releaseChannel(returnedChannel);
		verify(pool).returnObject(returnedChannel);
	}
	
	@Test
	public void shouldClose() throws Exception {
		amqpConnection.close();
		verify(connection).close();
		verify(pool).close();
	}
	
	@Test
	public void shouldConstructPoolConfig() {
		GenericObjectPoolConfig config = amqpConnection.constructPoolConfig(10, 2, 3);
		assertEquals(config.getMaxIdle(), 3);
		assertEquals(config.getMinIdle(), 2);
		assertEquals(config.getMaxTotal(), 10);
	}
	
	@Test
	public void shouldConstructPool() {
		GenericObjectPoolConfig config = amqpConnection.constructPoolConfig(10, 2, 3);
		GenericObjectPool<Channel> pool = amqpConnection.constructPool(connection, config);
		assertEquals(pool.getMaxIdle(), 3);
		assertEquals(pool.getMinIdle(), 2);
		assertEquals(pool.getMaxTotal(), 10);
	}
	
    @Test
    public void shouldInvokeListenerOnInit() {
        AtomicBoolean result = new AtomicBoolean();
        amqpConnection.register(new ConnectionPoolListener() {
            @Override
            public void onPoolInitialized(GenericObjectPool<Channel> pool) {
                result.set(pool != null);
            }
            
            @Override
            public void onPoolDestroyed(GenericObjectPool<Channel> pool) {
            }
        });
        amqpConnection.init(connection, 1, 1, 1);
        assertTrue(result.get());
    }
    
    @Test
    public void shouldInvokeListenerOnDestroy() {
        AtomicBoolean result = new AtomicBoolean();
        amqpConnection.register(new ConnectionPoolListener() {
            @Override
            public void onPoolInitialized(GenericObjectPool<Channel> pool) {
            }
            
            @Override
            public void onPoolDestroyed(GenericObjectPool<Channel> pool) {
                result.set(pool != null);
            }
        });
        amqpConnection.init(connection, 1, 1, 1);
        amqpConnection.close();
        assertTrue(result.get());
    }
}
