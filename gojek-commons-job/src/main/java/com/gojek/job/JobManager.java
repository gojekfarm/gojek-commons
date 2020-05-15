/**
 * 
 */
package com.gojek.job;

import static java.util.Objects.nonNull;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.guice.util.GuiceUtil;
import com.gojek.job.JobConfiguration.Job;
import com.gojek.job.JobConfiguration.Schedule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.ServerLifecycleListener;

/**
 * @author vishnuchilamakuru
 *
 */
@Singleton
public class JobManager implements Managed, ServerLifecycleListener {
	
	private Scheduler scheduler;
	
	private JobFactory jobFactory;
	
	private QuartzConfiguration quartzConfiguration;
	
	private JobConfiguration jobConfiguration;
	
	private TriggerListener triggerListener;
	
	public static final String KEY_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS = "job_running_time_threshold_breach_in_secs";
	
	public static final Integer DEFAULT_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS = 30*60;
	
	private static Logger logger = LoggerFactory.getLogger(JobManager.class);
	
	/**
	 * @param quartzConfiguration
	 * @param jobConfiguration
	 * @param jobFactory
	 * @param triggerListener
	 */
	public void init(QuartzConfiguration quartzConfiguration, JobConfiguration jobConfiguration, JobFactory jobFactory, TriggerListener triggerListener) {
	    this.quartzConfiguration = quartzConfiguration;
        this.jobConfiguration = jobConfiguration;
        this.jobFactory = jobFactory;
        this.triggerListener = triggerListener;
	}

	@Override
	public void start() throws Exception {
		logger.info("Starting the job manager");
		StdSchedulerFactory factory = new StdSchedulerFactory(this.quartzConfiguration.toProperties());
		this.scheduler = factory.getScheduler();
		if (this.jobFactory != null) {
		    this.scheduler.setJobFactory(this.jobFactory);
		}
		if (this.triggerListener != null) {
		    this.scheduler.getListenerManager().addTriggerListener(this.triggerListener);
		}
	}
	
	/**
	 * For unit testing
	 * 
	 * @return
	 */
	Scheduler getScheduler() {
	    return this.scheduler;
	}
	
	/**
     * For unit testing
     * 
     * @return
     */
	TriggerListener getTriggerListener() {
        return triggerListener;
    }
	
	/**
     * For unit testing
     * 
     * @return
     */
	JobFactory getJobFactory() {
        return jobFactory;
    }
	
	@Override
	public void serverStarted(Server server) {
	    if (! this.jobConfiguration.isJobProcessingEnabled()) {
	        logger.info("Job processing is disabled. Not scheduling the jobs");
	        return;
	    }
	    
	    logger.info("Scheduling the jobs");
        this.jobConfiguration.getJobs().forEach((job) -> {
            addJob(job);
        });
        
        logger.info("Starting the scheduler");
        try {
            if (! this.scheduler.isStarted()) {
                this.scheduler.start();
            }
        } catch (Exception e) {
            logger.error("Failed while starting the scheduler", e);
            throw new JobException("Failed while starting the scheduler", e);
        }
	}
	
	/**
	 * Checks if the scheduler is started
	 * 
	 * @return
	 */
	private boolean isStarted() {
	    try {
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            throw new JobException("Faild while checking if the scheduler is started", e);
        }
	}
	
	/**
	 * Fires the job immediately. If the job is not created, it creates that and then triggers immediately
	 * 
	 * @param job
	 * @param data
	 */
	public void fireNow(Job job, Map<String, Object> data) {
	    if (! isStarted()) {
	        throw new IllegalStateException("Scheduler is not started");
	    }
	    logger.info("Firing the job {} with the data {}", job.getJobClass(), data);
	    List<JobDetail> jobDetails = addJob(job);
	    jobDetails.forEach(jobDetail -> {
	        try {
	            scheduler.triggerJob(jobDetail.getKey(), new JobDataMap(data));
	        } catch (Exception e) {
	            throw new JobException("Failed while triggering the job - " + jobDetail.getKey(), e); 
	        }
	    });
	}
	
	/**
	 * Adds the job if not exists. Else updates the job
	 * 
	 * @param job
	 */
    public List<JobDetail> addJob(Job job) {
        List<JobDetail> jobDetails = Lists.newArrayList();
        if (!job.isEnabled()) {
		logger.info("Job - {} is not enabled", job.getName());
		deleteJob(job);
            return jobDetails;
        }
        if (job.isCollection()) {
            jobDetails.addAll(scheduleCollection(job));
        } else {
            jobDetails.add(scheduleJob(job, null));
        }
        return jobDetails;
    }
	
