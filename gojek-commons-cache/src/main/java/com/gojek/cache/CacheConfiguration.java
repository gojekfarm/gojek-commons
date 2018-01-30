package com.gojek.cache;

/**
 *
 */
public class CacheConfiguration {

    private String host;

    private int port;

    private Integer timeout;

    private String password;

    private Integer maxConnections;

    private Integer maxIdleConnections;

    private Integer minConnections;

    private Long commandTimeoutInMs;
    /**
     * Default constructor
     */
    public CacheConfiguration() {
	}

    /**
	 * @param host
	 * @param port
	 * @param timeout
	 * @param maxConnections
	 * @param maxIdleConnections
	 * @param minConnections
	 */
	public CacheConfiguration(String host, int port, Integer timeout, Integer maxConnections,
			Integer maxIdleConnections, Integer minConnections, Long commandTimeoutInMs) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.maxConnections = maxConnections;
		this.maxIdleConnections = maxIdleConnections;
		this.minConnections = minConnections;
		this.commandTimeoutInMs = commandTimeoutInMs;
	}

	/**
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the maxConnections
     */
    public Integer getMaxConnections() {
        return maxConnections;
    }

    /**
     * @param maxConnections the maxConnections to set
     */
    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * @return the maxIdleConnections
     */
    public Integer getMaxIdleConnections() {
        return maxIdleConnections;
    }

    /**
     * @param maxIdleConnections the maxIdleConnections to set
     */
    public void setMaxIdleConnections(Integer maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    /**
     * @return the minConnections
     */
    public Integer getMinConnections() {
        return minConnections;
    }

    /**
     * @param minConnections the minConnections to set
     */
    public void setMinConnections(Integer minConnections) {
        this.minConnections = minConnections;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCommandTimeoutInMs() {
        return commandTimeoutInMs;
    }

    /**
     * @param commandTimeoutInMs
     */
    public void setCommandTimeoutInMs(Long commandTimeoutInMs) {
        this.commandTimeoutInMs = commandTimeoutInMs;
    }
}
