package com.gojek.util.hystrix;

import static com.google.common.collect.ImmutableSortedMap.of;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HystrixWrappedCommandTest {

    private HystrixConfig hystrixConfig;

    @BeforeMethod
    public void setUp() throws Exception {
        hystrixConfig = new HystrixConfig();
        hystrixConfig.setThreadGroupKey("thread group key");
        hystrixConfig.setCommandGroupKey("command group key");
    }

    @Test
    public void shouldExecuteGivenRunImplementation() throws Exception {
        HystrixWrappedCommand<Map<String, Boolean>> command = new HystrixWrappedCommand<>(hystrixConfig,
                () -> of("run", true), () -> of("fallback", true));
        Map<String, Boolean> response = command.execute();
        assertTrue(response.get("run"));
    }

    @Test
    public void shouldExecuteGivenFallbackImplementationWhenRunFails() throws Exception {
        HystrixWrappedCommand<Map<String, Boolean>> command = new HystrixWrappedCommand<>(hystrixConfig,
                () -> {throw new RuntimeException();}, () -> of("fallback", true));
        Map<String, Boolean> response = command.execute();
        assertTrue(response.get("fallback"));
    }
}