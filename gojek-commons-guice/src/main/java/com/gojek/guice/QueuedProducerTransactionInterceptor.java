/**
 *
 */
package com.gojek.guice;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import com.gojek.core.event.QueuedProducer;
import com.gojek.guice.util.GuiceUtil;

/**
 * Interceptor class that intercepts transaction complete eveent to either flush or clear the producer queues
 *
 * @author ganeshs
 *
 */
public class QueuedProducerTransactionInterceptor extends EmptyInterceptor {
	
	private static final long serialVersionUID = 1L;
	
	private QueuedProducer<?> producer;
	
	/**
	 * Default constructor
	 */
	public QueuedProducerTransactionInterceptor() {
		this(GuiceUtil.getInstance(QueuedProducer.class));
	}
	
	/**
	 * @param producer
	 */
	public QueuedProducerTransactionInterceptor(QueuedProducer<?> producer) {
		this.producer = producer;
	}
	
	@Override
	public void afterTransactionCompletion(Transaction tx) {
		if (tx.getStatus() == TransactionStatus.COMMITTED) {
			producer.flush();
		} else {
			producer.clear();
		}
	}
}
