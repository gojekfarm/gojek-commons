package com.gojek.cache.redis;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.cache.CacheConfiguration;
import com.gojek.cache.redis.JedisConnection.ConnectionPoolListener;

import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class JedisConnectionTest {
    
    private CacheConfiguration configuration;

    private JedisConnection connection;
    
    private RedisServer redisServer;

    @BeforeClass
    public void beforeClass() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterClass
    public void afterClass(){
        redisServer.stop();
    }
    
    @BeforeMethod
    public void setup() {
        this.configuration = new CacheConfiguration("localhost", 6379, 5, 2, 2, 2);
        connection = new JedisConnection();
    }

    @Test
    public void shouldSetAndGetTheValueToKey(){
        connection.init(configuration);
        connection.execute(connection -> {
            connection.set("name", "value");
            return null;
        });
        String value = connection.execute(connection -> {
            return connection.get("name");
        });
        assertEquals(value, "value");
    }
    
    @Test
    public void shouldInvokeListenerOnInit() {
        AtomicBoolean result = new AtomicBoolean();
        connection.register(new ConnectionPoolListener() {
            @Override
            public void onPoolInitialized(GenericObjectPool<Jedis> pool) {
                result.set(pool != null);
            }
            
            @Override
            public void onPoolDestroyed(GenericObjectPool<Jedis> pool) {
            }
        });
        connection.init(configuration);
        assertTrue(result.get());
    }
    
    @Test
    public void shouldInvokeListenerOnDestroy() {
        AtomicBoolean result = new AtomicBoolean();
        connection.register(new ConnectionPoolListener() {
            @Override
            public void onPoolInitialized(GenericObjectPool<Jedis> pool) {
            }
            
            @Override
            public void onPoolDestroyed(GenericObjectPool<Jedis> pool) {
                result.set(pool != null);
            }
        });
        connection.init(configuration);
        connection.close();
        assertTrue(result.get());
    }

}
