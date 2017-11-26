/**
 *
 */
package com.gojek.util.http;

import java.util.Iterator;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class ProxyConfig {

	private String proxyHost;

	private String proxyPassword;

	private Integer proxyPort;

	private String proxyUser;

	private String proxyUserDomain = "";

	/**
	 * Windows domain separator
	 */
	private static final char DOMAIN_SEPARATOR = '\\';

	/**
	 * @param proxyHost
	 * @param proxyPort
	 * @param proxyUser
	 * @param proxyPassword
	 */
	public ProxyConfig(String proxyHost, String proxyPort, String proxyUser, String proxyPassword) {
		this.proxyHost = proxyHost;
		this.proxyPassword = proxyPassword;
		if (!Strings.isNullOrEmpty(proxyPort)) {
			this.proxyPort = Integer.parseInt(proxyPort);
		}

		if (!Strings.isNullOrEmpty(proxyUser) && proxyUser.contains(DOMAIN_SEPARATOR + "")) {
			Iterator<String> splits = Splitter.on(DOMAIN_SEPARATOR).omitEmptyStrings().split(proxyUser).iterator();
			this.proxyUserDomain = splits.next();
			if (splits.hasNext()) {
				this.proxyUser = splits.next();
			}
		} else {
			this.proxyUser = proxyUser;
		}
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @return the proxyPassword
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * @return the proxyPort
	 */
	public Integer getProxyPort() {
		return proxyPort;
	}

	/**
	 * @return the proxyUser
	 */
	public String getProxyUser() {
		return proxyUser;
	}

	/**
	 * @return the proxyUserDomain
	 */
	public String getProxyUserDomain() {
		return proxyUserDomain;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProxyConfig [proxyHost=" + proxyHost + ", proxyPassword=[password] , proxyPort=" + proxyPort + ", proxyUser=" + proxyUser
				+ ", proxyUserDomain=" + proxyUserDomain + "]";
	}

}