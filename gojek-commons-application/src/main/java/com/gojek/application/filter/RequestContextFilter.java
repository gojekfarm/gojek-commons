/**
 *
 */
package com.gojek.application.filter;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;

import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.application.filter.RequestContext.Context;
import com.gojek.application.filter.RequestContext.Context.AccountType;
import com.gojek.application.filter.RequestContext.Context.Role;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

/**
 * @author ganeshs
 */
@PreMatching
@Priority(Integer.MIN_VALUE)
public class RequestContextFilter implements ContainerRequestFilter, ContainerResponseFilter {

	public static final String USER_DEVICE_AGENT = "X-Device-Info";

	public static final String APPLICATION_LICENSE_KEY = "X-Application-License-Key";

	public static final String USER_AGENT = "User-Agent";

	public static final String CLIENT_IP = "Client-IP";
	
	public static final String PARTNER_ID = "X-Partner-Id";
	
	public static final String MERCHANT_ID = "X-Merchant-Id";
	
	public static final String MERCHANT_BRANCH_ID = "X-Merchant-Branch-Id";
	
	public static final String USER_ID = "X-User-Id";
	
	public static final String USER_ROLE = "X-User-Role";
	
	public static final String INTERNAL_USER_ID = "system_user";

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestContextFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		LOGGER.trace("Clearing the request context information for the request {}", responseContext);
		RequestContext.instance().clear();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOGGER.trace("Populating the request context information for the request {}", requestContext);

		Context context = RequestContext.instance().getContext();
		String deviceInfo = requestContext.getHeaderString(USER_DEVICE_AGENT);
		String userAgent = requestContext.getHeaderString(USER_AGENT);
		String clientIp = requestContext.getHeaderString(CLIENT_IP);
		String appLicenseKey = requestContext.getHeaderString(APPLICATION_LICENSE_KEY);
		String partnerId = requestContext.getHeaderString(PARTNER_ID);
		String merchantId = requestContext.getHeaderString(MERCHANT_ID);
		String merchantBranchId = requestContext.getHeaderString(MERCHANT_BRANCH_ID);
		String userId = requestContext.getHeaderString(USER_ID);
		String role = requestContext.getHeaderString(USER_ROLE);
		String localeString = requestContext.getHeaderString(HttpHeaders.ACCEPT_LANGUAGE);
		
		if (! Strings.isNullOrEmpty(partnerId)) {
			context.setAccountType(AccountType.partner);
		} else if (! Strings.isNullOrEmpty(merchantId)) {
			context.setAccountType(AccountType.merchant);
		} else {
			if (! Strings.isNullOrEmpty(userId) && userId.equals(INTERNAL_USER_ID)) {
				context.setAccountType(AccountType.internal);
			} else {
				context.setAccountType(AccountType.consumer);
			}
		}
		
		if (! Strings.isNullOrEmpty(role)) {
			try {
				context.setRole(Role.valueOf(role));
			} catch (Exception e) {
				LOGGER.info("Failed while resolving the role", e);
				// Suppress exception
			}
		}

		if (! Strings.isNullOrEmpty(localeString)) {
			Locale locale = new Locale(localeString);
			if(LocaleUtils.isAvailableLocale(locale)) {
				context.setLocale(locale);
			}
		}
		context.setClientIp(clientIp);
		context.setUserAgent(userAgent);
		context.setDeviceInfo(deviceInfo);
		context.setApplicationLicenseKey(appLicenseKey);
		context.setMerchantId(merchantId);
		context.setMerchantBranchId(merchantBranchId);
		context.setUserId(userId);
		context.setPartnerId(partnerId);
	}
}
