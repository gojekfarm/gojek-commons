/**
 * 
 */
package com.gojek.job;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Configuration used to tune the {@link JobManager}
 * 
 * @author vishnuchilamakuru
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobConfiguration {

    private boolean jobProcessingEnabled;

    private List<Job> jobs = Lists.newArrayList();

    /**
     * @return the jobProcessingEnabled
     */
    public boolean isJobProcessingEnabled() {
        return jobProcessingEnabled;
    }

    /**
     * @param jobProcessingEnabled
     *            the jobProcessingEnabled to set
     */
    public void setJobProcessingEnabled(boolean jobProcessingEnabled) {
        this.jobProcessingEnabled = jobProcessingEnabled;
    }

    /**
     * @return the jobs
     */
    public List<Job> getJobs() {
        return jobs;
    }

    /**
     * @param jobs the jobs to set
     */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
    
    /**
     * @author ganeshs
     *
     */
    public static class Schedule {

        private String cron;

        private Integer interval;
        
        /**
         * Default constructor
         */
        public Schedule() {
        }
        
        /**
         * @param interval
         */
        public Schedule(Integer interval) {
            this.interval = interval;
        }

        /**
         * @param cron
         */
        public Schedule(String cron) {
            this.cron = cron;
        }

        /**
         * @return the cron
         */
        public String getCron() {
            return cron;
        }

        /**
         * @param cron the cron to set
         */
        public void setCron(String cron) {
            this.cron = cron;
        }

        /**
         * @return the interval
         */
        public Integer getInterval() {
            return interval;
        }

        /**
         * @param interval the interval to set
         */
        public void setInterval(Integer interval) {
            this.interval = interval;
        }
    }

    /**
     * @author ganeshs
     *
     */
    public static class Job {
        
        private String name;
        
        private boolean enabled;
        
        private Class<? extends org.quartz.Job> jobClass;
        
        private Class<? extends JobCollection> jobCollectionClass;
        
        private Schedule schedule;
        
        private String groupName;
        
        private Map<String, Object> data = Maps.newHashMap();
        
        /**
         * Default constructor
         */
        public Job() {
        }
        
        /**
         * @param name
         * @param jobClass
         * @param groupName
         * @param data
         * @param enabled
         */
        public Job(String name, Class<? extends org.quartz.Job> jobClass, String groupName, Map<String, Object> data, boolean enabled) {
            this.name = name;
            this.jobClass = jobClass;
            this.groupName = groupName;
            this.data = data;
            this.enabled = enabled;
        }
        
        /**
         * @param name
         * @param jobClass
         * @param groupName
         * @param schedule
         * @param data
         * @param enabled
         */
        public Job(String name, Class<? extends org.quartz.Job> jobClass, String groupName, Schedule schedule, Map<String, Object> data, boolean enabled) {
            this(name, jobClass, groupName, data, enabled);
            this.schedule = schedule;
        }

        /**
         * @return the name
         */
        public String getName() {
            if (name == null) {
                return jobClass != null ? jobClass.getName() : jobCollectionClass.getName();
            }
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the enabled
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * @param enabled the enabled to set
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * @return the jobClass
         */
        public Class<? extends org.quartz.Job> getJobClass() {
            return jobClass;
        }

        /**
         * @param jobClass the jobClass to set
         */
        public void setJobClass(Class<? extends org.quartz.Job> jobClass) {
            this.jobClass = jobClass;
        }

        /**
         * @return the groupName
         */
        public String getGroupName() {
            return groupName;
        }

        /**
         * @param groupName the groupName to set
         */
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        /**
         * @return the data
         */
        public Map<String, Object> getData() {
            return data;
        }

        /**
         * @param data the data to set
         */
        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        /**
         * @return the schedule
         */
        public Schedule getSchedule() {
            return schedule;
        }

        /**
         * @param schedule the schedule to set
         */
        public void setSchedule(Schedule schedule) {
            this.schedule = schedule;
        }

        /**
         * @return the jobCollectionClass
         */
        public Class<? extends JobCollection> getJobCollectionClass() {
            return jobCollectionClass;
        }

        /**
         * @param jobCollectionClass the jobCollectionClass to set
         */
        public void setJobCollectionClass(Class<? extends JobCollection> jobCollectionClass) {
            this.jobCollectionClass = jobCollectionClass;
        }
        
        /**
         * @return
         */
        public boolean isCollection() {
            return jobCollectionClass != null;
        }
    }

}
