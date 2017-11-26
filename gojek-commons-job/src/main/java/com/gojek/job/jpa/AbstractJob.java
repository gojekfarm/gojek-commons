/**
 * 
 */
package com.gojek.job.jpa;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gojek.guice.jpa.ActiveJpaGuiceExecutionWrapper;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractJob implements InterruptableJob {
    
    private Thread runningThread;
	
	protected ActiveJpaGuiceExecutionWrapper executionWrapper;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractJob.class);
	
	/**
	 * Default constrcutor
	 */
	public AbstractJob() {
	    this.executionWrapper = new ActiveJpaGuiceExecutionWrapper();
	}

	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		runningThread = Thread.currentThread();
		try {
		    executionWrapper.execute((optional) -> {
	            run(jobContext);
	            return null;
	        });
		} catch(Exception e) {
			logger.error("Failed while running the job", e);
		}
	}

	/**
	 * @param context
	 */
	protected abstract void run(JobExecutionContext context);
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		if (runningThread != null) {
			logger.info("Interrupting thread - {}", runningThread.toString());
			runningThread.interrupt();
		}
	}
}
