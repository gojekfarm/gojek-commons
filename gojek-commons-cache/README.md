# Cache Bundle
The `CacheBundle` provides caching support to your application

## Maven dependency
    <dependency>
        <groupId>com.gojek</groupId>
        <artifactId>gojek-commons-cache</artifactId>
        <version>${latest_version}</version>
    </dependency>

## Add bundle to application
To configure the bundle, make your configuration implement CacheSupport,

```java
public class DSConfiguration extends BaseConfiguration implements JpaSupport {

    @JsonProperty("cache")
    private CacheConfiguration cacheConfiguration;

    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    public void setCacheConfiguration(CacheConfiguration configuration) {
        this.cacheConfiguration = configuration;
    }
}
```

In your yml configuration file, add the cache configuration.

```yaml
cache:
  host: ${REDIS_HOST:-localhost}
  port: ${REDIS_PORT:-6379}
  password: ${REDIS_PASSWORD:-}
  timeout: ${REDIS_TIMEOUT:-5}
  maxConnections: ${REDIS_MAX_CONNECTIONS:-25}
  minConnections: ${REDIS_MIN_CONNECTIONS:-5}
  maxIdleConnections: ${REDIS_MAX_IDLE_CONNECTIONS:-5}
```

Add the bundle to your application class,

```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    bootstrap.addBundle(CacheBundle.<DSConfiguration>builder().build());
    // You can also supply a uninitialized connection instance
    // bootstrap.addBundle(CacheBundle.<DSConfiguration>builder().using(() -> connection).build());
}
```

## Utilities
### Atomic Execution
The cache bundle comes with an `AtomicExecutor` that allows you to execute a functional block atomically across instances (jvm) in a cluster. The `AtomicExecutor` acquires a lock in redis before executing the block and releases after the execution if complete.

```java

    AtomicExecutor atomicExecutor = new AtomicExecutor<Void>(jedisConnection,
            RetryerBuilder.<Boolean> newBuilder().withStopStrategy(StopStrategies.stopAfterAttempt(LOCK_MAX_RETRIES))
                .withWaitStrategy(WaitStrategies.fixedWait(LOCK_RETRY_INTERVAL_IN_MILLIS, TimeUnit.MILLISECONDS))
                .retryIfResult((result) -> {
                    return !result;
                }).build(), LOCK_EXPIRY_TIME_IN_SECONDS);
                
    public void doSomethingAtomically() {
        atomicExecutor.execute(lockKey, (optional) -> {
            // do something here
            return null;
        });
    }
```

You can configure the retry strategy for the lock using the `RetryerBuilder`. If the lock is not available, a `LockException` will be thrown.