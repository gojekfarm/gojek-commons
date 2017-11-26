/**
 *
 */
package com.gojek.application;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 * @author ganeshs
 *
 */
public class ConflictException extends ClientErrorException {

    private static final long serialVersionUID = -6820866117511628388L;

    /**
     * Construct a new "conflict" exception.
     */
    public ConflictException() {
        super(Response.Status.CONFLICT);
    }

    /**
     * Construct a new "conflict" exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }

    /**
     * Construct a new "conflict" exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public ConflictException(Throwable cause) {
        super(Response.Status.CONFLICT, cause);
    }

    /**
     * Construct a new "conflict" exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     */
    public ConflictException(String message, Throwable cause) {
        super(message, Response.Status.CONFLICT, cause);
    }
}