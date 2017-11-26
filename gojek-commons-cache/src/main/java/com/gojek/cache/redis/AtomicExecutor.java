/**
 *
 */
package com.gojek.cache.redis;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * <p>Executes a function block atomically. The atomicity is ensured by acquiring a lock on redis for the given key</p>
 *
 * @author ganeshs
 *
 */
public class AtomicExecutor<T> {

    private JedisConnection connection;
    
    private Retryer<Boolean> lockRetryer;
    
    private Integer lockExpiryTimeInSecs;
    
    private static final ThreadLocal<Set<String>> locks = new ThreadLocal<>();
    
    /**
     * @param connection
     * @param lockRetryer
     * @param lockExpiryTimeInSecs
     */
    public AtomicExecutor(JedisConnection connection, Retryer<Boolean> lockRetryer, Integer lockExpiryTimeInSecs) {
        this.connection = connection;
        this.lockRetryer = lockRetryer;
        this.lockExpiryTimeInSecs = lockExpiryTimeInSecs;
    }
    
    /**
     * @param lockKey
     * @param function
     * @return
     */
    public T execute(String lockKey, Function<Optional<Void>, T> function) {
        boolean locked = false;
        boolean owner = false;
        try {
            if (! isReentrant(lockKey)) {
                locked = this.lockRetryer.call(() -> {
                    return lock(lockKey);
                });
                if (! locked) {
                    throw new LockException("Unable to acquire a lock on the key - " + lockKey);
                }
                addLockKey(lockKey);
                owner = true;
            } else {
                locked = true;
            }
            return function.apply(Optional.empty());
        } catch (RetryException | ExecutionException e) {
            throw new LockException("Unable to acquire a lock on the key - " + lockKey, e);
        } finally {
            if (locked && owner) {
                removeLockKey(lockKey);
                release(lockKey);
            }
        }
    }
    
    /**
     * Check if this is a re-entrant key
     *
     * @param lockKey
     * @return
     */
    private boolean isReentrant(String lockKey) {
        Set<String> lockKeys = locks.get();
        return lockKeys != null && lockKeys.contains(lockKey);
    }
    
    /**
     * Adds the lock key to the thread local
     *
     * @param lockKey
     */
    private void addLockKey(String lockKey) {
        Set<String> lockKeys = locks.get();
        if (lockKeys == null) {
            lockKeys = Sets.newHashSet();
            locks.set(lockKeys);
        }
        lockKeys.add(lockKey);
    }
    
    /**
     * Removes the lock key from the thread local 
     *
     * @param lockKey
     */
    private void removeLockKey(String lockKey) {
        Set<String> lockKeys = locks.get();
        if (lockKeys != null) {
            lockKeys.remove(lockKey);
        }
    }

    /**
     * Acquire a lock on the lockKey for {@link AtomicExecutor#lockExpiryTimeInSecs} duration
     *
     * @param lockKey
     * @return
     */
    protected boolean lock(String lockKey) {
        return connection.execute((connection) -> {
            String result = connection.set(lockKey, lockKey, "NX", "EX", lockExpiryTimeInSecs);
            return !Strings.isNullOrEmpty(result) && result.equals("OK");
        });
    }

    /**
     * Release the lock on the lockKey
     *
     * @param lockKey
     * @return
     */
    protected boolean release(String lockKey) {
        return connection.execute((connection) -> {
            return connection.del(lockKey) != 0;
        });
    }
}
