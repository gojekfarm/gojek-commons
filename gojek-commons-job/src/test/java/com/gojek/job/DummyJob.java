/**
 * 
 */
package com.gojek.job;

import org.quartz.JobExecutionContext;

import com.gojek.job.jpa.AbstractJob;

/**
 * @author ganeshs
 *
 */
public class DummyJob extends AbstractJob {

    @Override
    protected void run(JobExecutionContext context) {
    }

}
