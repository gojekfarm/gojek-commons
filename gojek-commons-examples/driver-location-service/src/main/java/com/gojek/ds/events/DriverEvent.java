/**
 * 
 */
package com.gojek.ds.events;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gojek.core.event.Event;
import com.gojek.ds.domain.Driver;

/**
 * @author ganeshs
 *
 */
@JsonInclude(NON_NULL)
public abstract class DriverEvent extends Event {
    
    public static final String ATTR_VEHICLE_TYPE = "vehicle_type";

    /**
     * @param driver
     * @param type
     * @param eventtime
     */
    public DriverEvent(Driver driver, String type, DateTime eventtime) {
        this(driver.getId(), type, eventtime);
        addAttribute(ATTR_VEHICLE_TYPE, driver.getVehicleType());
    }
    
    /**
     * @param driverId
     * @param type
     * @param eventtime
     */
    public DriverEvent(String driverId, String type, DateTime eventtime) {
        super(driverId, type, eventtime);
    }
}
