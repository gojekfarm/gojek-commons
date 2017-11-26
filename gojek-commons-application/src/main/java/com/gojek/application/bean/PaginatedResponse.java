/**
 *
 */
package com.gojek.application.bean;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author ganeshs
 *
 */
@ApiModel(value="PaginatedResponse", description="Wraps the search result along with pagination data")
public class PaginatedResponse<T> {

	@ApiModelProperty(value="List of entities matching the search", required=true)
	private List<T> items;

	@ApiModelProperty(value="Page number to start querying from", allowableValues="range[1,infinity]", required=true) 
	private int page;

	@ApiModelProperty(value="Number of results per page", allowableValues="range[1,100]", required=true) 
	private int perPage;

	@ApiModelProperty(value="Count of results in this page", allowableValues="range[0,100]", required=true) 
	private int count;

	@ApiModelProperty(value="Total number of results available for this search", allowableValues="range[0,infinity]", required=true)
	private int total;

	/**
	 * @param items
	 * @param page
	 * @param perPage
	 * @param count
	 * @param total
	 */
    public PaginatedResponse(List<T> items, int page, int perPage, int count, int total) {
	    this.items = items;
	    this.page = page;
	    this.perPage = perPage;
	    this.count = count;
	    this.total = total;
    }

	/**
	 * @return the items
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<T> items) {
		this.items = items;
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

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
}
