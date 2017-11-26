/**
 *
 */
package com.gojek.application.metrics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

/**
 * @author ganeshs
 *
 */
public class EnhancedMetricRegistryTest {
	
	private EnhancedMetricRegistry metricRegistry;
	
	@BeforeMethod
	public void setup() {
		metricRegistry = new EnhancedMetricRegistry(new MetricRegistry());
	}

	@Test
	public void shouldCreateCounterIfNotExistAlready() {
		Counter counter = metricRegistry.counter("new_counter");
		assertNotNull(counter);
	}
	
	@Test
	public void shouldReturnSameCounterIfExistAlready() {
		Counter counter = metricRegistry.counter("new_counter");
		Counter existingCounter = metricRegistry.counter("new_counter");
		assertEquals(counter, existingCounter);
	}
	
	@Test
	public void shouldRegisterGuageWhenCounterDoesntExistAlready() {
		metricRegistry.counter("new_counter");
		Map<String, Gauge> gauges = metricRegistry.getGauges((name, metric) -> { return name.equals("new_counter"); });
		assertEquals(gauges.size(), 1);
	}
	
	@Test
	public void shouldNotRegisterGuageWhenCounterExistAlready() {
		metricRegistry.counter("new_counter");
		metricRegistry.counter("new_counter");
		Map<String, Gauge> gauges = metricRegistry.getGauges((name, metric) -> { return name.equals("new_counter"); });
		assertEquals(gauges.size(), 1);
	}
	
	@Test
	public void shouldResetCounterWhenGuageValueIsCalled() {
		Counter counter = metricRegistry.counter("new_counter");
		Map<String, Gauge> gauges = metricRegistry.getGauges((name, metric) -> { return name.equals("new_counter"); });
		Gauge gauge = gauges.get("new_counter");
		counter.inc(10);
		assertEquals(counter.getCount(), 10);
		assertEquals(gauge.getValue(), 10);
		assertEquals(counter.getCount(), 0);
	}
}
