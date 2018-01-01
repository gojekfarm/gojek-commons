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
    
    @JsonProperty("queue")
    private QueueConfiguration queueConfiguration;

    @Override
    public AmqpConfiguration getAmqpConfiguration() {
        return amqpConfiguration;
    }
    
    public void setAmqpConfiguration(AmqpConfiguration amqpConfiguration) {
        this.amqpConfiguration = amqpConfiguration;
    }
    
    /**
     * @return the queueConfiguration
     */
    public QueueConfiguration getQueueConfiguration() {
        return queueConfiguration;
    }

    /**
     * @param queueConfiguration the queueConfiguration to set
     */
    public void setQueueConfiguration(QueueConfiguration queueConfiguration) {
        this.queueConfiguration = queueConfiguration;
    }
}
```

In your yml configuration file, add the amqp configuration.

```yaml
amqp:
  automaticRecovery: true
  uri: amqp://${RABBITMQ_USER:-guest}:${RABBITMQ_PASSWORD:-guest}@${RABBITMQ_HOST:-localhost}:${RABBITMQ_PORT:-5672}
  maxChannels: ${RABBITMQ_MAX_CHANNELS:-10}
  minChannels: ${RABBITMQ_MIN_CHANNELS:-5}
  maxIdleChannels: ${RABBITMQ_MAX_IDLE_CHANNELS:-5}
  # Known broker host list for auto connection recovery
  hosts:
    - localhost
```

Add the bundle to your application class,

```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    super.addBundles(bootstrap);
    bootstrap.addBundle(AmqpBundle.<DSConfiguration>builder().with(configuration -> {
            Map<ConsumerConfiguration, EventHandler<?>> consumers = Maps.newHashMap();
            consumers.put(configuration.getQueueConfiguration().getDriverConsumerConfiguration(), new DriverEventHandler()));
            return consumers;
        }).build());
}
```

## Publishing messages to an exchange
In your yml configuration file, add the queue configuration,

```yaml
queue:
  driverStatusDestination:
    exchange: ${DRIVER_TOPIC_EXCHANGE:-driver_direct_exchange}
    routingKey: driver.status
```

To raising an event to an exchange, you need to use AmqpProducer.

```java
@JsonTypeName(DriverActiveEvent.TYPE)
public class DriverActiveEvent extends Event {

    public static final String TYPE = "driver_active_event";
    
    /**
     * @param driver
     * @param jobId
     */
    public DriverActiveEvent(Driver driver) {
        super(driver, TYPE, DateTime.now());
    }
    
    /**
     * @param driverId
     * @param type
     * @param eventDate
     */
    @JsonCreator
    DriverActiveEvent(@JsonProperty("entity_id") String driverId, @JsonProperty("type") String type, @JsonProperty("event_date") DateTime eventDate) {
        super(driverId, type, eventDate);
    }
}

public class DriverService {

    private AmqpProducer producer;
    
    private QueueConfiguration configuration;

    @Inject
    public DriverService(DSConfiguration configuration, AmqpProducer producer) {
        this.producer = producer;
        this.configuration = configuration.getQueueConfiguration();
    }

    public void markActive(Driver driver) {
        // Do something
        raiseEvent(new DriverActiveEvent(driver));
    }

    /**
     * @param event
     */
    private void raiseEvent(Event event) {
        producer.send(event, configuration.getDriverStatusDestination());;
    }
}
```

## Consuming messages from a queue
In your yml configuration file, add the consumer configuration inside the queue configuration,

```yaml
queue:
  driverStatusDestination:
    exchange: ${DRIVER_TOPIC_EXCHANGE:-driver_direct_exchange}
    routingKey: driver.status
  driverConsumer:
    retryDestination: 
      exchange: ${DRIVER_RETRY_DIRECT_EXCHANGE:-driver_retry_direct_exchange}
    maxRetries: ${MAX_DRIVER_EVENTS_CONSUMER_RETRIES:-5}
    queueName: ${DRIVER_EVENTS_QUEUE:-driver_events}
    maxQueueConsumers: ${MAX_DRIVER_EVENTS_QUEUE_CONSUMERS:-5}
```

To process messages from a consumer, you need to register an event handler,

```java
public class DriverEventHandler implements EventHandler<Event> {

    public final Status handle(Event event, String queueName, String routingKey, Map<String, Object> headers) {
        try {
            // Process the event 
            return Status.success;
        } catch (Exception e) {
            return isRetyable(e) ? Status.soft_failure : Status.hard_failure; 
        }
    }
    
