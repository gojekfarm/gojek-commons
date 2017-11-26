/**
 *
 */
package com.gojek.application;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class WebApplicationExceptionMapperTest {
    
    @Test
    public void shouldReturnLocalizedErrorResponse() {
        LocalizedWebException exception = spy(new LocalizedWebException("some_code", "some_message_key", 202));
        doReturn("test_errors").when(exception).getBundleName();
        WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();
        Response response = mapper.toResponse(exception);
        assertEquals(response.getStatus(), 202);
        assertEquals(response.getEntity(), new ErrorResponse("some_code", "Test Message Key"));
    }
    
    @Test
    public void shouldReturnErrorResponseForNonLocalizedException() {
        WebApplicationException exception = new BadRequestException("test_message");
        WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();
        Response response = mapper.toResponse(exception);
        assertEquals(response.getStatus(), 400);
        assertEquals(response.getEntity(), new ErrorResponse("test_message"));
    }
    
    @Test
    public void shouldReturnErrorResponseForNonLocalizedExceptionWithoutMessage() {
        WebApplicationException exception = new BadRequestException();
        WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();
        Response response = mapper.toResponse(exception);
        assertEquals(response.getStatus(), 400);
        assertEquals(response.getEntity(), new ErrorResponse("HTTP 400 Bad Request"));
    }
}
