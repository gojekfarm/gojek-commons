/**
 *
 */
package com.gojek.application;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.application.filter.RequestContext;

/**
 * @author ganeshs
 *
 */
public class LocalizedWebException extends WebApplicationException {

    private static final String NAME_ERRORS = "errors";

    private String code;
    
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = LoggerFactory.getLogger(LocalizedWebException.class);
    
    /**
     * @param code
     * @param messageKey
     * @param status
     */
    public LocalizedWebException(String code, String messageKey, int status) {
        this(code, messageKey, null, status);
    }
    
    /**
     * @param code
     * @param messageKey
     * @param throwable
     * @param status
     */
    public LocalizedWebException(String code, String messageKey, Throwable throwable, int status) {
        super(messageKey, throwable, status);
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    
    @Override
    public String getLocalizedMessage() {
        ResourceBundle bundle = getBundle();
        if (bundle == null) {
            return super.getLocalizedMessage();
        }
        try {
            return bundle.getString(getMessage());
        } catch (Exception e) {
            logger.warn("Failed while getting the string for the key - " + getMessage(), e);
            return super.getLocalizedMessage();
        }
    }
    
    /**
     * @return
     */
    protected ResourceBundle getBundle() {
        Locale locale = RequestContext.instance().getContext().getLocale();
        try {
            return ResourceBundle.getBundle(getBundleName(), locale);
        } catch (Exception e) {
            logger.warn("Failed while getting the bundle for the locale - " + locale, e);
            return null;
        }
    }
    
    /**
     * Note: For testing
     *
     * @return
     */
    protected String getBundleName() {
        return NAME_ERRORS;
    }
}