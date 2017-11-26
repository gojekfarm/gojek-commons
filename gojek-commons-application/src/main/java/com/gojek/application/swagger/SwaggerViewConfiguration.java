/**
 *
 */
package com.gojek.application.swagger;

/**
 * Contains all configurable parameters required to render the SwaggerUI View
 * from the default template
 */
public class SwaggerViewConfiguration {

    private static final String DEFAULT_TITLE = "Swagger UI";
    private static final String DEFAULT_TEMPLATE = "index.ftl";

    private String pageTitle;
    private String templateUrl;
    private String validatorUrl;
    private boolean showApiSelector = true;
    private boolean showAuth = true;

    public SwaggerViewConfiguration() {
        this.pageTitle = DEFAULT_TITLE;
        this.templateUrl = DEFAULT_TEMPLATE;
        this.validatorUrl = null;
        this.showApiSelector = true;
        this.showAuth = true;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String title) {
        this.pageTitle = title;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getValidatorUrl() {
        return validatorUrl;
    }

    public void setValidatorUrl(String validatorUrl) {
        this.validatorUrl = validatorUrl;
    }

    public boolean isShowApiSelector() {
        return showApiSelector;
    }

    public void setShowApiSelector(boolean showApiSelector) {
        this.showApiSelector = showApiSelector;
    }

    public boolean isShowAuth() {
        return showAuth;
    }

    public void setShowAuth(boolean showAuth) {
        this.showAuth = showAuth;
    }
}