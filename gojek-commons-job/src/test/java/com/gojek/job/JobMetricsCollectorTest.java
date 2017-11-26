/**
 * 
 */
package com.gojek.job;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.codahale.metrics.MetricRegistry;

/**
 * @author ganeshs
 *
 */
public class JobMetricsCollectorTest {

	private JobMetricsCollector metricListener;
	
	private MetricRegistry metricRegistry;
	
	private String triggerName = "test-trigger";
    
    private Trigger trigger;
	
	private JobManager jobManager;
	
	private JobExecutionContext jobExecutionContext;
	
	@BeforeMethod
	public void setup() throws Exception {
		metricRegistry = new MetricRegistry();
		jobManager = mock(JobManager.class);
		when(jobManager.getLongRunningJobs()).thenReturn(Lists.newArrayList(mock(JobExecutionContext.class), mock(JobExecutionContext.class)));
		metricListener = new JobMetricsCollector();
		metricListener.init(jobManager, metricRegistry);
		trigger = mock(Trigger.class);
		when(trigger.getKey()).thenReturn(new TriggerKey(triggerName));
		jobExecutionContext = mock(JobExecutionContext.class);
		when(jobExecutionContext.getFireInstanceId()).thenReturn(UUID.randomUUID().toString());
		when(jobExecutionContext.getFireTime()).thenReturn(new Date());
	}
	
	@Test
	public void shouldRecordLongRunningJobCount() throws Exception {
	    assertEquals(metricRegistry.getGauges().get(JobMetricsCollector.METRIC_LONG_RUNNING_JOB).getValue(), 2);
	}
	
	@Test
	public void shouldIncCounterWhenRoutingJob() {
		when(jobExecutionContext.getJobInstance()).thenReturn(mock(DummyJob.class));
		metricListener.triggerFired(trigger, jobExecutionContext);
		assertEquals(metricRegistry.counter(String.format(JobMetricsCollector.METRIC_TRIGGER_FIRED, triggerName)).getCount(), 1);
	}
	
	@Test
	public void shouldIncCounterWhenNotRoutingJob() {
		metricListener.triggerFired(trigger, jobExecutionContext);
		assertEquals(metricRegistry.counter(String.format(JobMetricsCollector.METRIC_TRIGGER_FIRED, triggerName, Boolean.FALSE.toString())).getCount(), 1);
	}
	
	@Test
	public void shouldIncCounterWhenTriggerIsMisFired() {
		metricListener.triggerMisfired(trigger);
		assertEquals(metricRegistry.counter(String.format(JobMetricsCollector.METRIC_TRIGGER_MISFIRED, triggerName)).getCount(), 1);
	}
	
	@Test
	public void shouldRecordTimerWhenTriggerIsCompletedAndRoutingJobIsNotLazyFire() {
		long runTime = 10001;
		when(jobExecutionContext.getJobRunTime()).thenReturn(runTime);
		when(jobExecutionContext.getJobInstance()).thenReturn(mock(DummyJob.class));
		metricListener.triggerComplete(trigger, jobExecutionContext, CompletedExecutionInstruction.NOOP);
		assertEquals(metricRegistry.timer(String.format(JobMetricsCollector.METRIC_TRIGGER_COMPLETED, triggerName)).getSnapshot().getValue(0), runTime*1000000.0);
	}
	
	@Test
	public void shouldRecordTimerAsZeroWhenTriggerIsCompletedAndNotRoutingJob() {
		long runTime = 10001;
		when(jobExecutionContext.getJobRunTime()).thenReturn(runTime);
		metricListener.triggerComplete(trigger, jobExecutionContext, CompletedExecutionInstruction.NOOP);
		assertEquals(metricRegistry.timer(String.format(JobMetricsCollector.METRIC_TRIGGER_COMPLETED, triggerName)).getSnapshot().getValue(0), runTime*1000000.0);
	}
	
	@Test
	public void shouldIncrementCounterWhenTriggerIsCompletedAndRoutingJobIsNotLazyFire() {
		long runTime = 10001;
		when(jobExecutionContext.getJobRunTime()).thenReturn(runTime);
		when(jobExecutionContext.getJobInstance()).thenReturn(mock(DummyJob.class));
		metricListener.triggerComplete(trigger, jobExecutionContext, CompletedExecutionInstruction.NOOP);
		assertEquals(metricRegistry.counter(String.format(JobMetricsCollector.METRIC_TRIGGER_COMPLETED_COUNT, triggerName)).getCount(), 1);
	}
	
	@Test
	public void shouldIncrementCounterWhenTriggerIsCompletedAndNotRoutingJob() {
		long runTime = 10001;
		when(jobExecutionContext.getJobRunTime()).thenReturn(runTime);
		metricListener.triggerComplete(trigger, jobExecutionContext, CompletedExecutionInstruction.NOOP);
		assertEquals(metricRegistry.counter(String.format(JobMetricsCollector.METRIC_TRIGGER_COMPLETED_COUNT, triggerName, Boolean.FALSE.toString())).getCount(), 1);
	}
}
