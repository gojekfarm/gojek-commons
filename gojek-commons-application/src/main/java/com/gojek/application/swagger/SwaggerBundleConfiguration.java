/**
 *
 */
package com.gojek.application.swagger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Contact;

/**
 * For the meaning of all these properties please refer to Swagger documentation or {@link io.swagger.jaxrs.config.BeanConfig}
 *
 * @author Tristan Burch
 * @author Federico Recio
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwaggerBundleConfiguration {

    /**
     * This is the only property that is required for Swagger to work correctly.
     * <p/>
     * It is a comma separated list of the all the packages that contain the {@link com.wordnik.swagger.annotations.Api}
     * annoted resources
     */
    @JsonProperty
    private String resourcePackage;

    @JsonProperty
    private String title;

    @JsonProperty
    private String version;

    @JsonProperty
    private String description;

    @JsonProperty
    private String termsOfServiceUrl;

    @JsonProperty
    private String contact;
    
    @JsonProperty
    private String contactEmail;
    
    @JsonProperty
    private String contactUrl;
    
    @JsonProperty
    private SwaggerViewConfiguration swaggerViewConfiguration = new SwaggerViewConfiguration();
    
    @JsonProperty
    private Boolean prettyPrint = true;
    
    @JsonProperty
    private String host;
    
    @JsonProperty
    private String[] schemes = new String[] { "http" };

    @JsonProperty
    private String license;

    @JsonProperty
    private String licenseUrl;
    
    @JsonProperty
    private Boolean enabled = true;
    
    @JsonProperty
    private String filterClass;

    /**
     * For most of the scenarios this property is not needed.
     * <p/>
     * This is not a property for Swagger but for bundle to set up Swagger UI correctly.
     * It only needs to be used of the root path or the context path is set programatically
     * and therefore cannot be derived correctly. The problem arises in that if you set the
     * root path or context path in the run() method in your Application subclass the bundle
     * has already been initialized by that time and so does not know you set the path programatically.
     */
    @JsonProperty
    private String uriPrefix;

    /**
     * @return the resourcePackage
     */
    public String getResourcePackage() {
        return resourcePackage;
    }

    /**
     * @param resourcePackage the resourcePackage to set
     */
    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the termsOfServiceUrl
     */
    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    /**
     * @param termsOfServiceUrl the termsOfServiceUrl to set
     */
    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return the contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * @param contactEmail the contactEmail to set
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * @return the contactUrl
     */
    public String getContactUrl() {
        return contactUrl;
    }

    /**
     * @param contactUrl the contactUrl to set
     */
    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    /**
     * @return the swaggerViewConfiguration
     */
    public SwaggerViewConfiguration getSwaggerViewConfiguration() {
        return swaggerViewConfiguration;
    }

    /**
     * @param swaggerViewConfiguration the swaggerViewConfiguration to set
     */
    public void setSwaggerViewConfiguration(SwaggerViewConfiguration swaggerViewConfiguration) {
        this.swaggerViewConfiguration = swaggerViewConfiguration;
    }

    /**
     * @return the prettyPrint
     */
    public Boolean getPrettyPrint() {
        return prettyPrint;
    }

    /**
     * @param prettyPrint the prettyPrint to set
     */
    public void setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the schemes
     */
    public String[] getSchemes() {
        return schemes;
    }

    /**
     * @param schemes the schemes to set
     */
    public void setSchemes(String[] schemes) {
        this.schemes = schemes;
    }

    /**
     * @return the license
     */
    public String getLicense() {
        return license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(String license) {
        this.license = license;
    }

    /**
     * @return the licenseUrl
     */
    public String getLicenseUrl() {
        return licenseUrl;
    }

    /**
     * @param licenseUrl the licenseUrl to set
     */
    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    /**
     * @return the enabled
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the uriPrefix
     */
    public String getUriPrefix() {
        return uriPrefix;
    }

    /**
     * @param uriPrefix the uriPrefix to set
     */
    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }
    
    /**
     * @return the filterClass
     */
    public String getFilterClass() {
        return filterClass;
    }

    /**
     * @param filterClass the filterClass to set
     */
    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    @JsonIgnore
    public BeanConfig build(String urlPattern) {
        if (Strings.isNullOrEmpty(resourcePackage)) {
            throw new IllegalStateException(
                    "Resource package needs to be specified"
                            + " for Swagger to correctly detect annotated resources");
        }

        final BeanConfig config = new BeanConfig();
        config.setTitle(title);
        config.setVersion(version);
        config.setDescription(description);
        config.setContact(contact);
        config.setLicense(license);
        config.setLicenseUrl(licenseUrl);
        config.setTermsOfServiceUrl(termsOfServiceUrl);
        config.setPrettyPrint(prettyPrint);
        config.setBasePath(urlPattern);
        config.setResourcePackage(resourcePackage);
        config.setSchemes(schemes);
        config.setHost(host);
        config.setFilterClass(filterClass);
        config.setScan(true);

        // Assign contact email/url after scan, since BeanConfig.scan will
        // create a new info.Contact instance, thus overriding any info.Contact
        // settings prior to scan.
        if (contactEmail != null || contactUrl != null) {
            if (config.getInfo().getContact() == null) {
                config.getInfo().setContact(new Contact());
            }
            if (contactEmail != null) {
                config.getInfo().getContact().setEmail(contactEmail);
            }
            if (contactUrl != null) {
                config.getInfo().getContact().setUrl(contactUrl);
            }
        }

        return config;
    }

    @Override
    public String toString() {
        return "SwaggerBundleConfiguration [resourcePackage=" + resourcePackage + ", title=" + title + ", version="
                + version + ", description=" + description + ", termsOfServiceUrl=" + termsOfServiceUrl + ", contact="
                + contact + ", contactEmail=" + contactEmail + ", contactUrl=" + contactUrl
                + ", swaggerViewConfiguration=" + swaggerViewConfiguration + ", prettyPrint=" + prettyPrint + ", host="
                + host + ", schemes=" + Arrays.toString(schemes) + ", license=" + license + ", licenseUrl=" + licenseUrl
                + ", enabled=" + enabled + ", filterClass=" + filterClass + ", uriPrefix=" + uriPrefix + "]";
    }
    
}