/**
 *
 */
package com.gojek.guice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.core.event.QueuedProducer;

/**
 * @author ganeshs
 *
 */
public class QueuedProducerTransactionInterceptorTest {
	
	private QueuedProducerTransactionInterceptor interceptor;
	
	private QueuedProducer producer;
	
	@BeforeMethod
	public void setup() {
		producer = mock(QueuedProducer.class);
		interceptor = new QueuedProducerTransactionInterceptor(producer);
	}

	@Test
	public void shouldFlushQueueOnTransactionCommit() {
		Transaction txn = mock(Transaction.class);
		when(txn.getStatus()).thenReturn(TransactionStatus.COMMITTED);
		interceptor.afterTransactionCompletion(txn);
		verify(producer).flush();
	}
	
	@Test
	public void shouldClearQueueOnTransactionRollback() {
		Transaction txn = mock(Transaction.class);
		when(txn.getStatus()).thenReturn(TransactionStatus.ROLLED_BACK);
		interceptor.afterTransactionCompletion(txn);
		verify(producer).clear();
	}
}
