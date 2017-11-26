/**
 *
 */
package com.gojek.application.metrics;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
public class PercentileMetric {

	private double min;
	
	private double mean;
	
	private double p75;
	
	private double p90;
	
	private double p95;
	
	private double p98;
	
	private double max;
	
	public static final String MIN = "min";
	
	public static final String MAX = "max";
	
	public static final String MEAN = "mean";
	
	public static final String P75 = "p75";
	
	public static final String P90 = "p90";
	
	public static final String P95 = "p95";
	
	public static final String P98 = "p98";
	
	public static final String[] KEYS = {MIN, MEAN, P75, P90, P95, P98, MAX};
	
	/**
	 * @param min
	 * @param mean
	 * @param p75
	 * @param p90
	 * @param p95
	 * @param p98
	 * @param max
	 */
	public PercentileMetric(double min, double mean, double p75, double p90, double p95, double p98, double max) {
		this.min = min;
		this.mean = mean;
		this.p75 = p75;
		this.p90 = p90;
		this.p95 = p95;
		this.p98 = p98;
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}

	/**
	 * @return the max
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * @return the p75
	 */
	public double getP75() {
		return p75;
	}

	/**
	 * @param p75 the p75 to set
	 */
	public void setP75(double p75) {
		this.p75 = p75;
	}

	/**
	 * @return the p90
	 */
	public double getP90() {
		return p90;
	}

	/**
	 * @param p90 the p90 to set
	 */
	public void setP90(double p90) {
		this.p90 = p90;
	}

	/**
	 * @return the p95
	 */
	public double getP95() {
		return p95;
	}

	/**
	 * @param p95 the p95 to set
	 */
	public void setP95(double p95) {
		this.p95 = p95;
	}

	/**
	 * @return the p98
	 */
	public double getP98() {
		return p98;
	}

	/**
	 * @param p98 the p98 to set
	 */
	public void setP98(double p98) {
		this.p98 = p98;
	}
	
	/**
	 * @return
	 */
	public Map<String, Double> toMap() {
		Map<String, Double> map = Maps.newHashMap();
		map.put(MIN, min);
		map.put(MEAN, mean);
		map.put(P75, p75);
		map.put(P90, p90);
		map.put(P95, p95);
		map.put(P98, p98);
		map.put(MAX, max);
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(mean);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(max);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(min);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(p75);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(p90);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(p95);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(p98);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PercentileMetric other = (PercentileMetric) obj;
		if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean))
			return false;
		if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
			return false;
		if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
			return false;
		if (Double.doubleToLongBits(p75) != Double.doubleToLongBits(other.p75))
			return false;
		if (Double.doubleToLongBits(p90) != Double.doubleToLongBits(other.p90))
			return false;
		if (Double.doubleToLongBits(p95) != Double.doubleToLongBits(other.p95))
			return false;
		if (Double.doubleToLongBits(p98) != Double.doubleToLongBits(other.p98))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PercentileMetric [min=" + min + ", mean=" + mean + ", p75=" + p75 + ", p90=" + p90 + ", p95=" + p95
				+ ", p98=" + p98 + ", max=" + max + "]";
	}
}
