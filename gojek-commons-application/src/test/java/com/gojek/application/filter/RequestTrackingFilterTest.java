package com.gojek.application.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author srinivas.iyengar
 *
 */
public class RequestTrackingFilterTest {

	private RequestTrackingFilter requestTrackingFilter;
	private ContainerRequestContext reqContext;
	private ContainerResponseContext resContext;
	private MultivaluedMap<String, Object> map = new MultivaluedHashMap<String, Object>();

	@BeforeClass
	public void setUp() {
		requestTrackingFilter = new RequestTrackingFilter();
		reqContext = mock(ContainerRequestContext.class);
		resContext = mock(ContainerResponseContext.class);
		when(resContext.getHeaders()).thenReturn(map);
	}

	@Test
	public void shouldAddMDCFromHeader() throws IOException {
		when(reqContext.getHeaderString(RequestTrackingFilter.TRANSACTION_KEY)).thenReturn("X-TX-KEY");
		when(reqContext.getHeaderString(RequestTrackingFilter.REQUEST_KEY)).thenReturn("X-REQ-KEY");

		requestTrackingFilter.filter(reqContext);

		Assert.assertEquals("X-TX-KEY", MDC.get(RequestTrackingFilter.TRANSACTION_KEY));
		Assert.assertEquals("X-REQ-KEY", MDC.get(RequestTrackingFilter.REQUEST_KEY));
	}

	@Test
	public void shouldAddDefaultMDCFromHeader() throws IOException {
		when(reqContext.getHeaderString(RequestTrackingFilter.TRANSACTION_KEY)).thenReturn("");
		when(reqContext.getHeaderString(RequestTrackingFilter.REQUEST_KEY)).thenReturn("");
		requestTrackingFilter.filter(reqContext);
		assertTrue(MDC.get(RequestTrackingFilter.TRANSACTION_KEY).startsWith("Txn-"));
		assertTrue(MDC.get(RequestTrackingFilter.REQUEST_KEY).startsWith("Req-"));
	}

	@Test
	public void shouldRemoveMDC() throws IOException {
		when(reqContext.getHeaderString(RequestTrackingFilter.TRANSACTION_KEY)).thenReturn("X-TX-KEY");
		when(reqContext.getHeaderString(RequestTrackingFilter.REQUEST_KEY)).thenReturn("X-REQ-KEY");

		requestTrackingFilter.filter(reqContext);
		assertEquals("X-TX-KEY", MDC.get(RequestTrackingFilter.TRANSACTION_KEY));
		assertEquals("X-REQ-KEY", MDC.get(RequestTrackingFilter.REQUEST_KEY));

		// Then Clear out in Response.
		requestTrackingFilter.filter(reqContext, resContext);

		assertEquals(resContext.getHeaders().get(RequestTrackingFilter.TRANSACTION_KEY).get(0), "X-TX-KEY");
		assertEquals(resContext.getHeaders().get(RequestTrackingFilter.REQUEST_KEY).get(0), "X-REQ-KEY");
		assertTrue(StringUtils.isEmpty(MDC.get(RequestTrackingFilter.TRANSACTION_KEY)));
		assertTrue(StringUtils.isEmpty(MDC.get(RequestTrackingFilter.REQUEST_KEY)));
	}
}
