/**
 *
 */
package com.gojek.application.swagger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Federico Recio
 */
@Path(Constants.SWAGGER_PATH)
@Produces(MediaType.TEXT_HTML)
public class SwaggerResource {
    
    private final String urlPattern;
    
    private final SwaggerViewConfiguration config;

    public SwaggerResource(String urlPattern, SwaggerViewConfiguration config) {
        this.urlPattern = urlPattern;
        this.config = config;
    }

    @GET
    public SwaggerView get() {
        return new SwaggerView(urlPattern, config);
    }
}