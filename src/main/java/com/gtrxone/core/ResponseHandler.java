package com.gtrxone.core;

import com.gtrxone.config.ConfigManager;
import com.gtrxone.exceptions.ApiException;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Handler class for processing and validating API responses.
 * Implements the Chain of Responsibility pattern for sequential response processing.
 */
@Slf4j
public class ResponseHandler {
    
    private final ConfigManager configManager;
    private final List<Integer> successStatusCodes;
    private final boolean validateStatus;
    private final boolean logResponse;
    
    /**
     * Constructs a new ResponseHandler with default configuration.
     */
    public ResponseHandler() {
        this.configManager = ConfigManager.getInstance();
        this.successStatusCodes = Arrays.asList(200, 201, 202, 204);
        this.validateStatus = configManager.getBooleanProperty("api.validateStatus", true);
        this.logResponse = configManager.getBooleanProperty("api.logging.response", true);
    }
    
    /**
     * Validates the response and returns it.
     *
     * @param response The response to validate
     * @return The validated response
     * @throws ApiException if the response is invalid
     */
    @Step("Validate response")
    public Response validateResponse(Response response) {
        if (response == null) {
            throw new ApiException("Response is null");
        }
        
        if (logResponse) {
            logResponse(response);
        }
        
        if (validateStatus) {
            validateStatusCode(response);
        }
        
        return response;
    }
    
    /**
     * Validates the response status code.
     *
     * @param response The response to validate
     * @throws ApiException if the status code is not a success code
     */
    @Step("Validate status code")
    private void validateStatusCode(Response response) {
        int statusCode = response.getStatusCode();
        
        if (!successStatusCodes.contains(statusCode)) {
            String responseBody = response.getBody().asString();
            String errorMessage = String.format("Request failed with status code %d. Response: %s", 
                    statusCode, responseBody);
            
            log.error(errorMessage);
            throw new ApiException(statusCode, errorMessage);
        }
    }
    
    /**
     * Logs the response details.
     *
     * @param response The response to log
     */
    private void logResponse(Response response) {
        log.info("Response Status Code: {}", response.getStatusCode());
        log.info("Response Status Line: {}", response.getStatusLine());
        log.info("Response Headers: {}", response.getHeaders());
        
        String responseBody = response.getBody().asString();
        if (responseBody != null && !responseBody.isEmpty()) {
            log.info("Response Body: {}", responseBody);
        }
    }
    
    /**
     * Extracts a value from the response using a JSON path.
     *
     * @param response The response to extract from
     * @param jsonPath The JSON path to extract
     * @param <T> The type of the extracted value
     * @return The extracted value
     */
    @Step("Extract value using JSON path: {jsonPath}")
    public <T> T extractValue(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }
    
    /**
     * Extracts a map from the response.
     *
     * @param response The response to extract from
     * @return The extracted map
     */
    @Step("Extract response as map")
    public Map<String, Object> extractAsMap(Response response) {
        return response.jsonPath().getMap("$");
    }
    
    /**
     * Extracts a list from the response.
     *
     * @param response The response to extract from
     * @param jsonPath The JSON path to extract
     * @param <T> The type of the list elements
     * @return The extracted list
     */
    @Step("Extract response as list")
    public <T> List<T> extractAsList(Response response, String jsonPath) {
        return response.jsonPath().getList(jsonPath);
    }
    
    /**
     * Extracts an object from the response and converts it to the specified class.
     *
     * @param response The response to extract from
     * @param jsonPath The JSON path to extract
     * @param clazz The class to convert to
     * @param <T> The type of the extracted object
     * @return The extracted object
     */
    @Step("Extract response as object")
    public <T> T extractAsObject(Response response, String jsonPath, Class<T> clazz) {
        return response.jsonPath().getObject(jsonPath, clazz);
    }
    
    /**
     * Validates the response using a custom validator function.
     *
     * @param response The response to validate
     * @param validator The validator function
     * @return The validated response
     * @throws ApiException if the validation fails
     */
    @Step("Validate response with custom validator")
    public Response validateWith(Response response, Function<Response, Boolean> validator) {
        if (!validator.apply(response)) {
            throw new ApiException("Response validation failed");
        }
        return response;
    }
    
    /**
     * Validates that the response contains the specified field.
     *
     * @param response The response to validate
     * @param jsonPath The JSON path to check
     * @return The validated response
     * @throws ApiException if the validation fails
     */
    @Step("Validate response contains field: {jsonPath}")
    public Response validateContainsField(Response response, String jsonPath) {
        if (response.jsonPath().get(jsonPath) == null) {
            throw new ApiException("Response does not contain field: " + jsonPath);
        }
        return response;
    }
    
    /**
     * Validates that the response field equals the expected value.
     *
     * @param response The response to validate
     * @param jsonPath The JSON path to check
     * @param expectedValue The expected value
     * @return The validated response
     * @throws ApiException if the validation fails
     */
    @Step("Validate response field equals expected value")
    public Response validateFieldEquals(Response response, String jsonPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(jsonPath);
        
        if (actualValue == null || !actualValue.equals(expectedValue)) {
            throw new ApiException(String.format("Field %s does not equal expected value. Expected: %s, Actual: %s", 
                    jsonPath, expectedValue, actualValue));
        }
        
        return response;
    }
}