	/**
     * @param job
     */
    protected List<JobDetail> scheduleCollection(Job job) {
        JobCollection collection = constructJobCollection(job.getJobCollectionClass());
        return collection.getJobs(job.getGroupName(), job.getSchedule()).stream().map(j -> scheduleJob(j, job)).collect(Collectors.toList());
    }
    
    /**
     * @param clazz
     * @return
     */
    protected JobCollection constructJobCollection(Class<? extends JobCollection> clazz) {
        return GuiceUtil.getInstance(clazz);
    }
	
	/**
	 * @param job
	 * @param parent
	 */
	protected JobDetail scheduleJob(Job job, Job parent) {
	    String name = job.getName();
	    try {
	        Map<String, Object> jobData = Maps.newHashMap(job.getData());
	        if (parent != null) {
	            jobData.putAll(parent.getData());
	        }
	        
            JobBuilder builder = newJob(job.getJobClass()).withIdentity(name, job.getGroupName()).usingJobData(new JobDataMap(jobData)).storeDurably();
            JobDetail jobDetail = builder.build();
            if (job.getSchedule() != null) {
                createOrUpdateTrigger(jobDetail, job.getSchedule(), name, job.getGroupName());
            }
            this.scheduler.addJob(jobDetail, true);
            return jobDetail;
	    } catch (SchedulerException e) {
	        logger.error("Failed while scheduling the job - " + job.getJobClass(), e);
            throw new JobException("Failed while scheduling the job", e);
	    }
	}
	
	@Override
	public void stop() throws Exception {
		logger.info("Stopping the job manager");
		for (JobExecutionContext context : this.scheduler.getCurrentlyExecutingJobs()) {
			this.scheduler.interrupt(context.getFireInstanceId());
		}
		this.scheduler.shutdown();
	}
	
	/**
	 * Returns the list of long running job execution contexts
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<JobExecutionContext> getLongRunningJobs() throws SchedulerException {
	    return this.scheduler.getCurrentlyExecutingJobs().stream().filter((context) -> {
	        Integer thresholdBreachInSecs = DEFAULT_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS;
	        if (context.getJobDetail().getJobDataMap().containsKey(KEY_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS)) {
	            thresholdBreachInSecs = context.getJobDetail().getJobDataMap().getInt(KEY_JOB_RUNNING_TIME_THRESHOLD_BREACH_IN_SECS);
	        }
            return Seconds.secondsBetween(new DateTime(context.getFireTime()), DateTime.now()).getSeconds() > thresholdBreachInSecs;
        }).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private void createOrUpdateTrigger(JobDetail job, Schedule schedule, String name, String groupName) throws SchedulerException {
		Trigger oldTrigger = this.scheduler.getTrigger(TriggerKey.triggerKey(name, groupName));
		if (nonNull(oldTrigger)) {
			Trigger updatedTrigger = oldTrigger.getTriggerBuilder().withIdentity(name, groupName).withSchedule(createSchedule(schedule)).build();
			this.scheduler.rescheduleJob(updatedTrigger.getKey(), updatedTrigger);
        } else {
			Trigger trigger = newTrigger().withIdentity(name, groupName).withSchedule(createSchedule(schedule)).build();
			this.scheduler.scheduleJob(job, trigger);
        }
	}
		
	/**
	 * Creates a simple or cron schedule
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
    protected ScheduleBuilder createSchedule(Schedule schedule) {
	    if (schedule == null) {
	        return null;
	    }
	    if (nonNull(schedule.getCron())) {
			CronScheduleBuilder cb = CronScheduleBuilder.cronSchedule(schedule.getCron()).inTimeZone(DateTimeZone.UTC.toTimeZone());
			switch (schedule.getCronMisfireInstruction()) {
				case CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
					return cb.withMisfireHandlingInstructionFireAndProceed();
				case CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
					return cb.withMisfireHandlingInstructionDoNothing();
			}
			return cb;
	    } else {
	        return SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(schedule.getInterval()).repeatForever();	        
	    }
	}

    private void deleteJob(Job job) {
        try {
            boolean result = job.isCollection() ? scheduler.deleteJobs(deleteJobCollection(job)) :
                    scheduler.deleteJob(new JobKey(job.getName(), job.getGroupName()));
            if (!result) {
                logger.warn("Job does not exist - " + job.getName());
            }
        } catch (SchedulerException e) {
            logger.error("Failed while deleting the job - " + job.getName(), e);
            throw new JobException("Failed while deleting the job", e);
        }
    }

	private List<JobKey> deleteJobCollection(Job job) {
		JobCollection collection = constructJobCollection(job.getJobCollectionClass());
		return collection.getJobs(job.getGroupName(), job.getSchedule()).stream().map(j -> new JobKey(j.getName(), j.getGroupName())).collect(Collectors.toList());
	}
}
