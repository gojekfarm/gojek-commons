/**
 *
 */
package com.gojek.application;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;

import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.application.filter.RequestContext;

/**
 * @author ganeshs
 *
 */
public class LocalizedWebExceptionTest {
    
    @BeforeMethod
    public void setup() {
        RequestContext.instance().getContext().setLocale(Locale.getDefault());
    }
    
    @Test
    public void shouldSetHttpCodeAndMessage() {
        LocalizedWebException exception = new LocalizedWebException("some_code", "some_message_key", 202);
        assertEquals(exception.getResponse().getStatus(), 202);
        assertEquals(exception.getCode(), "some_code");
        assertEquals(exception.getMessage(), "some_message_key");
    }
    
    @Test
    public void shouldReturnKeyIfLocalizedValueNotFound() {
        LocalizedWebException exception = spy(new LocalizedWebException("some_code", "some_other_message_key", 200));
        doReturn("test_errors").when(exception).getBundleName();
        assertEquals(exception.getLocalizedMessage(), "some_other_message_key");
    }

    @Test
    public void shouldReturnKeyIfBundleNotFound() {
        LocalizedWebException exception = new LocalizedWebException("some_code", "some_message_key", 200);
        assertEquals(exception.getLocalizedMessage(), "some_message_key");
    }
    
    @Test
    public void shouldReturnLocalizedMessage() {
        LocalizedWebException exception = spy(new LocalizedWebException("some_code", "some_message_key", 200));
        doReturn("test_errors").when(exception).getBundleName();
        assertEquals(exception.getLocalizedMessage(), "Test Message Key");
    }
    
    @Test
    public void shouldReturnLocalizedMessageForLocale() {
        LocalizedWebException exception = spy(new LocalizedWebException("some_code", "some_message_key", 200));
        doReturn("test_errors").when(exception).getBundleName();
        RequestContext.instance().getContext().setLocale(Locale.GERMAN);
        assertEquals(exception.getLocalizedMessage(), "Test Message Key In DE");
    }
    
    @Test
    public void shouldReturnDefaultLocalizedMessageIfLocaleFileNotFound() {
        LocalizedWebException exception = spy(new LocalizedWebException("some_code", "some_message_key", 200));
        doReturn("test_errors").when(exception).getBundleName();
        RequestContext.instance().getContext().setLocale(Locale.FRENCH);
        assertEquals(exception.getLocalizedMessage(), "Test Message Key");
    }
}
