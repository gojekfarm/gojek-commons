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
@JsonTypeName(DriverActiveEvent.TYPE)
public class DriverActiveEvent extends DriverEvent {

    public static final String TYPE = "driver_active_event";
    
    /**
     * @param driver
     * @param jobId
     */
    public DriverActiveEvent(Driver driver) {
        super(driver, TYPE, DateTime.now());
    }
    
    /**
     * @param driverId
     * @param type
     * @param eventDate
     */
    @JsonCreator
    DriverActiveEvent(@JsonProperty("entity_id") String driverId, @JsonProperty("type") String type, @JsonProperty("event_date") DateTime eventDate) {
        super(driverId, type, eventDate);
    }
}
