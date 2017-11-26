/**
 *
 */
package com.gojek.jpa.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * @author ganeshs
 *
 */
public class C3p0PoolGaugeSet implements MetricSet {
	
	private PooledDataSource dataSource;
	
	/**
	 * Default constructor
	 */
	public C3p0PoolGaugeSet() {
	}

	/**
	 * @param dataSource
	 */
	public C3p0PoolGaugeSet(PooledDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Map<String, Metric> getMetrics() {
		final Map<String, Metric> gauges = new HashMap<String, Metric>();
		
		gauges.put("c3p0.numFailedCheckins", new Gauge<Long>() {
            @Override
            public Long getValue() {
                try {
					return dataSource.getNumFailedCheckinsDefaultUser();
				} catch (Exception e) {
					return -1L;
				}
            }
        });
		
		gauges.put("c3p0.numFailedCheckouts", new Gauge<Long>() {
            @Override
            public Long getValue() {
                try {
					return dataSource.getNumFailedCheckoutsDefaultUser();
				} catch (Exception e) {
					return -1L;
				}
            }
        });
		
		gauges.put("c3p0.numFailedIdleTests", new Gauge<Long>() {
            @Override
            public Long getValue() {
                try {
					return dataSource.getNumFailedIdleTestsDefaultUser();
				} catch (Exception e) {
					return -1L;
				}
            }
        });
		
		gauges.put("c3p0.upTimeMillis", new Gauge<Long>() {
            @Override
            public Long getValue() {
                try {
					return dataSource.getUpTimeMillisDefaultUser();
				} catch (Exception e) {
					return -1L;
				}
            }
        });
		
		gauges.put("c3p0.numBusyConnections", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                try {
					return dataSource.getNumBusyConnectionsDefaultUser();
				} catch (Exception e) {
					return -1;
				}
            }
        });
		
		gauges.put("c3p0.numConnections", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                try {
					return dataSource.getNumConnectionsDefaultUser();
				} catch (Exception e) {
					return -1;
				}
            }
        });
		
		gauges.put("c3p0.numIdleConnections", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                try {
					return dataSource.getNumIdleConnectionsDefaultUser();
				} catch (Exception e) {
					return -1;
				}
            }
        });
		
		gauges.put("c3p0.numThreadsAwaitingCheckout", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                try {
					return dataSource.getNumThreadsAwaitingCheckoutDefaultUser();
				} catch (Exception e) {
					return -1;
				}
            }
        });
		
		gauges.put("c3p0.numUnclosedOrphanedConnections", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                try {
					return dataSource.getNumUnclosedOrphanedConnectionsDefaultUser();
				} catch (Exception e) {
					return -1;
				}
            }
        });
		
        return Collections.unmodifiableMap(gauges);
	}

	/**
	 * @return the dataSource
	 */
	public PooledDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(PooledDataSource dataSource) {
		this.dataSource = dataSource;
	}

}
