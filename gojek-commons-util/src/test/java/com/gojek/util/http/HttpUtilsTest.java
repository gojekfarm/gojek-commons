package com.gojek.util.http;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * @author srinivas.iyengar
 *
 */
public class HttpUtilsTest {

	private String baseUri = "http://base.com";

	@Test
	public void testBuildUrlNullEmpty() {
		String nullBaseUrl = HttpUtils.buildUrl(null, null);
		String nullUrl = HttpUtils.buildUrl(baseUri, null);
		String emptyUrl = HttpUtils.buildUrl(baseUri, new HashMap<String, String>());
		assertNull(nullBaseUrl);
		assertEquals(baseUri, nullUrl);
		assertEquals(baseUri, emptyUrl);
	}

	@Test
	public void testBuildUrlParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("q1", "v1");
		params.put("q2", "v2");
		String expectedUrl = "http://base.com/?q1=v1&q2=v2";
		String url = HttpUtils.buildUrl(baseUri, params);
		assertEquals(url, expectedUrl);
	}

	@Test
	public void testBuildUrlMalformed() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("q1", "v1");
		params.put("q2", "v2");
		String uri = "randommalformedurl Q124hasdf";
		String actualUrl = HttpUtils.buildUrl(uri, params);
		assertNull(actualUrl);
	}

	@Test
	public void testGetTopLevelDomain() {
		String url = "https://www.adfs.domain.com";
		String domain = HttpUtils.getTopLevelDomainFromUrl(url);
		assertEquals(domain, "domain.com");
	}

	@Test
	public void testGetTopLevelDomainNull() {
		String url = "https://www.adfs.domain.com asdf";
		String domain = HttpUtils.getTopLevelDomainFromUrl(url);
		assertNull(domain);
	}

	@Test
	public void testGetTopLevelDomainUrlNull() {
		String domain = HttpUtils.getTopLevelDomainFromUrl(null);
		assertNull(domain);
	}
}
