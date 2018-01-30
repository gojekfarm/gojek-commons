/**
 * 
 */
package com.gojek.cache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gojek.cache.redis.JedisConnection;

import redis.embedded.RedisServer;

/**
 * @author ganeshs
 *
 */
public class CacheConnectionManagerTest {

    private RedisServer redisServer;
    
    private JedisConnection connection;
    
    private CacheConfiguration configuration;
    
    private CacheConnectionManager cacheConnectionManager;

    @BeforeClass
    public void setup() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
        configuration = new CacheConfiguration("localhost", 6379, 500, 24, 4, 2, 500L);
        connection = mock(JedisConnection.class);
        cacheConnectionManager = spy(new CacheConnectionManager(configuration, connection));
    }

    @AfterClass
    public void stopServer(){
        redisServer.stop();
    }

    @Test
    public void shouldStartTheConnection(){
        cacheConnectionManager.start();
        verify(connection).init(configuration);
    }

    @Test
    public void shouldStopTheConnection(){
        cacheConnectionManager.stop();
        verify(connection).close();
    }
}
