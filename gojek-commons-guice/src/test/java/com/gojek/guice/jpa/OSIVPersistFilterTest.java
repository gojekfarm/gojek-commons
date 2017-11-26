/**
 * 
 */
package com.gojek.guice.jpa;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mockito.InOrder;
import org.testng.annotations.Test;

import com.google.inject.persist.UnitOfWork;

/**
 * @author ganeshs
 *
 */
public class OSIVPersistFilterTest {

    @Test
    public void shouldChainFilterAndThenEndUnitOfWork() throws Exception {
        UnitOfWork unitOfWork = mock(UnitOfWork.class);
        OSIVPersistFilter filter = new OSIVPersistFilter(unitOfWork);
        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        InOrder inOrder = inOrder(chain, unitOfWork);
        inOrder.verify(chain).doFilter(request, response);
        inOrder.verify(unitOfWork).end();
    }
}
