/**
 *
 */
package com.gojek.util.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * @author srinivas.iyengar
 *
 */
public class HttpClientFactory {

	private HttpClientConnectionManager connectionManager;

	private static final Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

	private static final int DEFAULT_MAX_REDIRECTS = 10;
	
	private int maxRedirects = DEFAULT_MAX_REDIRECTS;
	
	private static final String IE_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)";

	/**
	 * @return Create Basic Http Client.
	 */
	public HttpClient createClient() {
		return createClient(null);
	}

	/**
	 * Creates an instance of http client
	 *
	 * @param proxyConfig the proxy to use if provided
	 * @return HttpClient.
	 */
	public HttpClient createClient(ProxyConfig proxyConfig) {
		logger.debug("Creating HttpClient with Proxy {}", proxyConfig);
		HttpClientBuilder builder = HttpClients.custom().setConnectionManager(connectionManager).setUserAgent(IE_USER_AGENT);
		
		if (proxyConfig != null && !Strings.isNullOrEmpty(proxyConfig.getProxyHost()) && proxyConfig.getProxyPort() != null) {
			builder.setProxy(new HttpHost(proxyConfig.getProxyHost(), proxyConfig.getProxyPort()));

			CredentialsProvider provider = createCredentialsProvider(proxyConfig);
			if (provider != null) {
				builder.setDefaultCredentialsProvider(provider);
			}
		}

		RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(maxRedirects).build();
		builder.setDefaultRequestConfig(requestConfig);
		return builder.disableRedirectHandling().build();
	}

	/**
	 * Creates the credential provider from the proxy config
	 *
	 * @param config
	 * @return
	 */
	protected CredentialsProvider createCredentialsProvider(ProxyConfig config) {
		if (!Strings.isNullOrEmpty(config.getProxyUser())) {
			AuthScope authScope = new AuthScope(config.getProxyHost(), config.getProxyPort(), AuthScope.ANY_REALM);
			CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(authScope, new NTCredentials(config.getProxyUser(), config.getProxyPassword(), "", config.getProxyUserDomain()));
			return provider;
		}
		return null;
	}

	/**
	 * @return the maxRedirects
	 */
	public int getMaxRedirects() {
		return maxRedirects;
	}

	/**
	 * @param maxRedirects the maxRedirects to set
	 */
	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

	/**
	 * @return the connectionManager
	 */
	public HttpClientConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/**
	 * @param connectionManager the connectionManager to set
	 */
	public void setConnectionManager(HttpClientConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
}
