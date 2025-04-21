package com.gtrxone.core;

import com.gtrxone.auth.AuthenticationManager;
import com.gtrxone.config.ConfigManager;
import com.gtrxone.exceptions.ApiException;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

/**
 * Base class for all API service classes.
 * Implements the Facade pattern to provide a simplified interface for API interactions.
 */
@Slf4j
public abstract class BaseService {

    protected final ConfigManager configManager;
    protected final String baseUrl;
    protected final RequestBuilder requestBuilder;
    protected final ResponseHandler responseHandler;
    protected final AuthenticationManager authManager;
    protected String authStrategy;

    /**
     * Constructs a new BaseService with the specified configuration.
     */
    public BaseService() {
        this.configManager = ConfigManager.getInstance();
        this.baseUrl = configManager.getProperty("api.baseUrl");
        this.requestBuilder = new RequestBuilder();
        this.responseHandler = new ResponseHandler();
        this.authManager = AuthenticationManager.getInstance();
        this.authStrategy = null; // Use default strategy

        // Initialize authentication strategies
        authManager.initializeDefaultStrategies();

        log.info("Initialized service with base URL: {}", baseUrl);
    }

    /**
     * Constructs a new BaseService with a specific authentication strategy.
     * 
     * @param authStrategy The name of the authentication strategy to use
     */
    public BaseService(String authStrategy) {
        this();
        this.authStrategy = authStrategy;
        log.info("Using authentication strategy: {}", authStrategy);
    }

    /**
     * Performs a GET request.
     *
     * @param endpoint The API endpoint
     * @return The response
     */
    @Step("GET {endpoint}")
    public Response get(String endpoint) {
        return get(endpoint, null, null);
    }

    /**
     * Performs a GET request with query parameters.
     *
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @return The response
     */
    @Step("GET {endpoint} with query parameters")
    public Response get(String endpoint, Map<String, Object> queryParams) {
        return get(endpoint, queryParams, null);
    }

    /**
     * Performs a GET request with query parameters and headers.
     *
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The response
     */
    @Step("GET {endpoint} with query parameters and headers")
    public Response get(String endpoint, Map<String, Object> queryParams, Map<String, String> headers) {
        return executeRequest(requestSpec -> {
            RequestSpecification spec = requestBuilder.buildRequest(requestSpec, baseUrl, endpoint, queryParams, headers);
            return spec.get();
        });
    }

    /**
     * Performs a POST request.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The response
     */
    @Step("POST {endpoint}")
    public Response post(String endpoint, Object body) {
        return post(endpoint, body, null, null);
    }

    /**
     * Performs a POST request with query parameters.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @param queryParams The query parameters
     * @return The response
     */
    @Step("POST {endpoint} with query parameters")
    public Response post(String endpoint, Object body, Map<String, Object> queryParams) {
        return post(endpoint, body, queryParams, null);
    }

    /**
     * Performs a POST request with query parameters and headers.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The response
     */
    @Step("POST {endpoint} with query parameters and headers")
    public Response post(String endpoint, Object body, Map<String, Object> queryParams, Map<String, String> headers) {
        return executeRequest(requestSpec -> {
            RequestSpecification spec = requestBuilder.buildRequest(requestSpec, baseUrl, endpoint, queryParams, headers);
            if (body != null) {
                spec.body(body);
            }
            return spec.post();
        });
    }

    /**
     * Performs a PUT request.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The response
     */
    @Step("PUT {endpoint}")
    public Response put(String endpoint, Object body) {
        return put(endpoint, body, null, null);
    }

    /**
     * Performs a PUT request with query parameters and headers.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The response
     */
    @Step("PUT {endpoint} with query parameters and headers")
    public Response put(String endpoint, Object body, Map<String, Object> queryParams, Map<String, String> headers) {
        return executeRequest(requestSpec -> {
            RequestSpecification spec = requestBuilder.buildRequest(requestSpec, baseUrl, endpoint, queryParams, headers);
            if (body != null) {
                spec.body(body);
            }
            return spec.put();
        });
    }

