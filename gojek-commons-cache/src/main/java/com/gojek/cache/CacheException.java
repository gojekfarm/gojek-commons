package com.gojek.cache;

import com.gojek.core.CoreException;

public class CacheException extends CoreException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     * @param cause
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CacheException(Throwable cause) {
        super(cause);
    }

}
