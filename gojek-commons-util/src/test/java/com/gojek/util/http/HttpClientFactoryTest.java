package com.gojek.util.http;


import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author srinivas.iyengar
 */
public class HttpClientFactoryTest {

	private HttpClientFactory httpClientFactory;

	private ProxyConfig proxyConfig;

	private HttpClientConnectionManager httpConnectionManager;

	@BeforeClass
	public void beforeClass() {
		httpClientFactory = new HttpClientFactory();
		httpConnectionManager = mock(HttpClientConnectionManager.class);
		httpClientFactory.setConnectionManager(httpConnectionManager);
		proxyConfig = new ProxyConfig("host", "8080", "user", "password");
	}

	@Test
	public void testCreateClientWithProxyCredentials() {
		proxyConfig = new ProxyConfig("host", "8080", "user", "password");
		HttpClient client = httpClientFactory.createClient(proxyConfig);
		assertNotNull(client);
	}

	@Test
	public void testCreateClientWithNoCreds() {
		proxyConfig = new ProxyConfig("host", "8080", "", "");
		HttpClient client = httpClientFactory.createClient(proxyConfig);
		assertNotNull(client);
	}

	@Test
	public void testCreateClientWithoutProxy() {
		HttpClient client = httpClientFactory.createClient();
		assertNotNull(client);
	}
}
