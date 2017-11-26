/**
 * 
 */
package com.gojek.guice.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * @author ganeshs
 *
 */
public class GuiceUtilTest {

    @Test
    public void shouldLoadInjector() {
        Injector injector = mock(Injector.class);
        when(injector.getInstance(GuiceUtilTest.class)).thenReturn(mock(GuiceUtilTest.class));
        GuiceUtil.load(injector);
        assertNotNull(GuiceUtil.getInstance(GuiceUtilTest.class));
    }
    
    @Test
    public void shouldNotLoadInjectorIfAlreadyLoaded() {
        Injector injector = mock(Injector.class);
        GuiceUtil.load(injector);
        Injector injector1 = mock(Injector.class);
        GuiceUtilTest obj = mock(GuiceUtilTest.class);
        when(injector1.getInstance(GuiceUtilTest.class)).thenReturn(obj);
        GuiceUtil.load(injector1);
        assertNotEquals(GuiceUtil.getInstance(GuiceUtilTest.class), obj);
    }
    
    @Test
    public void shouldGetInstance() {
        Injector injector = mock(Injector.class);
        GuiceUtilTest obj = mock(GuiceUtilTest.class);
        when(injector.getInstance(GuiceUtilTest.class)).thenReturn(obj);
        GuiceUtil.load(injector);
        assertEquals(GuiceUtil.getInstance(GuiceUtilTest.class), obj);
    }
}
