/**
 *
 */
package com.gojek.application.swagger;

/**
 * @author Federico Recio
 */
public class Constants {
    /**
     * The project's directory in which Swagger static assets live
     */
    public static final String SWAGGER_RESOURCES_PATH = "/swagger-static";
    /**
     * The path with which all HTTP requests for Swagger assets should be prefixed.
     */
    public static final String SWAGGER_URI_PATH = SWAGGER_RESOURCES_PATH;
    /**
     * The name of the {@link io.dropwizard.assets.AssetsBundle} to register.
     */
    public static final String SWAGGER_ASSETS_NAME = "swagger-assets";
    /**
     * Default host name will be used if the host cannot be determined by other means.
     */
    public static final String DEFAULT_SWAGGER_HOST = "localhost";
    /**
     * The URL to use to determine this host's name when running in AWS.
     */
    public static final String AWS_HOST_NAME_URL = "http://169.254.169.254/latest/meta-data/public-hostname/";
    /**
     * The file to check for its existence to determine if the server is running on AWS.
     */
    public static final String AWS_FILE_TO_CHECK = "/var/lib/cloud/";
    /**
     * The path to which Swagger resources are bound to
     */
    public static final String SWAGGER_PATH = "/swagger";
}
