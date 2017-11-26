/**
 *
 */
package com.gojek.application.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Timer;

/**
 * @author ganeshs
 *
 */
public class EnhancedMetricRegistry extends MetricRegistry {
	
	private MetricRegistry metricRegistry;
	
	private Map<String, Counter> counters = new HashMap<>();
	
	/**
	 * @param metricRegistry
	 */
	public EnhancedMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}

	/**
	 * Overrides the counter method and converts it to a guage
	 */
	@Override
	public Counter counter(String name) {
		Counter counter = counters.get(name);
		if (counter == null) {
			counter = new Counter();
			counters.put(name, counter);
			registerGuage(name, counter);
		}
		return counter;
	}
	
	/**
	 * @param name
	 * @param counter
	 */
	protected void registerGuage(String name, Counter counter) {
		if (getNames().contains(name)) {
			return;
		}
		Gauge<Integer> gauge = () -> { 
			Long count = counter.getCount();
			counter.dec(count);
			return count.intValue();
		};
		register(name, gauge);
	}

	public int hashCode() {
		return metricRegistry.hashCode();
	}

	public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
		return metricRegistry.register(name, metric);
	}

	public void registerAll(MetricSet metrics) throws IllegalArgumentException {
		metricRegistry.registerAll(metrics);
	}

	public boolean equals(Object obj) {
		return metricRegistry.equals(obj);
	}

	public Histogram histogram(String name) {
		return metricRegistry.histogram(name);
	}

	public Meter meter(String name) {
		return metricRegistry.meter(name);
	}

	public Timer timer(String name) {
		return metricRegistry.timer(name);
	}

	public boolean remove(String name) {
		return metricRegistry.remove(name);
	}

	public void removeMatching(MetricFilter filter) {
		metricRegistry.removeMatching(filter);
	}

	public void addListener(MetricRegistryListener listener) {
		metricRegistry.addListener(listener);
	}

	public void removeListener(MetricRegistryListener listener) {
		metricRegistry.removeListener(listener);
	}

	public SortedSet<String> getNames() {
		return metricRegistry.getNames();
	}

	public SortedMap<String, Gauge> getGauges() {
		return metricRegistry.getGauges();
	}

	public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
		return metricRegistry.getGauges(filter);
	}

	public SortedMap<String, Counter> getCounters() {
		return metricRegistry.getCounters();
	}

	public SortedMap<String, Counter> getCounters(MetricFilter filter) {
		return metricRegistry.getCounters(filter);
	}

	public SortedMap<String, Histogram> getHistograms() {
		return metricRegistry.getHistograms();
	}

	public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
		return metricRegistry.getHistograms(filter);
	}

	public SortedMap<String, Meter> getMeters() {
		return metricRegistry.getMeters();
	}

	public SortedMap<String, Meter> getMeters(MetricFilter filter) {
		return metricRegistry.getMeters(filter);
	}

	public String toString() {
		return metricRegistry.toString();
	}

	public SortedMap<String, Timer> getTimers() {
		return metricRegistry.getTimers();
	}

	public SortedMap<String, Timer> getTimers(MetricFilter filter) {
		return metricRegistry.getTimers(filter);
	}

	public Map<String, Metric> getMetrics() {
		return metricRegistry.getMetrics();
	}
	
}
