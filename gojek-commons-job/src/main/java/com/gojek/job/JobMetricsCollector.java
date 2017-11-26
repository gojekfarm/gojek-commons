/**
 * 
 */
package com.gojek.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

/**
 * @author ganeshs
 *
 */
public class JobMetricsCollector implements TriggerListener {
	
	private MetricRegistry metricRegistry;
	
	private JobManager jobManager;
	
	public static final String NAME = "JOB_METRICS";
	
	public static final String METRIC_TRIGGER_FIRED = "jobs.fired.count[trigger:%s]";
	
	public static final String METRIC_TRIGGER_MISFIRED = "jobs.misfired.count[trigger:%s]";
	
	public static final String METRIC_TRIGGER_COMPLETED = "jobs.completed[trigger:%s]";
	
	public static final String METRIC_TRIGGER_COMPLETED_COUNT = "jobs.completed.real_count[trigger:%s]";
	
	public static final String METRIC_LONG_RUNNING_JOB = "jobs.long_running.count";
	
	private static final Logger logger = LoggerFactory.getLogger(JobMetricsCollector.class);
	
	/**
	 * @param jobManager
	 * @param metricRegistry
	 */
	public void init(JobManager jobManager, MetricRegistry metricRegistry) {
	    this.jobManager = jobManager;
	    this.metricRegistry = metricRegistry;
	    this.metricRegistry.gauge(METRIC_LONG_RUNNING_JOB, () -> {
	        return () -> { 
	            try { 
	                List<JobExecutionContext> jobs = this.jobManager.getLongRunningJobs();
	                logger.info("Long running jobs - {}", jobs);
	                return jobs.size();
    	        } catch (Exception e) {
    	            logger.error("Failed while getting the long running jobs", e);
    	            return 0;
    	        }
	        };
	    });
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		logger.info("Trigger {} with instance id {} and fire time {} fired job {} at: {}", trigger.getKey(), context.getFireInstanceId(), context.getFireTime(), trigger.getJobKey(), new Date());
		metricRegistry.counter(String.format(METRIC_TRIGGER_FIRED, trigger.getKey().getName())).inc();
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return false;
	}

	@Override
	public void triggerMisfired(Trigger trigger) {
		logger.info("Trigger {} misfired job {}  at: {}.  Should have fired at: {}", trigger.getKey(), trigger.getJobKey(), new Date(), trigger.getNextFireTime());
		metricRegistry.counter(String.format(METRIC_TRIGGER_MISFIRED, trigger.getKey().getName())).inc();
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
		logger.info("Trigger {} with instance id {} completed job {} at: {} with resulting trigger instruction code {}. Time taken - {} ", trigger.getKey(), context.getFireInstanceId(), trigger.getJobKey(), new Date(), triggerInstructionCode, context.getJobRunTime());
		metricRegistry.timer(String.format(METRIC_TRIGGER_COMPLETED, trigger.getKey().getName())).update(context.getJobRunTime(), TimeUnit.MILLISECONDS);
		metricRegistry.counter(String.format(METRIC_TRIGGER_COMPLETED_COUNT, trigger.getKey().getName())).inc();
	}

}
