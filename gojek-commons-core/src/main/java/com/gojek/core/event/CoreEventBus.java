/**
 *
 */
package com.gojek.core.event;

import com.google.common.eventbus.EventBus;

/**
 * @author ganeshs
 *
 */
public class CoreEventBus {

	private EventBus eventBus = new EventBus(CoreEventBus.class.getSimpleName());
	
    private static final CoreEventBus instance = new CoreEventBus();
	
	/**
	 * Singleton constructor
	 */
	private CoreEventBus() {
	}
	
	/**
	 * Returns singleton instance
	 *
	 * @return
	 */
	public static CoreEventBus instance() {
		return instance;
	}
	
    public void post(Event event) {
        eventBus.post(event);
    }

    public void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    public void unregister(Object subscriber) {
        eventBus.unregister(subscriber);
    }
}
