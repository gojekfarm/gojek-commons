package com.gojek.util.hystrix;

public class HystrixConfig {

    private String commandGroupKey;
    private String threadGroupKey;
    private int threadTimeoutInMillis = 1000;
    private Integer coreSize = 10;
    private Integer rollingStatsTimeInMillis = 10000;
    private Integer rollingStatsNumOfBuckets = 10;
    private Integer circuitBreakerSleepWindowInMillis = 5000;
    private Integer circuitBreakerErrorThresholdPercentage = 50;
    private Integer circuitBreakerRequestVolumeThreshold = 20;

    public String getCommandGroupKey() {
        return commandGroupKey;
    }

    public void setCommandGroupKey(String commandGroupKey) {
        this.commandGroupKey = commandGroupKey;
    }

    public String getThreadGroupKey() {
        return threadGroupKey;
    }

    public void setThreadGroupKey(String threadGroupKey) {
        this.threadGroupKey = threadGroupKey;
    }

    public int getThreadTimeoutInMillis() {
        return threadTimeoutInMillis;
    }

    public void setThreadTimeoutInMillis(int threadTimeoutInMillis) {
        this.threadTimeoutInMillis = threadTimeoutInMillis;
    }

    public Integer getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(Integer coreSize) {
        this.coreSize = coreSize;
    }

    public Integer getRollingStatsTimeInMillis() {
        return rollingStatsTimeInMillis;
    }

    public void setRollingStatsTimeInMillis(Integer rollingStatsTimeInMillis) {
        this.rollingStatsTimeInMillis = rollingStatsTimeInMillis;
    }

    public Integer getRollingStatsNumOfBuckets() {
        return rollingStatsNumOfBuckets;
    }

    public void setRollingStatsNumOfBuckets(Integer rollingStatsNumOfBuckets) {
        this.rollingStatsNumOfBuckets = rollingStatsNumOfBuckets;
    }

    public Integer getCircuitBreakerSleepWindowInMillis() {
        return circuitBreakerSleepWindowInMillis;
    }

    public void setCircuitBreakerSleepWindowInMillis(Integer circuitBreakerSleepWindowInMillis) {
        this.circuitBreakerSleepWindowInMillis = circuitBreakerSleepWindowInMillis;
    }

    public Integer getCircuitBreakerErrorThresholdPercentage() {
        return circuitBreakerErrorThresholdPercentage;
    }

    public void setCircuitBreakerErrorThresholdPercentage(Integer circuitBreakerErrorThresholdPercentage) {
        this.circuitBreakerErrorThresholdPercentage = circuitBreakerErrorThresholdPercentage;
    }

    public Integer getCircuitBreakerRequestVolumeThreshold() {
        return circuitBreakerRequestVolumeThreshold;
    }

    public void setCircuitBreakerRequestVolumeThreshold(Integer circuitBreakerRequestVolumeThreshold) {
        this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
    }

}
