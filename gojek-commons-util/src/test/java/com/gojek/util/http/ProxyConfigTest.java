package com.gojek.util.http;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * @author srinivas.iyengar
 *
 */
public class ProxyConfigTest {

	private String proxyHost = "proxyHost";
	private String proxyPort = "8080";
	private String proxyUser = "domain\\user";
	private String proxyPassword = "password";

	@Test
	public void testBuildProxyConfig() {
		proxyPort = "8080";
		proxyUser = "domain\\user";

		ProxyConfig proxyConfig = new ProxyConfig(proxyHost, proxyPort, proxyUser, proxyPassword);
		assertNotNull(proxyConfig);
		assertEquals(proxyConfig.getProxyHost(), proxyHost);
		assertEquals(proxyConfig.getProxyPort(), new Integer(8080));
		assertEquals(proxyConfig.getProxyUser(), "user");
		assertEquals(proxyConfig.getProxyUserDomain(), "domain");
	}

	@Test
	public void testBuildProxyConfigNoDomain() {
		proxyPort = "8080";
		proxyUser = "user";

		ProxyConfig proxyConfig = new ProxyConfig(proxyHost, proxyPort, proxyUser, proxyPassword);
		assertNotNull(proxyConfig);
		assertEquals(proxyConfig.getProxyHost(), proxyHost);
		assertEquals(proxyConfig.getProxyPort(), new Integer(8080));
		assertEquals(proxyConfig.getProxyUser(), "user");
		assertEquals(proxyConfig.getProxyUserDomain(), "");
	}

	@Test
	public void testBuildProxyConfigNoUser() {
		proxyPort = "";
		proxyUser = "";

		ProxyConfig proxyConfig = new ProxyConfig(proxyHost, proxyPort, proxyUser, proxyPassword);
		assertNotNull(proxyConfig);
		assertEquals(proxyConfig.getProxyHost(), proxyHost);
		assertNull(proxyConfig.getProxyPort());
		assertEquals(proxyConfig.getProxyUser(), "");
		assertEquals(proxyConfig.getProxyUserDomain(), "");
	}
}
