/**
 *
 */
package com.gojek.application.metrics;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class PercentileMetricTest {

	@Test
	public void shouldConstructPercentileMetric() {
		PercentileMetric metric = new PercentileMetric(100d, 150d, 160d, 170d, 180d, 190d, 200d);
		assertEquals(metric.getMin(), 100d);
		assertEquals(metric.getMean(), 150d);
		assertEquals(metric.getP75(), 160d);
		assertEquals(metric.getP90(), 170d);
		assertEquals(metric.getP95(), 180d);
		assertEquals(metric.getP98(), 190d);
		assertEquals(metric.getMax(), 200d);
	}
	
	public void shouldReturnAMap() {
		PercentileMetric metric = new PercentileMetric(100d, 150d, 160d, 170d, 180d, 190d, 200d);
		Map<String, Double> map = metric.toMap();
		assertEquals(map.get(PercentileMetric.MIN), 100d);
		assertEquals(map.get(PercentileMetric.MEAN), 150d);
		assertEquals(map.get(PercentileMetric.P75), 160d);
		assertEquals(map.get(PercentileMetric.P90), 170d);
		assertEquals(map.get(PercentileMetric.P95), 180d);
		assertEquals(map.get(PercentileMetric.P98), 190d);
		assertEquals(map.get(PercentileMetric.MAX), 200d);
	}
}
