/**
 *
 */
package com.gojek.application.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * A HTTP 422 (Unprocessable Entity) exception.
 *
 * @author ganeshs
 */
public class UnprocessableEntityException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    public static final StatusType UNPROCESSABLE_ENTITY = new StatusType() {
        @Override
        public int getStatusCode() {
            return 422;
        }

        @Override
        public String getReasonPhrase() {
            return "Unprocessable Entity";
        }

        @Override
        public Family getFamily() {
            return Family.CLIENT_ERROR;
        }
    };

    /**
     * Create a HTTP 422 (Unprocessable Entity) exception.
     */
    public UnprocessableEntityException() {
        super(Response.status(UNPROCESSABLE_ENTITY).type("text/plain").build());
    }

    /**
     * Create a 422 (Unprocessable Entity) exception.
     *
     * @param message
     *            the String that is the entity of the 422 response.
     */
    public UnprocessableEntityException(String message) {
        super(Response.status(UNPROCESSABLE_ENTITY).entity(message).type("text/plain").build());
    }
}
