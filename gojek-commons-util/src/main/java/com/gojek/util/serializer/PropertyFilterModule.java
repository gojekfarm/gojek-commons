/**
 *
 */
package com.gojek.util.serializer;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Jackson module for filtering the properties
 *
 * @author ganeshs
 *
 */
public class PropertyFilterModule extends SimpleModule {
	
    private static final long serialVersionUID = 1L;
    
    public static final String PROPERTY_FILTER_ID = "property_filter";
    
    @JsonFilter(PROPERTY_FILTER_ID)  
	public class PropertyFilterMixIn {}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(Object.class, PropertyFilterMixIn.class);
		((ObjectMapper) context.getOwner()).setFilters(new SimpleFilterProvider().addFilter(PROPERTY_FILTER_ID, SimpleBeanPropertyFilter.serializeAllExcept(new String[0])));
		super.setupModule(context);
	}
}
