/**
 * 
 */
package com.gojek.ds.domain;

import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.gojek.jpa.entity.BaseRecordableModel;

/**
 * @author ganeshs
 *
 */
@Table(name="drivers")
@Entity
@Access(AccessType.FIELD)
public class Driver extends BaseRecordableModel {
    
    /**
     * @author ganeshs
     *
     */
    public enum Status {
        active, busy, inactive
    }
    
    /**
     * @author ganeshs
     *
     */
    public enum VehicleType {
        bike, car, truck
    }

    @Id
    private String id;
    
    @NotEmpty
    private String name;
    
    @NotEmpty
    private String phone;
    
    @Embedded
    private Coordinate coordinate;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastSeenTime;
    
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.active;
    
    /**
     * Default constructor
     */
    public Driver() {
        setId(UUID.randomUUID().toString());
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * @param coordinate the coordinate to set
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * @return the lastSeenTime
     */
    public DateTime getLastSeenTime() {
        return lastSeenTime;
    }

    /**
     * @param lastSeenTime the lastSeenTime to set
     */
    public void setLastSeenTime(DateTime lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    /**
     * @return the vehicleType
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * @param vehicleType the vehicleType to set
     */
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }
    
    /**
     * Marks the driver as active
     */
    public void markActive() {
        setStatus(Status.active);
        persist();
    }
    
    /**
     * Marks the driver as inactive
     */
    public void markInactive() {
        setStatus(Status.inactive);
        persist();
    }
    
    /**
     * Marks the driver as busy
     */
    public void markBusy() {
        setStatus(Status.busy);
        persist();
    }
}
