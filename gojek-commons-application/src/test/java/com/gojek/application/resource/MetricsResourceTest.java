/**
 *
 */
package com.gojek.application.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.test.JerseyTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gojek.util.serializer.Serializer;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
/**
 * @author gayatri.mali
 *
 */
public class MetricsResourceTest {

	private MetricRegistry metricRegistry;

	private JerseyTest jerseyTest;

	private ObjectMapper mapper = Jackson.newObjectMapper();

	@BeforeClass
	public void setup() throws Exception {
		metricRegistry = mock(MetricRegistry.class);
		setUpJerseyTest();
	}
	
	@AfterClass
	public void teardown() throws Exception {
		if (jerseyTest != null) {
			jerseyTest.tearDown();
		}
	}

	private void setUpJerseyTest() throws Exception {
		jerseyTest = new JerseyTest() {
			@Override
			protected Application configure() {
				final DropwizardResourceConfig config = DropwizardResourceConfig
						.forTesting(new MetricRegistry());
				config.register(
						new JacksonMessageBodyProvider(mapper));
				config.register(new MetricsResource(metricRegistry));
				return config;
			}
		};
		jerseyTest.setUp();
	}
	
	@Test
	public void shouldGetMetricByKeyNameAndType() {
		Counter counter = new Counter();
		counter.inc();
		SortedMap<String, Counter> map = mock(SortedMap.class);
		when(metricRegistry.getCounters()).thenReturn(map);
		when(map.get("FileScan.file_created")).thenReturn(counter);
		String object = jerseyTest.client().target("/metrics/").queryParam("key", "FileScan.file_created").queryParam("metric_type", "counter").request().buildGet().invoke().readEntity(String.class);
		Map<String, Integer> newCounter = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(object.toString(), Map.class);
		assertEquals(1, newCounter.get("count").intValue());
	}

	@Test
	public void shouldGetAllMetricsIfMetricTypeIsWrong() {
		Metric metric = new Counter();
		Map<String, Metric> map = new HashMap<String, Metric>();
		map.put("counters", metric);
		when(metricRegistry.getMetrics()).thenReturn(map);
		String object = jerseyTest.client().target("/metrics/").queryParam("key", "FileScan.file_created").queryParam("metric_type", "count").request().buildGet().invoke().readEntity(String.class);
		Map<String, Map<String, Integer>> metricMap = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(object.toString(), Map.class);
		int expectedCount = new Long(((Counter) metric).getCount()).intValue();
		int actualValue = metricMap.get("counters").get("count").intValue();
		assertEquals(expectedCount, actualValue);
	}

	@Test
	public void shouldGetAllMetricsIfMetricTypeIsNull() {
		Metric metric = new Counter();
		Map<String, Metric> map = new HashMap<String, Metric>();
		map.put("counters", metric);
		when(metricRegistry.getMetrics()).thenReturn(map);
		String object = jerseyTest.target("/metrics/").queryParam("key", "FileScan.file_created").request().buildGet().invoke().readEntity(String.class);
		Map<String, Map<String, Integer>> metricMap = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(object.toString(), Map.class);
		int expectedCount = new Long(((Counter) metric).getCount()).intValue();
		int actualValue = metricMap.get("counters").get("count").intValue();
		assertEquals(expectedCount, actualValue);
	}
}
