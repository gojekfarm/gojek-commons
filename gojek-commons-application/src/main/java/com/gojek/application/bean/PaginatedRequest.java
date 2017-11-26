/**
 *
 */
package com.gojek.application.bean;

import javax.ws.rs.QueryParam;

import io.swagger.annotations.ApiParam;

/**
 * @author ganeshs
 *
 */
public class PaginatedRequest {

    @ApiParam(value="Page number to start querying from", allowableValues="range[1,infinity]", defaultValue="1")
    @QueryParam("page")
    private int page = 1;

    @ApiParam(value="Number of results per page", allowableValues="range[1,100]", defaultValue="10")
    @QueryParam("per_page")
    private int perPage = 10;
    
    /**
     * Default constructor
     */
    public PaginatedRequest() {
    }

    /**
     * @param page
     * @param perPage
     */
    public PaginatedRequest(int page, int perPage) {
        super();
        this.page = page;
        this.perPage = perPage;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the perPage
     */
    public int getPerPage() {
        return perPage;
    }

    /**
     * @param perPage the perPage to set
     */
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + page;
        result = prime * result + perPage;
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
        PaginatedRequest other = (PaginatedRequest) obj;
        if (page != other.page)
            return false;
        if (perPage != other.perPage)
            return false;
        return true;
    }
}
