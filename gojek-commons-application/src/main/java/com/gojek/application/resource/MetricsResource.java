/**
 *
 */
package com.gojek.application.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

/**
 * @author gayatri.mali
 *
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/metrics")
public class MetricsResource {

	private final MetricRegistry metricRegistry;

	private static final Logger logger = LoggerFactory.getLogger(MetricsResource.class);

	/**
	 * @param metricRegistry
	 */
	public MetricsResource(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}

	@GET
	@Path("/")
	public Object fetchMetricByKeyAndType(@QueryParam("key") String key, @QueryParam("metric_type") String metricType) {
		logger.debug("key =  {} and metricType = {}", key, metricType);

		if (metricType == null) {
			return metricRegistry.getMetrics();
		}

		Object value = null;
		switch (metricType) {
		case "gauge":
			value = metricRegistry.getGauges().get(key);
			break;
		case "meter":
			value = metricRegistry.getMeters().get(key);
			break;
		case "counter":
			value = metricRegistry.getCounters().get(key);
			break;
		case "timer":
			value = metricRegistry.getTimers().get(key);
			break;
		case "histogram":
			value = metricRegistry.getHistograms().get(key);
			break;
		default:
			value = metricRegistry.getMetrics();
			break;
		}
		return value;
	}
}
