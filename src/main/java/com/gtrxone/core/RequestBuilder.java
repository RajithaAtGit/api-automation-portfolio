package com.gtrxone.core;

import com.gtrxone.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for constructing API requests.
 * Implements the Builder pattern to provide a fluent interface for request construction.
 */
@Slf4j
public class RequestBuilder {

    private final ConfigManager configManager;
    private final Map<String, String> defaultHeaders;

    /**
     * Constructs a new RequestBuilder with default configuration.
     */
    public RequestBuilder() {
        this.configManager = ConfigManager.getInstance();
        this.defaultHeaders = new HashMap<>();

        // Set default headers
        defaultHeaders.put("Accept", ContentType.JSON.toString());
        defaultHeaders.put("Content-Type", ContentType.JSON.toString());

        // Configure RestAssured defaults
        configureRestAssured();
    }

    /**
     * Configures RestAssured with default settings.
     */
    private void configureRestAssured() {
        boolean enableLogging = configManager.getBooleanProperty("api.logging.enabled", true);

        RestAssuredConfig config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL)
                        .enablePrettyPrinting(true));

        RestAssured.config = config;

        if (enableLogging) {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        }
    }

    /**
     * Builds a request specification for the given endpoint.
     *
     * @param baseUrl The base URL
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The request specification
     */
    public RequestSpecification buildRequest(String baseUrl, String endpoint, Map<String, Object> queryParams, Map<String, String> headers) {
        return buildRequest(null, baseUrl, endpoint, queryParams, headers);
    }

    /**
     * Builds a request specification for the given endpoint, using an existing request specification as a base.
     *
     * @param existingSpec The existing request specification to enhance, or null to create a new one
     * @param baseUrl The base URL
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The enhanced request specification
     */
    public RequestSpecification buildRequest(RequestSpecification existingSpec, String baseUrl, String endpoint, Map<String, Object> queryParams, Map<String, String> headers) {
        RequestSpecBuilder builder = new RequestSpecBuilder();

        // If an existing spec was provided, include it
        if (existingSpec != null) {
            builder.addRequestSpecification(existingSpec);
        }

        // Set base URL and endpoint
        String url = baseUrl;
        if (!url.endsWith("/") && !endpoint.startsWith("/")) {
            url += "/";
        }
        url += endpoint;
        builder.setBaseUri(url);

        // Add default headers
        builder.addHeaders(defaultHeaders);

        // Add custom headers if provided
        if (headers != null && !headers.isEmpty()) {
            builder.addHeaders(headers);
        }

        // Add query parameters if provided
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                builder.addQueryParam(entry.getKey(), entry.getValue());
            }
        }

        // Add filters for logging and reporting
        builder.addFilter(new AllureRestAssured());

        // Build and return the request specification
        RequestSpecification spec = builder.build();

        // Enable logging if configured
        boolean requestLogging = configManager.getBooleanProperty("api.logging.request", true);
        if (requestLogging) {
            spec = spec.log().all();
        }

        return spec;
    }

    /**
     * Builds a request specification with authentication.
     *
     * @param baseUrl The base URL
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @param headers The headers
     * @param authToken The authentication token
     * @return The request specification
     */
    public RequestSpecification buildAuthenticatedRequest(String baseUrl, String endpoint, Map<String, Object> queryParams, 
                                                         Map<String, String> headers, String authToken) {
        Map<String, String> authHeaders = headers != null ? new HashMap<>(headers) : new HashMap<>();
        authHeaders.put("Authorization", "Bearer " + authToken);

        return buildRequest(baseUrl, endpoint, queryParams, authHeaders);
    }

    /**
     * Builds a request specification with basic authentication.
     *
     * @param baseUrl The base URL
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @param headers The headers
     * @param username The username
     * @param password The password
     * @return The request specification
     */
    public RequestSpecification buildBasicAuthRequest(String baseUrl, String endpoint, Map<String, Object> queryParams, 
                                                     Map<String, String> headers, String username, String password) {
        RequestSpecification spec = buildRequest(baseUrl, endpoint, queryParams, headers);
        return spec.auth().basic(username, password);
    }

    /**
     * Sets the content type for the request.
     *
     * @param spec The request specification
     * @param contentType The content type
     * @return The updated request specification
     */
    public RequestSpecification setContentType(RequestSpecification spec, ContentType contentType) {
        return spec.contentType(contentType);
    }

    /**
     * Adds a header to the request.
     *
     * @param spec The request specification
     * @param name The header name
     * @param value The header value
     * @return The updated request specification
     */
    public RequestSpecification addHeader(RequestSpecification spec, String name, String value) {
        return spec.header(name, value);
    }

    /**
     * Adds multiple headers to the request.
     *
     * @param spec The request specification
     * @param headers The headers to add
     * @return The updated request specification
     */
    public RequestSpecification addHeaders(RequestSpecification spec, Map<String, String> headers) {
        return spec.headers(headers);
    }

    /**
     * Adds a query parameter to the request.
     *
     * @param spec The request specification
     * @param name The parameter name
     * @param value The parameter value
     * @return The updated request specification
     */
    public RequestSpecification addQueryParam(RequestSpecification spec, String name, Object value) {
        return spec.queryParam(name, value);
    }

    /**
     * Adds multiple query parameters to the request.
     *
     * @param spec The request specification
     * @param params The parameters to add
     * @return The updated request specification
     */
    public RequestSpecification addQueryParams(RequestSpecification spec, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            spec = spec.queryParam(entry.getKey(), entry.getValue());
        }
        return spec;
    }

    /**
     * Sets the request body.
     *
     * @param spec The request specification
     * @param body The request body
     * @return The updated request specification
     */
    public RequestSpecification setBody(RequestSpecification spec, Object body) {
        return spec.body(body);
    }
}