/**
 *
 */
package com.gojek.core.event;

/**
 * @author ganeshs
 *
 */
public class Destination {

	private String exchange;
	
	private String routingKey;
	
	/**
	 * Default constructor
	 */
	public Destination() {
	}
	
	/**
	 * @param exchange
	 */
	public Destination(String exchange) {
        this(exchange, null);
    }

	/**
	 * @param exchange
	 * @param routingKey
	 */
	public Destination(String exchange, String routingKey) {
		this.exchange = exchange;
		this.routingKey = routingKey;
	}

	/**
	 * @return the exchange
	 */
	public String getExchange() {
		return exchange;
	}

	/**
	 * @return the routingKey
	 */
	public String getRoutingKey() {
		return routingKey;
	}

	/**
	 * @param exchange the exchange to set
	 */
	void setExchange(String exchange) {
		this.exchange = exchange;
	}

	/**
	 * @param routingKey the routingKey to set
	 */
	void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
		result = prime * result + ((routingKey == null) ? 0 : routingKey.hashCode());
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
		Destination other = (Destination) obj;
		if (exchange == null) {
			if (other.exchange != null)
				return false;
		} else if (!exchange.equals(other.exchange))
			return false;
		if (routingKey == null) {
			if (other.routingKey != null)
				return false;
		} else if (!routingKey.equals(other.routingKey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Destination [exchange=" + exchange + ", routingKey=" + routingKey + "]";
	}
}
