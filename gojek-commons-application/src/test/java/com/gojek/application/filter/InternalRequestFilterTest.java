/**
 *
 */
package com.gojek.application.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class InternalRequestFilterTest {

    private ContainerRequestContext request;

    private UriInfo uriInfo;
    
    private InternalRequestFilter internalRequestFilter;

    @BeforeMethod
    public void setup() throws Exception {
        internalRequestFilter = new InternalRequestFilter("internal/");
        request = mock(ContainerRequestContext.class);
        when(request.getHeaders()).thenReturn(new MultivaluedHashMap<>());
        uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(new URI("/tms/"));
        when(uriInfo.getPath()).thenReturn("internal/v1/some_resources/id");
        when(request.getUriInfo()).thenReturn(uriInfo);
    }
    
    @Test
    public void shouldTrimInternalUrlPrefix() throws Exception {
        UriBuilder builder = mock(UriBuilder.class);
        when(uriInfo.getRequestUriBuilder()).thenReturn(builder);
        internalRequestFilter.filter(request);
        verify(builder).replacePath("/tms/v1/some_resources/id");
    }
    
    @Test
    public void shouldRemoveInternaUserIdHeaderForNonInternalRequests() throws Exception {
        UriBuilder builder = mock(UriBuilder.class);
        when(uriInfo.getRequestUriBuilder()).thenReturn(builder);
        when(uriInfo.getPath()).thenReturn("v1/some_resources/id");
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.put(RequestContextFilter.USER_ID, Lists.newArrayList(RequestContextFilter.INTERNAL_USER_ID));
        when(request.getHeaders()).thenReturn(map);
        internalRequestFilter.filter(request);
        assertFalse(map.containsKey(RequestContextFilter.USER_ID));
    }
    
    @Test
    public void shouldAddInternaUserIdHeaderForInternalRequests() throws Exception {
        UriBuilder builder = mock(UriBuilder.class);
        when(uriInfo.getRequestUriBuilder()).thenReturn(builder);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        when(request.getHeaders()).thenReturn(map);
        internalRequestFilter.filter(request);
        assertTrue(map.containsKey(RequestContextFilter.USER_ID));
    }
}
