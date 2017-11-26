# Amqp Bundle
The Amqp bundle provides messaging support to your application.

## Maven Dependency
```xml
     <dependency>
       <groupId>com.gojek</groupId>
       <artifactId>gojek-commons-amqp</artifactId>
       <version>${latest_version}</version>
     </dependency>
```

## Add bundle to the application
To configure the bundle, make your configuration implement JpaSupport,

```java
public class DSConfiguration extends BaseConfiguration implements AmqpSupport {

    @JsonProperty("amqp")
    private AmqpConfiguration amqpConfiguration;

    @Override
    public AmqpConfiguration getAmqpConfiguration() {
        return amqpConfiguration;
    }
    
    public void setAmqpConfiguration(AmqpConfiguration amqpConfiguration) {
        this.amqpConfiguration = amqpConfiguration;
    }
}
```

In your yml configuration file, add the jpa configuration.

```yaml
amqp:
  automaticRecovery: true
  uri: amqp://${RABBITMQ_USER:-guest}:${RABBITMQ_PASSWORD:-guest}@${RABBITMQ_HOST:-localhost}:${RABBITMQ_PORT:-5672}
  maxChannels: ${RABBITMQ_MAX_CHANNELS:-10}
  minChannels: ${RABBITMQ_MIN_CHANNELS:-5}
  maxIdleChannels: ${RABBITMQ_MAX_IDLE_CHANNELS:-5}
```

Add the bundle to your application class,

```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    super.addBundles(bootstrap);
    bootstrap.addBundle(AmqpBundle.<DSConfiguration>builder().with(configuration -> {
            Map<ConsumerConfiguration, EventHandler> consumers = Maps.newHashMap();
            consumers.put(configuration.getQueueConfiguration().getDriverConsumerConfiguration(), new DriverEventHandler()));
            return consumers;
        }).build());
}
```