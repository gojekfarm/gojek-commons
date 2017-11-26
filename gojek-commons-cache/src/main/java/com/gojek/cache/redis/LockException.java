/**
 *
 */
package com.gojek.cache.redis;

import com.gojek.core.CoreException;

/**
 * @author ganeshs
 *
 */
public class LockException extends CoreException {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public LockException() {
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public LockException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public LockException(Throwable cause) {
        super(cause);
    }

}
