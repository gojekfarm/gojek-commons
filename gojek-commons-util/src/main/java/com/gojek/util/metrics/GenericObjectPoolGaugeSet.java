/**
 *
 */
package com.gojek.util.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class GenericObjectPoolGaugeSet<T> implements MetricSet {
	
	private GenericObjectPool<T> pool;
	
	private String prefix;

	/**
	 * @param pool
	 */
	public GenericObjectPoolGaugeSet(String prefix, GenericObjectPool<T> pool) {
	    this.prefix = Strings.isNullOrEmpty(prefix) ? "": prefix;
		this.pool = pool;
	}
	
	private String metricName(String name) {
	    return prefix + "." + name;
	}

	@Override
	public Map<String, Metric> getMetrics() {
		final Map<String, Metric> gauges = new HashMap<String, Metric>();

        gauges.put(metricName("createdCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getCreatedCount();
            }
        });
        
        gauges.put(metricName("borrowedCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getBorrowedCount();
            }
        });
        
        gauges.put(metricName("returnedCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getReturnedCount();
            }
        });
        
        gauges.put(metricName("destroyedCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getDestroyedCount();
            }
        });
        
        gauges.put(metricName("destroyedByBorrowValidationCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getDestroyedByBorrowValidationCount();
            }
        });
        
        gauges.put(metricName("destroyedByEvictorCountCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getDestroyedByEvictorCount();
            }
        });
        
        gauges.put(metricName("numActive"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return pool.getNumActive();
            }
        });
        
        gauges.put(metricName("numIdle"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return pool.getNumIdle();
            }
        });
        
        gauges.put(metricName("numWaiters"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return pool.getNumWaiters();
            }
        });
        
        gauges.put(metricName("meanActiveTimeMillis"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getMeanActiveTimeMillis();
            }
        });
        
        gauges.put(metricName("meanBorrowWaitTimeMillis"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getMeanBorrowWaitTimeMillis();
            }
        });
        
        gauges.put(metricName("meanIdleTimeMillis"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getMeanIdleTimeMillis();
            }
        });
        
        gauges.put(metricName("maxBorrowWaitTimeMillis"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return pool.getMaxBorrowWaitTimeMillis();
            }
        });

        return Collections.unmodifiableMap(gauges);
	}
}
