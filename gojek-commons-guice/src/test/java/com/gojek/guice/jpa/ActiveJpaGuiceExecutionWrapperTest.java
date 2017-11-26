/**
 *
 */
package com.gojek.guice.jpa;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.activejpa.jpa.JPAContext;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.persist.UnitOfWork;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaGuiceExecutionWrapperTest {
    
    private UnitOfWork work;
    
    private ActiveJpaGuiceExecutionWrapper wrapper;
    
    private JPAContext context;
    
    @BeforeMethod
    public void setup() {
        wrapper = spy(new ActiveJpaGuiceExecutionWrapper());
        context = mock(JPAContext.class);
        work = mock(UnitOfWork.class);
        when(context.getEntityManager()).thenReturn(mock(EntityManager.class));
        doReturn(context).when(wrapper).getContext();
        doReturn(work).when(wrapper).getUnitOfWork();
    }

    @Test
    public void shouldOpenEntityManagerOnExecute() {
        boolean status = wrapper.execute((optional) -> {
            verify(context).getEntityManager();
            return true;
        });
        assertTrue(status);
    }
    
    @Test
    public void shouldCloseUnitOfWorkAfterThreadRun() {
        boolean status = wrapper.execute((optional) -> {
            verify(context).getEntityManager();
            return true;
        });
        assertTrue(status);
        verify(work).end();
    }
    
    @Test
    public void shouldCloseUnitOfWorkEvenOnExeption() {
        boolean status = false;
        try {
            status = wrapper.execute((optional) -> {
                verify(context).getEntityManager();
                throw new RuntimeException();
            });
        } catch (Exception e) {
        }
        assertFalse(status);
        verify(work).end();
    }
    
    @Test
    public void shouldCloseOpenTxnAfterThreadRun() {
        when(context.isTxnOpen()).thenReturn(true);
        boolean status = wrapper.execute((optional) -> {
            verify(context).getEntityManager();
            return true;
        });
        assertTrue(status);
        verify(context).closeTxn(true);
    }
    
    @Test
    public void shouldCloseContextAfterUnitOfWorkIsEnd() {
        when(context.isTxnOpen()).thenReturn(true);
        boolean status = wrapper.execute((optional) -> {
            verify(context).getEntityManager();
            return true;
        });
        assertTrue(status);
        InOrder inOrder = Mockito.inOrder(work, context);
        inOrder.verify(work).end();
        inOrder.verify(context).close();
    }
    
    @Test
    public void shouldCloseContextEvenIfUnitOfWorkErrorsOut() {
        when(context.isTxnOpen()).thenReturn(true);
        doThrow(RuntimeException.class).when(work).end();
        boolean status = false;
        try {
            status = wrapper.execute((optional) -> {
                verify(context).getEntityManager();
                return false;
            });
        } catch (Exception e) {
        }
        assertFalse(status);
        verify(context).close();
    }
    
    @Test
    public void shouldNotCloseTxnAfterThreadRun() {
        when(context.isTxnOpen()).thenReturn(false);
        boolean status = wrapper.execute((optional) -> {
            verify(context).getEntityManager();
            return true;
        });
        assertTrue(status);
        verify(context, never()).closeTxn(true);
    }
}
