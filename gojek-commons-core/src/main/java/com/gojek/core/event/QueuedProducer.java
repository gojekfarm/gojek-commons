/**
 *
 */
package com.gojek.core.event;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Messages are stored in a in-memory queue at thread level. Users should make sure to call flush explicitly to actually send the messages.<br>
 * Usecase: This implementation is quite useful to queue messages in a transactional scope and send them only when commit is successful.<br>
 *
 * Note: If the transaction is successfully commited but amqp send fails, we don't retry.
 *
 * @author ganeshs
 */
public abstract class QueuedProducer implements Producer {
	
	private ThreadLocal<Map<Destination, List<Event>>> threadLocal = new ThreadLocal<>();

	/**
	 * @param event
	 * @param destination
	 */
	public void send(Event event, Destination destination) {
		List<Event> queue = getQueue(destination);
		queue.add(event);
	}
	
	/**
	 * Flushes the events in this thread and clears them
	 */
	public void flush() {
		flushInternal();
		clear();
	}
	
	/**
	 * Flushes the events in this thread
	 */
	protected abstract void flushInternal();
	
	/**
	 * Clears the messages in the queue
	 */
	public void clear() {
		threadLocal.remove();
	}
	
	/**
	 * @return
	 */
	protected Map<Destination, List<Event>> getQueues() {
		Map<Destination, List<Event>> queues = threadLocal.get();
		if (queues == null) {
			queues = Maps.newHashMap();
			threadLocal.set(queues);
		}
		return queues;
	}

	/**
	 * @param destination
	 * @return
	 */
	public List<Event> getQueue(Destination destination) {
		Map<Destination, List<Event>> queues = getQueues();
		List<Event> queue = queues.get(destination);
		if (queue == null) {
			queue = Lists.newArrayList();
			queues.put(destination, queue);
		}
		return queue;
	}
}
