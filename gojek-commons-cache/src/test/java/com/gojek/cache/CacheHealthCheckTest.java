/**
 * 
 */
package com.gojek.cache;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.cache.redis.JedisConnection;

import redis.embedded.RedisServer;

/**
 * @author ganeshs
 *
 */
public class CacheHealthCheckTest {
    
    private RedisServer redisServer;

    private CacheHealthCheck check;
    
    private JedisConnection connection;
    
    @BeforeClass
    public void startServer() throws IOException {
        redisServer = new RedisServer(6378);
        redisServer.start();
    }
    
    @AfterClass
    public void stopServer(){
        redisServer.stop();
    }
    
    @BeforeMethod
    public void setup() {
        connection = new JedisConnection();
        check = new CacheHealthCheck(connection);
    }
    
    @Test
    public void shouldReturnHealthy() throws Exception {
        connection.init(new CacheConfiguration("localhost", 6378, 500, 2, 1, 1, 500L));
        assertTrue(check.check().isHealthy());
    }
    
    @Test(expectedExceptions=CacheException.class)
    public void shouldReturnUnHealthy() throws Exception {
        connection.init(new CacheConfiguration("localhost", 6378, 500, 1, 1, 1, 500L));
        redisServer.stop();
        check.check().isHealthy();
    }
}
