/**
 * 
 */
package com.gojek.job.guice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gojek.guice.util.GuiceUtil;
import com.gojek.job.DummyJob;
import com.google.inject.Injector;

/**
 * @author ganeshs
 *
 */
public class GuiceJobFactoryTest {

	private Injector injector;
	
	private GuiceJobFactory jobFactory;
	
	@BeforeMethod
	public void beforeMethod() {
		injector = mock(Injector.class);
		GuiceUtil.load(injector);
		jobFactory = new GuiceJobFactory();
	}
	
	@AfterMethod
	public void afterMethod() {
	    GuiceUtil.reset();
	}
	
	@Test
	public void shouldGetAnInstanceOfJobFromGuice() throws SchedulerException {
		TriggerFiredBundle bundle = mock(TriggerFiredBundle.class);
		Scheduler scheduler = mock(Scheduler.class);
		JobDetail job = JobBuilder.newJob(DummyJob.class).build();
		when(bundle.getJobDetail()).thenReturn(job);
		DummyJob routingJob = mock(DummyJob.class);
		when(injector.getInstance(DummyJob.class)).thenReturn(routingJob);
		Job newJob = jobFactory.newJob(bundle, scheduler);
		assertEquals(newJob, routingJob);
	}
	
	@Test(expectedExceptions=UnsupportedOperationException.class)
	public void shouldThrowExceptionIfInjectorCouldntGetInstance() throws SchedulerException {
		TriggerFiredBundle bundle = mock(TriggerFiredBundle.class);
		Scheduler scheduler = mock(Scheduler.class);
		JobDetail job = JobBuilder.newJob(DummyJob.class).build();
		when(bundle.getJobDetail()).thenReturn(job);
		when(injector.getInstance(DummyJob.class)).thenThrow(new RuntimeException());
		jobFactory.newJob(bundle, scheduler);
	}
}
