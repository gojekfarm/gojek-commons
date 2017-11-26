/**
 * 
 */
package com.gojek.job;

import java.util.List;

import com.gojek.job.JobConfiguration.Job;
import com.gojek.job.JobConfiguration.Schedule;

/**
 * @author ganeshs
 *
 */
public interface JobCollection {

    /**
     * @return
     */
    List<Job> getJobs(String groupName, Schedule schedule);
}
