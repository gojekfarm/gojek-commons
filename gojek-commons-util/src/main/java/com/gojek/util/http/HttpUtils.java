package com.gojek.util.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;

/**
 * @author srinivas.iyengar
 */
public class HttpUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * Builds the url by appending the query params with the uri
	 *
	 * @param baseUri
	 * @param params
	 *
	 * @return
	 */
	public static String buildUrl(String baseUri, Map<String, String> params) {

		if (params == null || params.isEmpty() || Strings.isNullOrEmpty(baseUri)) {
			return baseUri;
		}

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		for (Entry<String, String> entry : params.entrySet()) {
			parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(baseUri);
			uriBuilder.setParameters(parameters);
			return uriBuilder.toString();
		} catch (URISyntaxException e) {
			logger.error("Error occurred when building url", e);
		}
		return null;
	}

	/**
	 * <p> Gets the top level domain name from the url </p>
	 *
	 * Ex: The top level domain name for https://www.adfs.w2008sp2010q.com/adfs/ls/ is w2008sp2010q.com
	 *
	 * @param url
	 *
	 * @return
	 */
	public static String getTopLevelDomainFromUrl(String url) {
		logger.debug("Fetching domain from the url - {}", url);
		URI uri;
		try {
			if (Strings.isNullOrEmpty(url)) {
				return null;
			}
			uri = new URI(url);
			String domain = uri.getHost();
			return InternetDomainName.from(domain).topPrivateDomain().toString();
		} catch (URISyntaxException e) {
			logger.error("Error in parsing url - {}", url, e);
		}
		return null;
	}
}
