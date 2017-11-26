/**
 *
 */
package com.gojek.util.serializer;

import com.gojek.core.CoreException;

/**
 * @author ganeshs
 */
public class SerializerException extends CoreException {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public SerializerException() {
    }

    /**
     * @param message
     * @param cause
     */
    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SerializerException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public SerializerException(Throwable cause) {
        super(cause);
    }

}