    /**
     * The class to deserialize the json event into
     */
    public Class<E> getEventClass() {
        return Event.class;
    }
}

### Handling failed event retries
Make your event handler class extend `FixedRetryEventHandler` class. This class takes care of retrying `Status.soft_failure` events by publishing the event to the `driverConsumer.retryDestination.exchange` for a max of `driverConsumer.maxRetries` times after which it will be dead lettered.

```java
public class DriverEventHandler extends FixedRetryHandler<Event> {

    @Inject
    public DriverEventHandler(DSConfiguration configuration, AmqpConnection connection) {
        super(connection, configuration.getQueueConfiguration().getDriverConsumerConfiguration().getRetryDestination(), Event.class, configuration.getQueueConfiguration().getDriverConsumerConfiguration().getMaxRetries());
    }
    
    @Override
    protected Status handleInternal(Event event) {
        try {
           switch(event.getType()) {
           case DriverActiveEvent.TYPE:
               break;
           case DriverInActiveEvent.TYPE:
               break;
           case DriverBusyEvent.TYPE:
               break;
           }
           return Status.success;
        } catch (Exception e) {
            return Status.soft_failure;
        }
    }
    
    protected void handleMaxRetryExceeded(E event) {
        // Handle what to do if max retry has exceeded. Event will be dead lettered in any case
    }
}
```

## Working with JPA Tranactions
### Publishing on JPA transactional commit
With JPA, you may have a usecase to ensure the events are published only when the JPA transaction is successfully committed. You can use the AmqpQueuedProducer instead of the AmqpProducer in such cases. The AmqpQueuedProducer queues up the messages in thread local and publishes them only when the JPA transaction is successfully committed. 

Add the following config parameter `hibernate.ejb.interceptor: com.gojek.guice.QueuedProducerTransactionInterceptor` to your jpa configuration in the application configuration yaml file.

```yaml
jpa:
  migrate: "true"
  driverClass: "org.postgresql.Driver"
  user: ${DB_USER:-ganesh.s}
  password: ${DB_PASSWORD:-}
  url: jdbc:postgresql://${DB_HOST:-localhost}:${DB_PORT:-5432}/${DB_NAME:-ds}?autoReconnect=true
  properties:
    hibernate.show_sql: ${DB_LOG_SQL:-true}
    hibernate.format_sql: "false"
    # hibernate.hbm2ddl.auto: create-drop
    hibernate.ejb.interceptor: com.gojek.guice.QueuedProducerTransactionInterceptor
```
While publishing use the AmpqQueuedProducer.

```java
public class DriverService {

    private AmqpQueuedProducer queuedProducer;

    /**
     * @param event
     */
    private void raiseEvent(Event event) {
        queuedProducer.send(event, configuration.getDriverStatusDestination());;
    }
}
```

### Open Session In View (OSIV) Consumers
AMQP consumers run on a different thread. So when using this with JPA, you need to make sure the entity managers are properly closed after every message is processed. Else you may get first level cache (like in Hibernate) hits for subsequent calls leading to data corruption in your db. To fix this issue you will have to write a wrapper around the handle() method of the event handler. The below example shows a wrapper written for Ampq-Guice-JPA combination.

```java
public class DriverEventHandler extends FixedRetryHandler<Event> {

    private ActiveJpaGuiceExecutionWrapper executionWrapper;
    
    private static final Logger logger = LoggerFactory.getLogger(DriverEventHandler.class);
    
    @Inject
    public DriverEventHandler(DSConfiguration configuration, AmqpConnection connection) {
        super(connection, configuration.getQueueConfiguration().getDriverConsumerConfiguration().getRetryDestination(), Event.class, configuration.getQueueConfiguration().getDriverConsumerConfiguration().getMaxRetries());
        this.executionWrapper = new ActiveJpaGuiceExecutionWrapper();
    }
    
    @Override
    protected Status handleInternal(Event event) {
        try {
            // Any jpa operations should be wrapped under the execution wrapper for proper handling on entity manager
            return executionWrapper.execute((optional) -> {
                switch(event.getType()) {
                case DriverActiveEvent.TYPE:
                    break;
                case DriverInActiveEvent.TYPE:
                    break;
                case DriverBusyEvent.TYPE:
                    break;
                }
                return Status.success;
            });
        } catch (Exception e) {
            logger.error("Failed while handling the event - " + event.getEventId(), e);
            return Status.soft_failure;
        }
    }
}
```
