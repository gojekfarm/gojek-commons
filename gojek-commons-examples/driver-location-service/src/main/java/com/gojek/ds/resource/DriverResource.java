/**
 * 
 */
package com.gojek.ds.resource;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.gojek.ds.bean.DriverLoctionRequest;
import com.gojek.ds.domain.Driver;
import com.gojek.ds.service.DriverService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author ganeshs
 *
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/v1/drivers")
@Api(tags = "Driver Apis")
public class DriverResource {
    
    private DriverService driverService;
    
    /**
     * @param driverService
     */
    @Inject
    public DriverResource(DriverService driverService) {
        this.driverService = driverService;
    }
    
    @POST
    @ApiOperation("Create Driver")
    public Driver createDriver(Driver driver) {
        driver.persist();
        return driver;
    }

    @PUT
    @Path("/{id}/locations")
    @ApiOperation("Update current location of the driver")
    public void updateLocation(@PathParam("id") String id, DriverLoctionRequest request) {
    }
    
    @PUT
    @Path("/{id}/active")
    @ApiOperation("Mark driver as active")
    public void markActive(@PathParam("id") String id) {
        this.driverService.markActive(getDriver(id));
    }
    
    @PUT
    @Path("/{id}/inactive")
    @ApiOperation("Mark driver as inactive")
    public void markInactive(@PathParam("id") String id) {
        this.driverService.markInactive(getDriver(id));
    }
    
    @GET
    @Path("/")
    @ApiOperation("Search near-by drivers by vehicle type")
    public void searchDrivers(@NotNull @QueryParam("location") String location, @NotNull @QueryParam("vehicle_type") String vehicleType) {
        
    }
    
    /**
     * @param id
     * @return
     */
    protected Driver getDriver(String id) {
        Driver driver = Driver.findById(id);
        if (driver == null) {
            throw new NotFoundException("Driver with id " + id + "is not found");
        }
        return driver;
    }
}
