package com.gojek.jpa.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.activejpa.entity.Model;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author ganeshs
 *
 */
@MappedSuperclass
public abstract class BaseRecordableModel extends Model {
	
	private Boolean deleted = false;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime createdAt;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime updatedAt;

	/**
	 * @return the deleted
	 */
	@NotNull
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	/**
	 * @return the createdAt
	 */
	@NotNull
	@JsonIgnore
	public DateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the updatedAt
	 */
	@NotNull
	@JsonIgnore
	public DateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(DateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@Override
	public void delete() {
	    setDeleted(true);
	}
	
	@PrePersist
	protected void preCreate() {
		DateTime now = DateTime.now();
		setCreatedAt(now);
		setUpdatedAt(now);
	}
	
	@PreUpdate
	protected void preUpdate() {
	    setUpdatedAt(DateTime.now());
	}
}