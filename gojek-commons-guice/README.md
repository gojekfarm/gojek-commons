# Guice Bundle
The `GuiceBundle` provides dependency injection support using Guice to your application.

## Maven dependency
```xml
     <dependency>
       <groupId>com.gojek</groupId>
       <artifactId>gojek-commons-guice</artifactId>
       <version>${latest_version}</version>
     </dependency>
```

## Add bundle to the application
The guice bundle has to be added before any of the other bundles (cache/amqp/job etc..)

```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    super.addBundles(bootstrap);
    bootstrap.addBundle(GuiceBundle.<DSConfiguration>builder().using(DSConfiguration.class).addModule(myCustomGuiceModule).build());
}
```

If you are using jpa module, you will have to use `GuiceJpaBundle` instead for guice-persist service support,
```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    super.addBundles(bootstrap);
    bootstrap.addBundle(GuiceJpaBundle.<DSConfiguration>builder(configFileLocation).with("ds").using(DSConfiguration.class).addModule(myCustomGuiceModule).build());
}
```

## Application Configuration Injection
The guice bundle binds the Application Configuration to the `configurationClass` specified in the bundle. So you can inject the app configuration in a guice managed instance,

```java
public class DriverService {

    private DSConfiguration configuration;
    
    @Inject
    public DriverService(DSConfiguration configuration) {
        this.configuration = configuration;
    }
}
```