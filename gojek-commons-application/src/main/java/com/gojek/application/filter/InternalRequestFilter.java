/**
 *
 */
package com.gojek.application.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Note: This filter has to be put before any other filter. This should take preference over {@link RequestContextFilter} as well
 *
 * @author ganeshs
 *
 */
@PreMatching
@Priority(Integer.MIN_VALUE)
public class InternalRequestFilter implements ContainerRequestFilter {
    
    private String internalUrlPrefix;
    
    public static final Logger logger = LoggerFactory.getLogger(InternalRequestFilter.class);
    
    /**
     * @param internalUrlPrefix
     */
    public InternalRequestFilter(String internalUrlPrefix) {
        this.internalUrlPrefix = internalUrlPrefix;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        preProcess(requestContext);
        boolean internalRequest = process(requestContext);
        postProcess(requestContext, internalRequest);
    }
    
    /**
     * Check if the request has system user header. If so remove that
     *
     * @param requestContext
     */
    private void preProcess(ContainerRequestContext requestContext) {
        String userId = requestContext.getHeaders().getFirst(RequestContextFilter.USER_ID);
        if (! Strings.isNullOrEmpty(userId) && userId.equalsIgnoreCase(RequestContextFilter.INTERNAL_USER_ID)) {
            logger.info("SECURITY ALERT: Request has system user header. Removing it");
            requestContext.getHeaders().remove(RequestContextFilter.USER_ID);
        }
    }
    
    /**
     * Rewrite the internal request URI
     *  
     * @param requestContext
     * @return true if internal request
     */
    private boolean process(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (! matchesPrefix(path)) {
            return false;
        }
        String basePath = requestContext.getUriInfo().getBaseUri().getPath();
        UriBuilder builder = requestContext.getUriInfo().getRequestUriBuilder();
        builder.replacePath(basePath + path.substring(internalUrlPrefix.length()));
        requestContext.setRequestUri(builder.build());
        return true;
    }
    
    /**
     * Add the system user header
     *
     * @param requestContext
     * @param internalRequest
     */
    private void postProcess(ContainerRequestContext requestContext, boolean internalRequest) {
        if (internalRequest) {
            requestContext.getHeaders().add(RequestContextFilter.USER_ID, RequestContextFilter.INTERNAL_USER_ID);
        }
    }
    
    /**
     * @return
     */
    private boolean matchesPrefix(String path) {
        return path.startsWith(internalUrlPrefix);
    }

}
