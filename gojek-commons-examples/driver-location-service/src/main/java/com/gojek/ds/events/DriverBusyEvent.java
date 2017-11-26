/**
 * 
 */
package com.gojek.ds.events;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gojek.ds.domain.Driver;

/**
 * @author ganeshs
 *
 */
@JsonTypeName(DriverBusyEvent.TYPE)
public class DriverBusyEvent extends DriverEvent {

    public static final String TYPE = "driver_busy_event";
    
    public static final String ATTR_JOB_ID = "job_id";
    
    /**
     * @param driver
     * @param jobId
     */
    public DriverBusyEvent(Driver driver, String jobId) {
        super(driver, TYPE, DateTime.now());
        addAttribute(ATTR_JOB_ID, jobId);
    }
    
    /**
     * @param driverId
     * @param type
     * @param eventDate
     */
    @JsonCreator
    DriverBusyEvent(@JsonProperty("entity_id") String driverId, @JsonProperty("type") String type, @JsonProperty("event_date") DateTime eventDate) {
        super(driverId, type, eventDate);
    }
}
