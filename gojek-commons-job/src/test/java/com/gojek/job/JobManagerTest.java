/**
 * 
 */
package com.gojek.job;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.joda.time.DateTime;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.MetricRegistry;
import com.gojek.guice.util.GuiceUtil;
import com.gojek.job.JobConfiguration.Job;
import com.gojek.job.JobConfiguration.Schedule;
import com.gojek.job.guice.GuiceJobFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

/**
 * @author ganeshs
 *
 */
public class JobManagerTest {

    private QuartzConfiguration quartzConfiguration;
    
    private JobConfiguration jobConfiguration;

    private JobManager jobManager;

    private Injector injector;
    
    @BeforeClass
    public void beforeClass() throws IOException {
        injector = mock(Injector.class);
        when(injector.getInstance(TestJob.class)).thenReturn(new TestJob());
        GuiceUtil.load(injector);
    }
    
    @AfterClass
    public void afterClass() {
        GuiceUtil.reset();
    }

    @BeforeMethod
    public void setup() throws Exception {
        quartzConfiguration = defaultQuartzConfiguration();
        jobConfiguration = new JobConfiguration();
        jobConfiguration.setJobProcessingEnabled(true);
        JobMetricsCollector collector = new JobMetricsCollector();
        collector.init(jobManager, new MetricRegistry());
        jobManager = spy(new JobManager());
        jobManager.init(quartzConfiguration, jobConfiguration, new GuiceJobFactory(), collector);
        jobManager.start();
    }
    
    @AfterMethod
    public void destroy() throws Exception {
        jobManager.stop();
    }

    @Test
    public void shouldInitializeSchedulerOnStart() throws Exception {
        assertNotNull(jobManager.getScheduler());
    }

    @Test
    public void shouldAddTriggerHistoryListenerOnStart() throws Exception {
        assertNotNull(jobManager.getScheduler().getListenerManager().getTriggerListener(JobMetricsCollector.NAME));
    }

    @Test
    public void shouldShutdownSchedulerOnStop() throws Exception {
        jobManager.stop();
        assertTrue(jobManager.getScheduler().isShutdown());
    }
    
    @Test
    public void shouldStartSchedulerWhenServerIsStarted() throws Exception {
        jobManager.serverStarted(mock(Server.class));
        assertTrue(jobManager.getScheduler().isStarted());
    }
    
    @Test
    public void shouldScheduleJobsOnServerStarted() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule("1 * * * * ?");
        Job job1 = new Job("some-job", TestJob.class, "some-group", schedule, data, true);
        Job job2 = new Job("some-other-job", TestJob.class, "some-other-group", schedule, data, true);
        jobConfiguration.setJobs(Lists.newArrayList(job1, job2));
        
