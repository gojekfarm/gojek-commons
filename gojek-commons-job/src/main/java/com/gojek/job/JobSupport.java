/**
 * 
 */
package com.gojek.job;

/**
 * @author ganeshs
 *
 */
public interface JobSupport {

    /**
     * @return
     */
    QuartzConfiguration getQuartzConfiguration();
    
    /**
     * @return
     */
    JobConfiguration getJobConfiguration();
}
