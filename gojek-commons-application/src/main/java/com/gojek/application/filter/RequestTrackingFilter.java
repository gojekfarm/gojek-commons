package com.gojek.application.filter;

import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * Filter for tracking Transaction Key and Request Key.
 *
 * @author srinivas.iyengar
 *
 */
public class RequestTrackingFilter implements ContainerRequestFilter, ContainerResponseFilter {

	public static final String TRANSACTION_KEY = "X-Transaction-Key";
	public static final String REQUEST_KEY = "X-Request-Key";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		//Set Headers for Response.
		responseContext.getHeaders().add(TRANSACTION_KEY, MDC.get(TRANSACTION_KEY));
		responseContext.getHeaders().add(REQUEST_KEY, MDC.get(REQUEST_KEY));
		// Remove MDC Keys.
		MDC.remove(TRANSACTION_KEY);
		MDC.remove(REQUEST_KEY);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String transactionId = requestContext.getHeaderString(TRANSACTION_KEY);
		String requestId = requestContext.getHeaderString(REQUEST_KEY);

		if (StringUtils.isEmpty(transactionId)) {
			transactionId = "Txn-" + UUID.randomUUID().toString();
		}

		if (StringUtils.isEmpty(requestId)) {
			requestId = "Req-" + UUID.randomUUID().toString();
		}

		MDC.put(TRANSACTION_KEY, transactionId);
		MDC.put(REQUEST_KEY, requestId);
	}
}
