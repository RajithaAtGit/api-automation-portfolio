package com.gtrxone.auth;

import com.gtrxone.config.ConfigManager;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthenticationStrategy for API Key Authentication.
 */
@Slf4j
public class ApiKeyStrategy implements AuthenticationStrategy {

    private final String apiKey;
    private final String apiKeyName;
    private final ApiKeyLocation location;
    private final ConfigManager configManager;

    /**
     * Enum representing the location of the API key.
     */
    public enum ApiKeyLocation {
        HEADER,
        QUERY_PARAM
    }

    /**
     * Constructs a new ApiKeyStrategy with the specified API key.
     *
     * @param apiKey The API key
     * @param apiKeyName The name of the API key parameter
     * @param location The location of the API key (header or query parameter)
     */
    public ApiKeyStrategy(String apiKey, String apiKeyName, ApiKeyLocation location) {
        this.apiKey = apiKey;
        this.apiKeyName = apiKeyName;
        this.location = location;
        this.configManager = ConfigManager.getInstance();
        
        log.debug("Created ApiKeyStrategy with key name: {}, location: {}", apiKeyName, location);
    }

    /**
     * Constructs a new ApiKeyStrategy with the API key from configuration.
     */
    public ApiKeyStrategy() {
        this.configManager = ConfigManager.getInstance();
        this.apiKey = configManager.getProperty("auth.apiKey.value");
        this.apiKeyName = configManager.getProperty("auth.apiKey.name", "api_key");
        
        String locationStr = configManager.getProperty("auth.apiKey.location", "HEADER");
        this.location = ApiKeyLocation.valueOf(locationStr.toUpperCase());
        
        log.debug("Created ApiKeyStrategy from configuration with key name: {}, location: {}", apiKeyName, location);
    }

    @Override
    public RequestSpecification authenticate(RequestSpecification requestSpec) {
        log.debug("Applying API Key Authentication with key name: {}, location: {}", apiKeyName, location);
        
        if (location == ApiKeyLocation.HEADER) {
            return requestSpec.header(apiKeyName, apiKey);
        } else {
            return requestSpec.queryParam(apiKeyName, apiKey);
        }
    }

    @Override
    public String getType() {
        return "ApiKey";
    }

    @Override
    public boolean isValid() {
        // API key is valid if it's not null or empty
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public boolean refresh() {
        // API keys don't need refreshing
        return true;
    }

    /**
     * Gets the API key.
     *
     * @return The API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Gets the API key name.
     *
     * @return The API key name
     */
    public String getApiKeyName() {
        return apiKeyName;
    }

    /**
     * Gets the API key location.
     *
     * @return The API key location
     */
    public ApiKeyLocation getLocation() {
        return location;
    }
}