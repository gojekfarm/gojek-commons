/**
 *
 */
package com.gojek.guice.jpa;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.guice.jpa.ManagedPersistService;
import com.google.inject.persist.PersistService;

/**
 * @author ganeshs
 *
 */
public class ManagedPersistServiceTest {

    private PersistService persistService;
    
    private ManagedPersistService managedPersistService;
    
    @BeforeMethod
    public void setup() {
        persistService = mock(PersistService.class);
        managedPersistService = new ManagedPersistService(persistService);
    }
    
    @Test
    public void shouldStartPersistService() {
        managedPersistService.start();
        verify(persistService).start();
    }
    
    @Test
    public void shouldStopPersistService() {
        managedPersistService.stop();
        verify(persistService).stop();
    }
}
