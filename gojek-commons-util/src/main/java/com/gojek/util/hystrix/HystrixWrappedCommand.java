package com.gojek.util.hystrix;

import static java.util.Objects.isNull;

import java.util.function.Supplier;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class HystrixWrappedCommand<T> extends HystrixCommand<T> {

    private final Supplier<T> run;
    private final Supplier<T> fallback;

    public HystrixWrappedCommand(HystrixConfig hystrixConfig) {
        this(hystrixConfig, null, null);
    }

    public HystrixWrappedCommand(HystrixConfig hystrixConfig, Supplier<T> run, Supplier<T> fallback) {
        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixConfig.getCommandGroupKey()))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(hystrixConfig.getThreadGroupKey()))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(hystrixConfig.getCoreSize())
                        .withMetricsRollingStatisticalWindowInMilliseconds(hystrixConfig.getRollingStatsTimeInMillis()))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationThreadTimeoutInMilliseconds(hystrixConfig.getThreadTimeoutInMillis())
                        .withCircuitBreakerErrorThresholdPercentage(hystrixConfig.getCircuitBreakerErrorThresholdPercentage())
                        .withCircuitBreakerRequestVolumeThreshold(hystrixConfig.getCircuitBreakerRequestVolumeThreshold())
                        .withCircuitBreakerSleepWindowInMilliseconds(hystrixConfig.getCircuitBreakerSleepWindowInMillis())));
        this.run = run;
        this.fallback = fallback;
    }

    @Override
    protected T run() throws Exception {
        if(isNull(this.run)) {
            return null;
        }
        return this.run.get();
    }

    @Override
    protected T getFallback() {
        if(isNull(this.fallback)) {
            return null;
        }
        return this.fallback.get();
    }
}
