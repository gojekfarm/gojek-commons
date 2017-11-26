/**
 *
 */
package com.gojek.application;

/**
 * @author ganeshs
 *
 */
public class ErrorResponse {

    private Error error;
    
    /**
     * @param message
     */
    public ErrorResponse(String message) {
        this.error = new Error(message);
    }
    
    /**
     * @param code
     * @param message
     */
    public ErrorResponse(String code, String message) {
        this.error = new Error(code, message);
    }
    
    /**
     * @return the error
     */
    public Error getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ErrorResponse other = (ErrorResponse) obj;
        if (error == null) {
            if (other.error != null)
                return false;
        } else if (!error.equals(other.error))
            return false;
        return true;
    }

    /**
     * @author ganeshs
     *
     */
    public static class Error {
        
        private String code;
        
        private String message;
        
        /**
         * @param message
         */
        public Error(String message) {
            this.message = message;
        }

        /**
         * @param code
         * @param message
         */
        public Error(String code, String message) {
            super();
            this.code = code;
            this.message = message;
        }

        /**
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code the code to set
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((code == null) ? 0 : code.hashCode());
            result = prime * result + ((message == null) ? 0 : message.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Error other = (Error) obj;
            if (code == null) {
                if (other.code != null)
                    return false;
            } else if (!code.equals(other.code))
                return false;
            if (message == null) {
                if (other.message != null)
                    return false;
            } else if (!message.equals(other.message))
                return false;
            return true;
        }
    }
}
