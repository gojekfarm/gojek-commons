/**
 *
 */
package com.gojek.application.swagger;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import io.dropwizard.views.View;

/**
 * Serves the content of Swagger's index page which has been "templatized" to
 * support replacing the directory in which Swagger's static content is located
 * (i.e. JS files) and the path with which requests to resources need to be
 * prefixed.
 */
public class SwaggerView extends View {

    private final String swaggerAssetsPath;
    private final String contextPath;

    private final SwaggerViewConfiguration viewConfiguration;

    public SwaggerView(@Nonnull final String urlPattern,
            @Nonnull SwaggerViewConfiguration config) {
        super(config.getTemplateUrl(), StandardCharsets.UTF_8);

        if ("/".equals(urlPattern)) {
            swaggerAssetsPath = Constants.SWAGGER_URI_PATH;
        } else {
            swaggerAssetsPath = urlPattern + Constants.SWAGGER_URI_PATH;
        }

        if ("/".equals(urlPattern)) {
            contextPath = "";
        } else {
            contextPath = urlPattern;
        }

        this.viewConfiguration = config;
    }

    /**
     * Returns the title for the browser header
     */
    public String getTitle() {
        return viewConfiguration.getPageTitle();
    }

    /**
     * Returns the path with which all requests for Swagger's static content
     * need to be prefixed
     */
    public String getSwaggerAssetsPath() {
        return swaggerAssetsPath;
    }

    /**
     * Returns the path with with which all requests made by Swagger's UI to
     * Resources need to be prefixed
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Returns the location of the validator URL or null to disable
     */
    public String getValidatorUrl() {
        return viewConfiguration.getValidatorUrl();
    }

    /**
     * Returns whether to display the authorization input boxes
     */
    public boolean getShowAuth() {
        return viewConfiguration.isShowAuth();
    }

    /**
     * Returns whether to display the swagger spec selector
     */
    public boolean getShowApiSelector() {
        return viewConfiguration.isShowApiSelector();
    }
}