/**
 *
 */
package com.gojek.application;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * FIXME: Move this class to commons
 *
 * @author ganeshs
 *
 */
public class JaxrsExceptionMapper implements ExceptionMapper<ProcessingException> {

	@Override
	public Response toResponse(ProcessingException exception) {
		exception.printStackTrace();
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("error", "Oops... Something went wrong. Please try again later");
		return Response.serverError().type(MediaType.APPLICATION_JSON).entity(message).build();
	}
}
