# Job Bundle
The Jpa bundle provides support for async job processing using Quartz.

## Maven dependency
```xml
    <dependency>
        <groupId>com.gojek</groupId>
        <artifactId>gojek-commons-job</artifactId>
        <version>${latest_version}</version>
    </dependency>
```

## Add bundle to the application
To configure the bundle, make your configuration implement JobSupport,

```java
public class DSConfiguration extends BaseConfiguration implements JpaSupport {

    @JsonProperty("quartz")
    private QuartzConfiguration quartzConfiguration = new QuartzConfiguration();

    @JsonProperty("job")
    private JobConfiguration jobConfiguration = new JobConfiguration();

    public QuartzConfiguration getQuartzConfiguration() {
        return quartzConfiguration;
    }

    public void setQuartzConfiguration(QuartzConfiguration quartzConfiguration) {
        this.quartzConfiguration = quartzConfiguration;
    }

    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }

    public void setJobConfiguration(JobConfiguration jobConfiguration) {
        this.jobConfiguration = jobConfiguration;
    }
}
```

In your yml configuration file, add the jpa configuration.
```yaml
quartz:
  properties:
    org.quartz.scheduler.instanceName: "ds-cluster"
    org.quartz.scheduler.instanceId: "AUTO"
    org.quartz.threadPool.threadCount: "${QRTZ_MAX_THREADS_PER_POOL:-3}"
    org.quartz.jobStore.class: "org.quartz.impl.jdbcjobstore.JobStoreTX"
    org.quartz.jobStore.driverDelegateClass: "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate"
    org.quartz.jobStore.useProperties: "false"
    org.quartz.jobStore.dataSource: "ds"
    org.quartz.jobStore.isClustered: "true"
    org.quartz.jobStore.clusterCheckinInterval: "20000"
    org.quartz.dataSource.ds.driver: "org.postgresql.Driver"
    org.quartz.dataSource.ds.URL: jdbc:postgresql://${DB_HOST:-localhost}:${DB_PORT:-5432}/${DB_NAME:-ds}?autoReconnect=true
    org.quartz.dataSource.ds.user: ${DB_USER:-guest}
    org.quartz.dataSource.ds.password: ${DB_PASSWORD:-}
    org.quartz.dataSource.ds.maxConnections: "5"
    org.quartz.jobStore.acquireTriggersWithinLock: "true"
    
job:
  jobProcessingEnabled: ${JOB_PROCESSING_ENABLED:-true}
  jobs: 
    - jobClass: com.gojek.ds.jobs.ReportingJob
      groupName: ${REPORTING_JOB_GROUP_NAME:-reporting}
      enabled: ${REPORTING_JOB_ENABLED:-true}
      schedule:
        interval: ${REPORTING_JOB_FREQUENCY_IN_SECS:-3600}
        cron: ${REPORTING_JOB_CRON_SCHEDULE:-"0 0 1 * * ?"}
      data:
        job_running_time_threshold_breach_in_secs: ${REPORTING_JOB_THRESOLD_BREACH_TIME_IN_SECS:-1200}
```

Add the bundle to your application class,

```java
@Override
protected void addBundles(Bootstrap<DSConfiguration> bootstrap) {
    super.addBundles(bootstrap);
    bootstrap.addBundle(JobBundle.<TMSConfiguration>builder().build());
    // If you use a dependency injection framework like guice, you can also supply the job factory and job manager via the builder
    // bootstrap.addBundle(JobBundle.<TMSConfiguration>builder().using(() -> GuiceUtil.getInstance(JobManager.class)).with(new GuiceJobFactory()).build());
}

```