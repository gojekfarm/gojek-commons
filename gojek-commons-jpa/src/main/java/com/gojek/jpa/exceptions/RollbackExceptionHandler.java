/**
 *
 */
package com.gojek.jpa.exceptions;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ganeshs
 *
 */
public class RollbackExceptionHandler implements ExceptionMapper<RollbackException> {
	
	private static final Logger logger = LoggerFactory.getLogger(RollbackExceptionHandler.class);
	
	@Override
	public Response toResponse(RollbackException exception) {
		logger.error("Handling the exception", exception);
		if (exception.getCause() != null) {
			if (exception.getCause() instanceof ConstraintViolationException) {
				return new ConstraintViolationExceptionHandler().toResponse((ConstraintViolationException) exception.getCause());
			}
		}
		return Response.serverError().build();
	}

}
