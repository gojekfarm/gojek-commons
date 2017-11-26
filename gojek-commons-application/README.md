# Basic Application

## Application class
Ensure your application class extends `com.gojek.application.BaseApplication`. It brings out-of-the-box support for,
* Exception mapper for `WebApplicationException` that transforms the exception into a json response
* Localization of error messsages
* Support for excluding or including specific fields from api response
* Useful filters like `RequestTrackingFilter`, `CorsFilter` etc.
* Swagger integration

```java
public class DSApplication extends BaseApplication<DSConfiguration> {
    
    @Override
    protected void registerResources(DSConfiguration configuration, Environment environment) {
        // Register your jaxrs resources
        environment.jersey().register(DriverResource.class);
    }
    
    // Optionally override the method
    @Override
    protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
        super.addBundles(bootstrap);
        // Register your drpwizard bundles here
    }
    
    // Optionally override the method
    @Override
    protected void registerFilters(T configuration, Environment environment) {
        super.registerFilters(configuration, environment);
        // Your custom filters
    }
    
    // Optionally override the method
    @Override
    protected void registerExceptionMappers(T configuration, Environment environment) {
        super.registerExceptionMappers(configuration, environment);
        // Your custom exception mappers
    }
    
    // Optionally override the method
    @Override
    protected void registerManagedObjects(T configuration, Environment environment) {
        super.registerManagedObjects(configuration, environment);
        // Your custom managed objects
    }
    
    // Optionally override the method
    @Override
    protected void registerHealthChecks(T configuration, Environment environment) {
        super.registerHealthChecks(configuration, environment);
        // Your custom health checks
    }
    
    public static void main(String[] args) throws Exception {
        new DSApplication().run("server", "src/main/resources/ds.yml");
    }
}
```
## Application Configuration
You application configuration must extend the `BaseConfiguration`.

```java
public class DSConfiguration extends BaseConfiguration {
    // Your application specific config parameters
}
```

Below is a basic yml configuration to configure logging, swagger & metrics.

```yaml
logging:
  level: ${LOG_LEVEL:-INFO}
  loggers:
    log4j.logger.org.hibernate: "INFO"
    org.hibernate.hql: "ERROR"
  appenders:
    - type: console
      threshold: ALL
      timeZone: UTC
      logFormat: "%d [%thread] %-5level %c{15} - %msg%n%rEx"

server:
  type: simple
  applicationContextPath: /
  adminContextPath: /admin
  connector:
    port: 8080
    type: http
  requestLog:
    appenders:
      - type: console
        timeZone: UTC

swagger:
  resourcePackage: com.gojek.tms.resource
  title: TMS Apis
  version: v1
  description: Apis related to shipments and routes

metrics:
  reporters:
    - type: console
      useRegexFilters: true
      excludes:
        - ch.*
        - jvm.*
      frequency: 600s

# Your application specific config parameters
```