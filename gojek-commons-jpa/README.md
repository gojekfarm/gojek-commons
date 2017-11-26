# JpaBundle
The Jpa bundle provides jpa (hibernate) support to your application.

## Maven dependency
```xml
   <dependencies>
     <dependency>
       <groupId>com.gojek</groupId>
       <artifactId>gojek-commons-jpa</artifactId>
       <version>${latest_version}</version>
     </dependency>
   </dependencies>
```

## Add bundle to the application
To configure the bundle, make your configuration implement `JpaSupport`,

```java
public class DSConfiguration extends BaseConfiguration implements JpaSupport {
    
    @JsonProperty("jpa")
    private JpaConfiguration jpaConfiguration;
    
    public JpaConfiguration getJpaConfiguration() {
        return jpaConfiguration;
    }
    
    public void setJpaConfiguration(JpaConfiguration configuration) {
        this.jpaConfiguration = configuration;
    }
}
```
In your yml configuration file, add the jpa configuration.

```yaml
jpa:
  migrate: "true"
  driverClass: "org.postgresql.Driver"
  user: ${DB_USER:-ganeshs}
  password: ${DB_PASSWORD:-}
  url: jdbc:postgresql://${DB_HOST:-localhost}:${DB_PORT:-5432}/${DB_NAME:-ds}?autoReconnect=true
  properties:
    hibernate.show_sql: ${DB_LOG_SQL:-true}
    hibernate.format_sql: "false"
    # hibernate.hbm2ddl.auto: create-drop
    hibernate.dialect: com.gojek.jpa.util.ExtendedPostgreSQL94Dialect
    hibernate.c3p0.acquireIncrement: 2
    hibernate.c3p0.initialPoolSize: 3
    hibernate.c3p0.minPoolSize: 5
    hibernate.c3p0.maxPoolSize: ${DB_POOL_MAX_SIZE:-10}
    hibernate.c3p0.maxIdleTime: 300
    hibernate.c3p0.maxStatements: 500
    hibernate.c3p0.idleConnectionTestPeriod: 30
    hibernate.c3p0.preferredTestQuery: "SELECT 1"
```

Add the bundle to your application class,

```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    String persistentUnit = "ds";
    bootstrap.addBundle(JpaBundle.<DSConfiguration>builder().with(persistentUnit).using(DSConfiguration.class).build());
}
```

## Base Entity
The Jpa bundle provides @MappedSuperClass `BaseRecordableModel` abstracting the fields [String id, DateTime createdAt, DateTime updatedAt, boolean deleted]. It also inherits the ActiveJPA `Model` class to allow you to query in ActiveRecord Style. Checkout [ActiveJPA Documentation](https://github.com/ActiveJpa/activejpa) for examples.


