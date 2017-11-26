/**
 *
 */
package com.gojek.jpa.metrics;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * @author ganeshs
 *
 */
public class C3p0PoolGaugeSetTest {
	
	private PooledDataSource dataSource;
	
	private C3p0PoolGaugeSet gaugeSet;
	
	@BeforeMethod
	public void setup() throws Exception {
		dataSource = mock(PooledDataSource.class);
		when(dataSource.getNumFailedCheckinsDefaultUser()).thenReturn(1L);
		when(dataSource.getNumFailedCheckoutsDefaultUser()).thenReturn(2L);
		when(dataSource.getNumFailedIdleTestsDefaultUser()).thenReturn(3L);
		when(dataSource.getUpTimeMillisDefaultUser()).thenReturn(4L);
		when(dataSource.getNumBusyConnectionsDefaultUser()).thenReturn(5);
		when(dataSource.getNumConnectionsDefaultUser()).thenReturn(6);
		when(dataSource.getNumIdleConnectionsDefaultUser()).thenReturn(7);
		when(dataSource.getNumThreadsAwaitingCheckoutDefaultUser()).thenReturn(8);
		when(dataSource.getNumUnclosedOrphanedConnectionsDefaultUser()).thenReturn(9);
		gaugeSet = new C3p0PoolGaugeSet(dataSource);
	}

	@Test
	public void shouldReturnMetricsForC3p0Pool() {
		Map<String, Metric> metrics = gaugeSet.getMetrics();
		assertEquals(((Gauge<Long>) metrics.get("c3p0.numFailedCheckins")).getValue(), Long.valueOf(1));
		assertEquals(((Gauge<Long>) metrics.get("c3p0.numFailedCheckouts")).getValue(), Long.valueOf(2));
		assertEquals(((Gauge<Long>) metrics.get("c3p0.numFailedIdleTests")).getValue(), Long.valueOf(3));
		assertEquals(((Gauge<Long>) metrics.get("c3p0.upTimeMillis")).getValue(), Long.valueOf(4));
		assertEquals(((Gauge<Integer>) metrics.get("c3p0.numBusyConnections")).getValue(), Integer.valueOf(5));
		assertEquals(((Gauge<Integer>) metrics.get("c3p0.numConnections")).getValue(), Integer.valueOf(6));
		assertEquals(((Gauge<Integer>) metrics.get("c3p0.numIdleConnections")).getValue(), Integer.valueOf(7));
		assertEquals(((Gauge<Integer>) metrics.get("c3p0.numThreadsAwaitingCheckout")).getValue(), Integer.valueOf(8));
		assertEquals(((Gauge<Integer>) metrics.get("c3p0.numUnclosedOrphanedConnections")).getValue(), Integer.valueOf(9));
	}
}
