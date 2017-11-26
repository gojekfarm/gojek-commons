/**
 *
 */
package com.gojek.guice.jpa;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaGuiceThreadFactoryTest {

    private ActiveJpaGuiceExecutionWrapper wrapper;
    
    private ActiveJpaGuiceThreadFactory factory;
    
    @BeforeMethod
    public void setup() {
        wrapper = mock(ActiveJpaGuiceExecutionWrapper.class);
        factory = new ActiveJpaGuiceThreadFactory(wrapper);
    }
 
    @Test
    public void shouldRunNewThread() throws Exception {
        Thread thread = factory.newThread(new Runnable() {
            @Override
            public void run() {
            }
        });
        thread.run();
        thread.join();
        verify(wrapper).execute(any(Function.class));
    }
    
}