        jobManager.serverStarted(mock(Server.class));
        JobDetail jobDetail1 = jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job", "some-group"));
        CronTrigger trigger1 = (CronTrigger) jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job", "some-group"));
        assertEquals(jobDetail1.getJobDataMap().getWrappedMap(), data);
        assertEquals(trigger1.getCronExpression(), schedule.getCron());
        JobDetail jobDetail2 = jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-other-job", "some-other-group"));
        CronTrigger trigger2 = (CronTrigger) jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-other-job", "some-other-group"));
        assertEquals(jobDetail2.getJobDataMap().getWrappedMap(), data);
        assertEquals(trigger2.getCronExpression(), schedule.getCron());
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldThrowExceptionWhenJobClassIsNull() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);
        Job job = new Job("some-job", null, "some-group", schedule, data, true);
        
        jobManager.addJob(job);
    }
    
    @Test
    public void shouldScheduleNewJobWithSimpleTrigger() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);
        Job job = new Job("some-job", TestJob.class, "some-group", schedule, data, true);
        
        jobManager.addJob(job);
        JobDetail jobDetail = jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job", "some-group"));
        SimpleTrigger trigger = (SimpleTrigger) jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job", "some-group"));
        assertEquals(jobDetail.getJobDataMap().getWrappedMap(), data);
        assertEquals(trigger.getRepeatInterval(), schedule.getInterval() * 1000);
        assertEquals(trigger.getRepeatCount(), SimpleTrigger.REPEAT_INDEFINITELY);
    }

    @Test
    public void shouldDeleteExistingJobWhenJobIsDisabled() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);

        Job job = new Job("some-job", TestJob.class, "some-group", schedule, data, true);
        jobManager.addJob(job);

        job = new Job("some-job", TestJob.class, "some-group", schedule, data, false);
        jobManager.addJob(job);

        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job", "some-group")));
    }

    @Test
    public void shouldDeleteExistingJobCollectionWhenJobCollectionIsDisabled() throws Exception {
        List<String> jobs = Lists.newArrayList("some-job-1", "some-job-2");
        doReturn(new TestJobCollection(jobs)).when(jobManager).constructJobCollection(TestJobCollection.class);

        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);

        Job job = new Job("some-job-collection", null, "some-group", schedule, data, true);
        job.setJobCollectionClass(TestJobCollection.class);
        jobManager.addJob(job);

        job = new Job("some-job-collection", null, "some-group", schedule, data, false);
        job.setJobCollectionClass(TestJobCollection.class);
        jobManager.addJob(job);

        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-1", "some-group")));
        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-2", "some-group")));
        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-collection", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-1", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-2", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-collection", "some-group")));
    }

    @Test
    public void shouldInvokeDeleteJobWhenJobIsDisabledWhenNoJobsExist() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);

        Job job = new Job("some-job", TestJob.class, "some-group", schedule, data, false);
        jobManager.addJob(job);

        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job", "some-group")));
    }

    @Test
    public void shouldInvokeDeleteJobCollectionWhenJobCollectionIsDisabledWhenNoJobsExist() throws Exception {
        List<String> jobs = Lists.newArrayList("some-job-1", "some-job-2");
        doReturn(new TestJobCollection(jobs)).when(jobManager).constructJobCollection(TestJobCollection.class);

        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);

        Job job = new Job("some-job-collection", null, "some-group", schedule, data, false);
        job.setJobCollectionClass(TestJobCollection.class);
        jobManager.addJob(job);

        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-1", "some-group")));
        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-2", "some-group")));
        assertNull(jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-collection", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-1", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-2", "some-group")));
        assertNull(jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-collection", "some-group")));
    }

    @Test(expectedExceptions=JobException.class, expectedExceptionsMessageRegExp = "Failed while deleting the job")
    public void shouldThrowJobExceptionWhenSchedulerIsNotActive() throws Exception {
        jobManager.stop();

        Job job = new Job("some-job", TestJob.class, "some-group", null, Maps.newHashMap(), false);

        jobManager.addJob(job);
    }

    @Test
    public void shouldScheduleNewJobWithCronTrigger() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule("1 * * * * ?");
        Job job = new Job("some-job", TestJob.class, "some-group", schedule, data, true);
        
        jobManager.addJob(job);
        JobDetail jobDetail = jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job", "some-group"));
        CronTrigger trigger = (CronTrigger) jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job", "some-group"));
        assertEquals(jobDetail.getJobDataMap().getWrappedMap(), data);
        assertEquals(trigger.getCronExpression(), schedule.getCron());
    }
    
    @Test
    public void shouldUpdateScheduleForExistingJob() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);
        Job job = new Job("some-job", TestJob.class, "some-group", schedule, data, true);
        
        jobManager.addJob(job);
        
        schedule = new Schedule("1 * * * * ?");
        job = new Job("some-job", TestJob.class, "some-group", schedule, data, true);
        
        jobManager.addJob(job);
        
        JobDetail jobDetail = jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job", "some-group"));
        CronTrigger trigger = (CronTrigger) jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job", "some-group"));
        assertEquals(jobDetail.getJobDataMap().getWrappedMap(), data);
        assertEquals(trigger.getCronExpression(), schedule.getCron());
    }
    
    @Test
    public void shouldNotScheduleJobsOnRunIfJobProcessingIsNotEnabled() throws Exception {
        Schedule schedule = new Schedule(1800);
        Job job = new Job("some-job", TestJob.class, "some-group", schedule, Maps.newHashMap(), true);
        jobConfiguration.setJobProcessingEnabled(false);
        jobConfiguration.setJobs(Lists.newArrayList(job));
        
        jobManager.serverStarted(mock(Server.class));
        
        assertTrue(jobManager.getScheduler().getJobGroupNames().isEmpty());
    }

    @Test
    public void shouldInterruptJobsOnStop() throws Exception {
        AtomicBoolean status = new AtomicBoolean();
        when(injector.getInstance(TestJobWithDelay.class)).thenReturn(new TestJobWithDelay(() -> {
            status.set(true);
            return null;
        }));
        Schedule schedule = new Schedule(1800);
        Job job = new Job("some-job", TestJobWithDelay.class, "some-group", schedule, Maps.newHashMap(), true);
        jobConfiguration.setJobs(Lists.newArrayList(job));
        
        jobManager.serverStarted(mock(Server.class));
        Thread.sleep(400);
        jobManager.stop();
        Thread.sleep(100);
        assertTrue(status.get());
    }
    
    @Test
    public void shouldScheduleCollectionOfJobs() throws Exception {
        List<String> jobs = Lists.newArrayList("some-job-1", "some-job-2");
        doReturn(new TestJobCollection(jobs)).when(jobManager).constructJobCollection(TestJobCollection.class);
        
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Schedule schedule = new Schedule(1800);
        Job job = new Job("some-job-collection", null, "some-group", schedule, data, true);
        job.setJobCollectionClass(TestJobCollection.class);
        
        List<JobDetail> jobDetails = jobManager.addJob(job);
        assertEquals(jobDetails.size(), jobs.size());
        for (String jobId : jobs) {
            JobDetail jobDetail = jobManager.getScheduler().getJobDetail(JobKey.jobKey(jobId, "some-group"));
            assertNotNull(jobDetail);
            assertEquals(jobDetail.getJobDataMap().getWrappedMap(), data);
            SimpleTrigger trigger = (SimpleTrigger) jobManager.getScheduler().getTrigger(TriggerKey.triggerKey(jobId, "some-group"));
            assertEquals(trigger.getRepeatInterval(), schedule.getInterval() * 1000);
            assertEquals(trigger.getRepeatCount(), SimpleTrigger.REPEAT_INDEFINITELY);
        }
    }
    
    @Test
    public void shouldAddJobWithoutSchedule() throws Exception {
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        Job job = new Job("some-job-without-schedule", TestJob.class, "some-group", null, data, true);
        
        jobManager.addJob(job);
        JobDetail jobDetail = jobManager.getScheduler().getJobDetail(JobKey.jobKey("some-job-without-schedule", "some-group"));
        Trigger trigger = jobManager.getScheduler().getTrigger(TriggerKey.triggerKey("some-job-without-schedule", "some-group"));
        assertEquals(jobDetail.getJobDataMap().getWrappedMap(), data);
        assertNull(trigger);
    }
    
    @Test(expectedExceptions=IllegalStateException.class)
    public void shouldThrowExceptionOnFireJobIfSchedulerIsNotStarted() throws Exception {
        Job job = new Job("fire-now-job", TestJobWithDelay.class, "some-group", null, Maps.newHashMap(), true);
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        
        jobManager.fireNow(job, data);
    }
    
    @Test
    public void shouldFireJobImmediately() throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        when(injector.getInstance(TestJobWithDelay.class)).thenReturn(new TestJobWithDelay((context) -> {
            resultMap.putAll(context.getMergedJobDataMap());
        }));
        
        Job job = new Job("fire-now-job", TestJobWithDelay.class, "some-group", null, Maps.newHashMap(), true);
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        
        jobManager.serverStarted(mock(Server.class));
        jobManager.fireNow(job, data);
        Thread.sleep(1100);
        assertEquals(resultMap, data);
    }
    
    @Test
    public void shouldFireSameJobMultipleTimes() throws Exception {
        AtomicInteger integer = new AtomicInteger();
        when(injector.getInstance(TestJobWithDelay.class)).thenReturn(new TestJobWithDelay((context) -> {
            integer.incrementAndGet();
        }));
        
        Job job1 = new Job("fire-now-job", TestJobWithDelay.class, "some-group", null, Maps.newHashMap(), true);
        Job job2 = new Job("fire-now-job", TestJobWithDelay.class, "some-group", null, Maps.newHashMap(), true);
        Map<String, Object> data = Maps.newHashMap();
        data.put("some-key", "some-value");
        
        jobManager.serverStarted(mock(Server.class));
        jobManager.fireNow(job1, data);
        jobManager.fireNow(job2, data);
        Thread.sleep(1200);
        assertEquals(integer.get(), 2);
    }
    
    @Test
    public void shouldReturnLongRunningJobsWithDefaultThreshold() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDetail detail = mock(JobDetail.class);
        when(detail.getJobDataMap()).thenReturn(new JobDataMap());
        when(context.getJobDetail()).thenReturn(detail);
        when(context.getFireTime()).thenReturn(DateTime.now().minusSeconds(JobManager.DEFAULT_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS + 10).toDate());
        when(scheduler.getCurrentlyExecutingJobs()).thenReturn(Lists.newArrayList(context));
        doReturn(scheduler).when(jobManager).getScheduler();
        jobManager.getLongRunningJobs();
    }
    
    @Test
    public void shouldReturnLongRunningJobsWithThresholdFromJobData() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDetail detail = mock(JobDetail.class);
        when(detail.getJobDataMap()).thenReturn(new JobDataMap(ImmutableMap.of(JobManager.KEY_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS, 10*60)));
        when(context.getJobDetail()).thenReturn(detail);
        when(context.getFireTime()).thenReturn(DateTime.now().minusMinutes(11*60).toDate());
        when(scheduler.getCurrentlyExecutingJobs()).thenReturn(Lists.newArrayList(context));
        doReturn(scheduler).when(jobManager).getScheduler();
        jobManager.getLongRunningJobs();
    }
    
    /**
     * @author ganeshs
     *
     */
    public static class TestJob implements org.quartz.Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }
    
    /**
     * @author ganeshs
     *
     */
    public static class TestJobCollection implements JobCollection {
        
        private List<String> jobs;
        
        public TestJobCollection(List<String> jobs) {
            this.jobs = jobs;
        }
        
        @Override
        public List<Job> getJobs(String groupName, Schedule schedule) {
            return jobs.stream().map(job -> new Job(job, TestJob.class, groupName, schedule, Maps.newHashMap(), true)).collect(Collectors.toList());
        }
    }
    
    /**
     * @author ganeshs
     *
     */
    public static class TestJobWithDelay implements org.quartz.InterruptableJob {
        
        private Callable<Void> onInterrupted;
        
        private Consumer<JobExecutionContext> onExecute;
        
        /**
         * @param callable
         */
        public TestJobWithDelay(Callable<Void> onInterrupted) {
            this.onInterrupted = onInterrupted;
        }
        
        public TestJobWithDelay(Consumer<JobExecutionContext> onExecute) {
            this.onExecute = onExecute;
        }

        @Override
        public void interrupt() throws UnableToInterruptJobException {
            try {
                if (this.onInterrupted != null) {
                    this.onInterrupted.call();
                }
            } catch (Exception e) {
            }
        }
        
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                Thread.sleep(1000);
                if (onExecute != null) {
                    onExecute.accept(context);
                }
            } catch (Exception ie) {
            }
        }
    }
    
    /**
     * @return
     */
    private static QuartzConfiguration defaultQuartzConfiguration() {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("org.quartz.scheduler.instanceName", "tms-cluster");
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        properties.put("org.quartz.threadPool.threadCount", "20");
        properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        return new QuartzConfiguration(properties);
    }
}
