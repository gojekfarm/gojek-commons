/**
 * 
 */
package com.gojek.job;

import com.gojek.core.CoreException;

/**
 * @author ganeshs
 *
 */
public class JobException extends CoreException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     * @param cause
     */
    public JobException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public JobException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public JobException(Throwable cause) {
        super(cause);
    }
}
