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

import com.rabbitmq.client.Address;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author ganeshs
 *
 */
public class AmqpConnectionManagerTest {

	private AmqpConnectionManager manager;
	
	private Connection connection;
	
	private AmqpConnection amqpConnection;
	
	private AmqpConfiguration configuration;
	
	@BeforeMethod
	public void setup() throws Exception {
		configuration = new AmqpConfiguration();
		amqpConnection = mock(AmqpConnection.class);
		manager = spy(new AmqpConnectionManager(configuration, amqpConnection));
		ConnectionFactory factory = mock(ConnectionFactory.class);
		doReturn(factory).when(manager).createConnectionFactory();
		connection = mock(Connection.class);
		when(factory.newConnection(new Address[]{})).thenReturn(connection);
	}
	
	@Test
	public void shouldStartManager() throws Exception {
		manager.start();
		verify(amqpConnection).init(connection, configuration.getMaxChannels(), configuration.getMinChannels(), configuration.getMaxIdleChannels());
	}

	@Test
	public void shouldCreateConnectionFactoryWithConfigs() throws Exception {
		when(manager.createConnectionFactory()).thenCallRealMethod();
		ConnectionFactory factory = manager.createConnectionFactory();
		assertEquals(configuration.isAutoRecovery(), factory.isAutomaticRecoveryEnabled());
		assertEquals(configuration.getNetworkRecoveryInterval().longValue(), factory.getNetworkRecoveryInterval());
	}

	@Test
	public void shouldStopManager() throws Exception {
		manager.start();
		manager.stop();
		verify(amqpConnection).close();
	}
	
	@Test
	public void shouldCreateConnectionFactory() throws Exception {
		ConnectionFactory factory = manager.createConnectionFactory();
		assertNotNull(factory);
	}
}
