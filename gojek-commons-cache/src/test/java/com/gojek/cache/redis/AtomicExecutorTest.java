/**
 *
 */
package com.gojek.cache.redis;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.gojek.cache.CacheConfiguration;

import redis.embedded.RedisServer;

/**
 * @author ganeshs
 *
 */
public class AtomicExecutorTest {
    
    private JedisConnection connection;
    
    private Retryer<Boolean> retryer;
    
    private RedisServer redisServer;

    @BeforeClass
    public void setup() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
        CacheConfiguration configuration = new CacheConfiguration("localhost", 6379, 5, 2, 2, 2, 5L);
        connection = new JedisConnection();
        connection.init(configuration);
        retryer = RetryerBuilder.<Boolean>newBuilder().withStopStrategy(StopStrategies.stopAfterAttempt(2)).build();
    }

    @AfterClass
    public void finish(){
        redisServer.stop();
    }

    @Test
    public void shouldAcquireLockBeforeExecute() {
        AtomicExecutor<Boolean> executor = new AtomicExecutor<>(connection, retryer, 1);
        AtomicBoolean status = new AtomicBoolean();
        executor.execute("some-key-1", (optional) -> {
            status.set(connection.execute((jedis) -> {
                return jedis.exists("some-key-1");
            }));
            return true;
        });
        assertTrue(status.get());
    }
    
    @Test
    public void shouldReleaseLockAfterExecute() {
        AtomicExecutor<Boolean> executor = new AtomicExecutor<>(connection, retryer, 1);
        executor.execute("some-key-2", (optional) -> {
            return true;
        });
        assertFalse(connection.execute((jedis) -> {
            return jedis.exists("some-key-2");
        }));
    }
    
    @Test(expectedExceptions=LockException.class)
    public void shouldThrowLockExceptionIfLockNotAvialableAfterMaxRetries() {
        AtomicExecutor<Boolean> executor = new AtomicExecutor<>(connection, retryer, 1);
        executor.lock("some-key-3");
        executor.execute("some-key-3", (optional) -> {
            return true;
        });
    }
    
    @Test
    public void shouldAcquireLockAfterItHasExpired() throws Exception {
        AtomicExecutor<Boolean> executor = new AtomicExecutor<>(connection, retryer, 1);
        executor.lock("some-key-4");
        Thread.sleep(1100);
        assertTrue(executor.execute("some-key-4", (optional) -> {
            return true;
        }));
    }
    
    @Test
    public void shouldAcquireReentrantLock() {
        AtomicExecutor<Boolean> executor1 = new AtomicExecutor<>(connection, retryer, 1);
        AtomicExecutor<Boolean> executor2 = new AtomicExecutor<>(connection, retryer, 1);
        AtomicBoolean status1 = new AtomicBoolean();
        AtomicBoolean status2 = new AtomicBoolean();
        executor1.execute("some-key-5", (optional) -> {
            status1.set(connection.execute((jedis) -> {
                return jedis.exists("some-key-5");
            }));
            
            executor2.execute("some-key-5", (optional2) -> {
                status2.set(connection.execute((jedis) -> {
                    return jedis.exists("some-key-5");
                }));
                return true;
            });
            
            return true;
        });
        
        assertTrue(status1.get());
        assertTrue(status2.get());
    }
    
    @Test
    public void shouldNotReleaseReentrantLock() {
        AtomicExecutor<Boolean> executor1 = new AtomicExecutor<>(connection, retryer, 1);
        AtomicExecutor<Boolean> executor2 = new AtomicExecutor<>(connection, retryer, 1);
        AtomicBoolean status = new AtomicBoolean();
        executor1.execute("some-key-5", (optional) -> {
            executor2.execute("some-key-5", (optional2) -> {
                return true;
            });
            
            status.set(connection.execute((jedis) -> {
                return jedis.exists("some-key-5");
            }));
            
            return true;
        });
        
        assertTrue(status.get());
    }
    
    @Test
    public void shouldReleaseReentrantLockIfOwner() {
        AtomicExecutor<Boolean> executor1 = new AtomicExecutor<>(connection, retryer, 1);
        AtomicExecutor<Boolean> executor2 = new AtomicExecutor<>(connection, retryer, 1);
        AtomicBoolean status = new AtomicBoolean();
        executor1.execute("some-key-5", (optional) -> {
            executor2.execute("some-key-5", (optional2) -> {
                return true;
            });
            return true;
        });
        status.set(connection.execute((jedis) -> {
            return jedis.exists("some-key-5");
        }));
        
        assertFalse(status.get());
    }
}
