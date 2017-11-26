/**
 *
 */
package com.gojek.guice.jpa;

import java.util.concurrent.ThreadFactory;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaGuiceThreadFactory implements ThreadFactory {
    
    private ActiveJpaGuiceExecutionWrapper wrapper;
    
    /**
     * Default constructor
     */
    public ActiveJpaGuiceThreadFactory() {
        wrapper = new ActiveJpaGuiceExecutionWrapper();
    }
    
    /**
     * For unit testing
     *
     * @param wrapper
     */
    ActiveJpaGuiceThreadFactory(ActiveJpaGuiceExecutionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable) {
            @Override
            public void run() {
                wrapper.execute((input) -> {
                    super.run();
                    return null;
                });
            }
        };
    }
}
