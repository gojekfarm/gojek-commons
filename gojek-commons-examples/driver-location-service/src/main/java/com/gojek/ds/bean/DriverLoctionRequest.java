/**
 * 
 */
package com.gojek.ds.bean;

import com.gojek.ds.domain.Driver.Status;
import com.gojek.ds.domain.Driver.VehicleType;

/**
 * @author ganeshs
 *
 */
public class DriverLoctionRequest {

    private Double lat;
    
    private Double lng;
    
    private VehicleType vehicleType;
    
    private Status status;
    
    /**
     * Default constructor
     */
    public DriverLoctionRequest() {
    }

    /**
     * @param lat
     * @param lng
     * @param vehicleType
     * @param status
     */
    public DriverLoctionRequest(Double lat, Double lng, VehicleType vehicleType, Status status) {
        this.lat = lat;
        this.lng = lng;
        this.vehicleType = vehicleType;
        this.status = status;
    }

    /**
     * @return the lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * @return the lng
     */
    public Double getLng() {
        return lng;
    }

    /**
     * @param lng the lng to set
     */
    public void setLng(Double lng) {
        this.lng = lng;
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
}
