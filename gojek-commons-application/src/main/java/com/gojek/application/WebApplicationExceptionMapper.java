/**
 *
 */
package com.gojek.application;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author ganeshs
 *
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    
	@Override
	public Response toResponse(WebApplicationException exception) {
	    ErrorResponse response = null;
	    if (exception instanceof LocalizedWebException) {
	        String code = ((LocalizedWebException) exception).getCode();
	        response = new ErrorResponse(code, exception.getLocalizedMessage());
	    } else {
	        response = new ErrorResponse(exception.getLocalizedMessage());
	    }
		return Response.status(exception.getResponse().getStatusInfo()).
                entity(response).
                type(MediaType.APPLICATION_JSON).
                build();
	}

}