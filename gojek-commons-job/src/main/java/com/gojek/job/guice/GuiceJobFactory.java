/**
 * 
 */
package com.gojek.job.guice;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.guice.util.GuiceUtil;

/**
 * @author ganeshs
 *
 */
public class GuiceJobFactory implements JobFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(GuiceJobFactory.class);

	@Override
	public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
		JobDetail jobDetail = triggerFiredBundle.getJobDetail();
		Class<? extends Job> jobClass = jobDetail.getJobClass();
		try {
			return GuiceUtil.getInstance(jobClass);
		} catch (Exception e) {
			logger.error("Failed while getting the job instance", e);
			throw new UnsupportedOperationException(e);
		}
	}
}