    /**
     * Performs a DELETE request.
     *
     * @param endpoint The API endpoint
     * @return The response
     */
    @Step("DELETE {endpoint}")
    public Response delete(String endpoint) {
        return delete(endpoint, null, null);
    }

    /**
     * Performs a DELETE request with query parameters and headers.
     *
     * @param endpoint The API endpoint
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The response
     */
    @Step("DELETE {endpoint} with query parameters and headers")
    public Response delete(String endpoint, Map<String, Object> queryParams, Map<String, String> headers) {
        return executeRequest(requestSpec -> {
            RequestSpecification spec = requestBuilder.buildRequest(requestSpec, baseUrl, endpoint, queryParams, headers);
            return spec.delete();
        });
    }

    /**
     * Performs a PATCH request.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @return The response
     */
    @Step("PATCH {endpoint}")
    public Response patch(String endpoint, Object body) {
        return patch(endpoint, body, null, null);
    }

    /**
     * Performs a PATCH request with query parameters and headers.
     *
     * @param endpoint The API endpoint
     * @param body The request body
     * @param queryParams The query parameters
     * @param headers The headers
     * @return The response
     */
    @Step("PATCH {endpoint} with query parameters and headers")
    public Response patch(String endpoint, Object body, Map<String, Object> queryParams, Map<String, String> headers) {
        return executeRequest(requestSpec -> {
            RequestSpecification spec = requestBuilder.buildRequest(requestSpec, baseUrl, endpoint, queryParams, headers);
            if (body != null) {
                spec.body(body);
            }
            return spec.patch();
        });
    }

    /**
     * Executes a request with retry logic.
     *
     * @param requestFunction The function to execute the request
     * @return The response
     */
    protected Response executeRequest(Function<RequestSpecification, Response> requestFunction) {
        int maxRetries = configManager.getIntProperty("api.maxRetries", 3);
        long retryDelayMs = configManager.getLongProperty("api.retryDelayMs", 1000);

        Response response = null;
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount <= maxRetries) {
            try {
                if (retryCount > 0) {
                    log.info("Retrying request (attempt {}/{})", retryCount, maxRetries);
                    Thread.sleep(retryDelayMs);
                }

                // Create a base request specification
                RequestSpecification requestSpec = io.restassured.RestAssured.given();

                // Apply authentication if configured
                if (authStrategy != null) {
                    // Use specified authentication strategy
                    if (authManager.hasStrategy(authStrategy)) {
                        log.debug("Authenticating request with strategy: {}", authStrategy);
                        requestSpec = authManager.authenticate(requestSpec, authStrategy);
                    } else {
                        log.warn("Authentication strategy not found: {}", authStrategy);
                    }
                } else if (authManager.getDefaultStrategy() != null) {
                    // Use default authentication strategy
                    log.debug("Authenticating request with default strategy");
                    requestSpec = authManager.authenticate(requestSpec);
                }

                response = requestFunction.apply(requestSpec);
                success = true;

            } catch (Exception e) {
                log.error("Request failed with exception: {}", e.getMessage());
                if (retryCount >= maxRetries) {
                    throw new ApiException("Request failed after " + maxRetries + " retries", e);
                }
                retryCount++;
            }
        }

        return responseHandler.validateResponse(response);
    }

    /**
     * Sets the authentication strategy to use for requests.
     *
     * @param strategyName The name of the authentication strategy
     */
    public void setAuthenticationStrategy(String strategyName) {
        this.authStrategy = strategyName;
        log.info("Set authentication strategy to: {}", strategyName);
    }

    /**
     * Clears the authentication strategy, reverting to the default strategy.
     */
    public void clearAuthenticationStrategy() {
        this.authStrategy = null;
        log.info("Cleared authentication strategy, will use default strategy");
    }
}