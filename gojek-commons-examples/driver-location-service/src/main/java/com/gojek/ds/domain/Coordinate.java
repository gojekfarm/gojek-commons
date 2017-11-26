/**
 * 
 */
package com.gojek.ds.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @author ganeshs
 *
 */
@Embeddable
public class Coordinate implements Serializable {

    @NotNull
    @Max(90)
    private double lat;

    @NotNull
    @Max(180)
    private double lng;

    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public Coordinate() {
    }

    /**
     * @param lat
     * @param lng
     */
    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return the lng
     */
    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "Coordinate [lat=" + lat + ", lng=" + lng + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (Math.abs(lat-other.lat) > 0.00001)
            return false;
        if (Math.abs(lng-other.lng) > 0.00001)
            return false;
        return true;
    }
}
