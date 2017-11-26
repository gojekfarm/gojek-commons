/**
 *
 */
package com.gojek.guice.jpa;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.activejpa.jpa.JPAContext;
import org.activejpa.utils.OpenSessionInViewFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.UnitOfWork;

/**
 * HACK: Using this instead of {@link com.google.inject.persist.PersistFilter} as it conflicts with {@link OpenSessionInViewFilter}. 
 * The entity manager is closed by {@link OpenSessionInViewFilter} and this results in an error in {@link com.google.inject.persist.PersistFilter}
 *
 * {@link UnitOfWork#begin()} is called by {@link JPAContext} already. So calling only the {@link UnitOfWork#end()} here
 *
 * @author ganeshs
 *
 */
public class OSIVPersistFilter implements Filter {
	
	private UnitOfWork unitOfWork;
	
	private static final Logger logger = LoggerFactory.getLogger(OSIVPersistFilter.class);
	
	@Inject
	public OSIVPersistFilter(UnitOfWork unitOfWork) {
		this.unitOfWork = unitOfWork;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} finally {
			try {
				unitOfWork.end();
			} catch (Exception e) {
				logger.error("Failed while ending the unit of work", e);
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
