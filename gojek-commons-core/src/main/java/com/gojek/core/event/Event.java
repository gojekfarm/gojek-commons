/**
 *
 */
package com.gojek.core.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.ConvertUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", defaultImpl = Event.class)
public class Event {
	
	/**
	 * The unique identity of this event
	 */
	private String eventId;

	/**
	 * The identifier of the entity for which this event is raised
	 */
	private String entityId;
	
	/**
	 * The type of this event
	 */
	private String type;
	
	/**
	 * Custom attributes associated with this event
	 */
	private Map<String, String> attributes = new HashMap<String, String>();
	
	/**
	 * The time at which this event was raised
	 */
	private DateTime eventDate;
	
	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * Default constrcutor
	 */
	public Event() {
		 eventId = UUID.randomUUID().toString();
	}
	
	/**
	 * @param entityId
	 * @param type
	 * @param eventDate
	 */
	public Event(String entityId, String type, DateTime eventDate) {
		this();
		this.entityId = entityId;
		this.type = type;
		this.eventDate = eventDate;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the eventDate
	 */
	public DateTime getEventDate() {
		return eventDate;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(DateTime eventDate) {
		this.eventDate = eventDate;
	}
    
    /**
     * @param key
     * @param value
     */
    protected void addAttribute(String key, Object value) {
        getAttributes().put(key, ConvertUtils.convert(value));
    }
    
    /**
     * @param key
     * @return
     */
    protected String getAttribute(String key) {
        return getAttributes().get(key);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
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
		Event other = (Event) obj;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", entityId=" + entityId + ", type=" + type + ", attributes=" + attributes
				+ ", eventDate=" + eventDate + "]";
	}
}
