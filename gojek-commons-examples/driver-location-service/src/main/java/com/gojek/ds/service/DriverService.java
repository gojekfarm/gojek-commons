/**
 * 
 */
package com.gojek.ds.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Inject;

import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.gojek.application.ConflictException;
import com.gojek.cache.redis.AtomicExecutor;
import com.gojek.cache.redis.JedisConnection;
import com.gojek.cache.redis.LockException;
import com.gojek.core.event.CoreEventBus;
import com.gojek.ds.domain.Driver;
import com.gojek.ds.events.DriverActiveEvent;
import com.gojek.ds.events.DriverBusyEvent;
import com.gojek.ds.events.DriverEvent;
import com.gojek.ds.events.DriverInActiveEvent;
import com.google.inject.persist.Transactional;

/**
 * @author ganeshs
 *
 */
public class DriverService {
    
    private AtomicExecutor<Void> atomicExecutor;
    
    public static final String LOCK_DRIVER_KEY_PREFIX = "lock_driver_key_";
    
    public static final int LOCK_MAX_RETRIES = 10;
    
    public static final int LOCK_RETRY_INTERVAL_IN_MILLIS = 10;
    
    public static final int LOCK_EXPIRY_TIME_IN_SECONDS = 10;
    
    @Inject
    public DriverService(JedisConnection jedisConnection) {
        this.atomicExecutor = new AtomicExecutor<Void>(jedisConnection,
                RetryerBuilder.<Boolean> newBuilder().withStopStrategy(StopStrategies.stopAfterAttempt(LOCK_MAX_RETRIES))
                        .withWaitStrategy(WaitStrategies.fixedWait(LOCK_RETRY_INTERVAL_IN_MILLIS, TimeUnit.MILLISECONDS))
                        .retryIfResult((result) -> {
                            return !result;
                        }).build(), LOCK_EXPIRY_TIME_IN_SECONDS);
    }

    /**
     * Marks the driver as active and raises event
     * 
     * @param driver
     */
    @Transactional
    public void markActive(Driver driver) {
        atomicExecute(driver, (optional) -> {
            driver.markActive();
            return null;
        });
        raiseEvent(new DriverActiveEvent(driver));
    }
    
    /**
     * Marks the driver as inactive and raises event
     * 
     * @param driver
     */
    @Transactional
    public void markInactive(Driver driver) {
        atomicExecute(driver, (optional) -> {
            driver.markInactive();
            return null;
        });
        raiseEvent(new DriverInActiveEvent(driver));
    }
    
    /**
     * Marks the driver as busy and raises event
     * 
     * @param driver
     * @param jobId
     */
    @Transactional
    public void markBusy(Driver driver, String jobId) {
        atomicExecute(driver, (optional) -> {
            driver.markBusy();
            driver.persist();
            return null;
        });
        raiseEvent(new DriverBusyEvent(driver, jobId));
    }
    
    /**
     * @param event
     */
    private void raiseEvent(DriverEvent event) {
        CoreEventBus.instance().post(event);
    }
    
    /**
     * Execute the given block atomically
     * 
     * @param key
     * @param function
     * @return
     */
    protected void atomicExecute(Driver driver, Function<Optional<Void>, Void> function) {
        try {
            atomicExecutor.execute(LOCK_DRIVER_KEY_PREFIX + driver.getId(), function);
        } catch (LockException e) {
            throw new ConflictException();
        }
    }
}
