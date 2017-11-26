/**
 *
 */
package com.gojek.application.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;

/**
 * @author ganeshs
 *
 */
public class RequestContextFilterTest {

	private RequestContextFilter requestContextFilter;

	private ContainerRequestContext request;

	private ContainerResponseContext response;

	@BeforeMethod
	public void setup() {
		requestContextFilter = new RequestContextFilter();
		request = mock(ContainerRequestContext.class);
		response = mock(ContainerResponseContext.class);
		when(request.getHeaderString(RequestContextFilter.USER_DEVICE_AGENT)).thenReturn("device info");
		when(request.getHeaderString(RequestContextFilter.CLIENT_IP)).thenReturn("dummy ip");
		when(request.getHeaderString(RequestContextFilter.USER_AGENT)).thenReturn("user agent");
		when(request.getHeaderString(HttpHeaders.ACCEPT_LANGUAGE)).thenReturn("id");
	}



	@Test
	public void shouldSetRequestContext() throws IOException, ServletException {
		requestContextFilter.filter(request);
		assertEquals(RequestContext.instance().getContext().getDeviceInfo(), "device info");
		assertEquals(RequestContext.instance().getContext().getClientIp(), "dummy ip");
		assertEquals(RequestContext.instance().getContext().getUserAgent(), "user agent");
		assertEquals(RequestContext.instance().getContext().getLocale().getDisplayLanguage(), new Locale("id").getDisplayLanguage());
	}

	@Test
	public void shouldResetRequestContext() throws IOException, ServletException {
		requestContextFilter.filter(request, response);
		assertNull(RequestContext.instance().getContext().getUserAgent());
	}

	@Test
	public void shouldSetDefaultLocaleIfHeaderIsNull() throws Exception {
		when(request.getHeaderString(HttpHeaders.ACCEPT_LANGUAGE)).thenReturn(null);
		requestContextFilter.filter(request);
		assertEquals(RequestContext.instance().getContext().getLocale().getDisplayLanguage(), Locale.getDefault().getDisplayLanguage());
	}

	@Test
	public void shouldSetDefaultLocaleIfHeaderHasInvalidLocaleString() throws Exception {
		when(request.getHeaderString(HttpHeaders.ACCEPT_LANGUAGE)).thenReturn("invalid string");
		requestContextFilter.filter(request);
		assertEquals(RequestContext.instance().getContext().getLocale().getDisplayLanguage(), Locale.getDefault().getDisplayLanguage());
	}
}
