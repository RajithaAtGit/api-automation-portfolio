package com.gtrxone.auth;

import com.gtrxone.config.ConfigManager;
import com.gtrxone.exceptions.ApiException;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for authentication strategies.
 * Provides a unified interface for authentication.
 */
@Slf4j
public class AuthenticationManager {

    private static final AuthenticationManager instance = new AuthenticationManager();
    private final ConfigManager configManager;
    private final Map<String, AuthenticationStrategy> strategies;
    private String defaultStrategyName;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private AuthenticationManager() {
        this.configManager = ConfigManager.getInstance();
        this.strategies = new HashMap<>();
        this.defaultStrategyName = configManager.getProperty("auth.defaultStrategy", "none");
        
        log.info("Initialized AuthenticationManager with default strategy: {}", defaultStrategyName);
    }

    /**
     * Gets the singleton instance of AuthenticationManager.
     *
     * @return The singleton instance of AuthenticationManager
     */
    public static AuthenticationManager getInstance() {
        return instance;
    }

    /**
     * Registers an authentication strategy.
     *
     * @param name The name of the strategy
     * @param strategy The authentication strategy
     */
    public void registerStrategy(String name, AuthenticationStrategy strategy) {
        strategies.put(name, strategy);
        log.info("Registered authentication strategy: {}", name);
    }

    /**
     * Gets an authentication strategy by name.
     *
     * @param name The name of the strategy
     * @return The authentication strategy
     * @throws ApiException if the strategy is not found
     */
    public AuthenticationStrategy getStrategy(String name) {
        if (!strategies.containsKey(name)) {
            throw new ApiException("Authentication strategy not found: " + name);
        }
        return strategies.get(name);
    }

    /**
     * Gets the default authentication strategy.
     *
     * @return The default authentication strategy
     * @throws ApiException if the default strategy is not found
     */
    public AuthenticationStrategy getDefaultStrategy() {
        if ("none".equals(defaultStrategyName)) {
            log.warn("No default authentication strategy configured");
            return null;
        }
        
        return getStrategy(defaultStrategyName);
    }

    /**
     * Sets the default authentication strategy.
     *
     * @param name The name of the strategy
     * @throws ApiException if the strategy is not found
     */
    public void setDefaultStrategy(String name) {
        if (!strategies.containsKey(name)) {
            throw new ApiException("Authentication strategy not found: " + name);
        }
        
        defaultStrategyName = name;
        log.info("Set default authentication strategy to: {}", name);
    }

    /**
     * Authenticates a request using the specified strategy.
     *
     * @param requestSpec The request specification
     * @param strategyName The name of the strategy
     * @return The authenticated request specification
     * @throws ApiException if the strategy is not found
     */
    public RequestSpecification authenticate(RequestSpecification requestSpec, String strategyName) {
        AuthenticationStrategy strategy = getStrategy(strategyName);
        return strategy.authenticate(requestSpec);
    }

    /**
     * Authenticates a request using the default strategy.
     *
     * @param requestSpec The request specification
     * @return The authenticated request specification
     * @throws ApiException if the default strategy is not found
     */
    public RequestSpecification authenticate(RequestSpecification requestSpec) {
        AuthenticationStrategy strategy = getDefaultStrategy();
        
        if (strategy == null) {
            log.warn("No default authentication strategy configured, returning request specification as is");
            return requestSpec;
        }
        
        return strategy.authenticate(requestSpec);
    }

    /**
     * Checks if a strategy is registered.
     *
     * @param name The name of the strategy
     * @return True if the strategy is registered, false otherwise
     */
    public boolean hasStrategy(String name) {
        return strategies.containsKey(name);
    }

    /**
     * Removes a strategy.
     *
     * @param name The name of the strategy
     */
    public void removeStrategy(String name) {
        if (strategies.containsKey(name)) {
            strategies.remove(name);
            log.info("Removed authentication strategy: {}", name);
            
            if (name.equals(defaultStrategyName)) {
                defaultStrategyName = "none";
                log.warn("Default authentication strategy was removed, set to 'none'");
            }
        }
    }

    /**
     * Gets all registered strategies.
     *
     * @return The registered strategies
     */
    public Map<String, AuthenticationStrategy> getAllStrategies() {
        return new HashMap<>(strategies);
    }

    /**
     * Creates and registers a basic authentication strategy.
     *
     * @param name The name of the strategy
     * @param username The username
     * @param password The password
     * @return The created strategy
     */
    public AuthenticationStrategy createBasicAuthStrategy(String name, String username, String password) {
        BasicAuthStrategy strategy = new BasicAuthStrategy(username, password);
        registerStrategy(name, strategy);
        return strategy;
    }

    /**
     * Creates and registers a bearer token authentication strategy.
     *
     * @param name The name of the strategy
     * @param token The bearer token
     * @return The created strategy
     */
    public AuthenticationStrategy createBearerTokenStrategy(String name, String token) {
        BearerTokenStrategy strategy = new BearerTokenStrategy(token);
        registerStrategy(name, strategy);
        return strategy;
    }

    /**
     * Creates and registers an API key authentication strategy.
     *
     * @param name The name of the strategy
     * @param apiKey The API key
     * @param apiKeyName The name of the API key parameter
     * @param location The location of the API key
     * @return The created strategy
     */
    public AuthenticationStrategy createApiKeyStrategy(String name, String apiKey, String apiKeyName, ApiKeyStrategy.ApiKeyLocation location) {
        ApiKeyStrategy strategy = new ApiKeyStrategy(apiKey, apiKeyName, location);
        registerStrategy(name, strategy);
        return strategy;
    }

    /**
     * Creates and registers an OAuth 2.0 authentication strategy.
     *
     * @param name The name of the strategy
     * @param clientId The client ID
     * @param clientSecret The client secret
     * @param scope The scope
     * @return The created strategy
     */
    public AuthenticationStrategy createOAuth2Strategy(String name, String clientId, String clientSecret, String scope) {
        OAuth2Strategy strategy = new OAuth2Strategy(clientId, clientSecret, scope);
        registerStrategy(name, strategy);
        return strategy;
    }

    /**
     * Initializes default authentication strategies from configuration.
     */
    public void initializeDefaultStrategies() {
        // Check if basic auth is configured
        if (configManager.getBooleanProperty("auth.basic.enabled", false)) {
            try {
                String username = configManager.getProperty("auth.basic.username");
                String password = configManager.getProperty("auth.basic.password");
                createBasicAuthStrategy("basic", username, password);
                log.info("Initialized basic authentication strategy");
            } catch (Exception e) {
                log.error("Failed to initialize basic authentication strategy: {}", e.getMessage());
            }
        }
        
        // Check if bearer token is configured
        if (configManager.getBooleanProperty("auth.bearer.enabled", false)) {
            try {
                String username = configManager.getProperty("auth.bearer.username");
                String password = configManager.getProperty("auth.bearer.password");
                BearerTokenStrategy strategy = new BearerTokenStrategy(username, password);
                registerStrategy("bearer", strategy);
                log.info("Initialized bearer token authentication strategy");
            } catch (Exception e) {
                log.error("Failed to initialize bearer token authentication strategy: {}", e.getMessage());
            }
        }
        
        // Check if API key is configured
        if (configManager.getBooleanProperty("auth.apiKey.enabled", false)) {
            try {
                String apiKey = configManager.getProperty("auth.apiKey.value");
                String apiKeyName = configManager.getProperty("auth.apiKey.name", "api_key");
                String locationStr = configManager.getProperty("auth.apiKey.location", "HEADER");
                ApiKeyStrategy.ApiKeyLocation location = ApiKeyStrategy.ApiKeyLocation.valueOf(locationStr.toUpperCase());
                createApiKeyStrategy("apiKey", apiKey, apiKeyName, location);
                log.info("Initialized API key authentication strategy");
            } catch (Exception e) {
                log.error("Failed to initialize API key authentication strategy: {}", e.getMessage());
            }
        }
        
        // Check if OAuth 2.0 is configured
        if (configManager.getBooleanProperty("auth.oauth2.enabled", false)) {
            try {
                OAuth2Strategy strategy = new OAuth2Strategy();
                registerStrategy("oauth2", strategy);
                log.info("Initialized OAuth 2.0 authentication strategy");
            } catch (Exception e) {
                log.error("Failed to initialize OAuth 2.0 authentication strategy: {}", e.getMessage());
            }
        }
        
        // Set default strategy if configured
        String defaultStrategy = configManager.getProperty("auth.defaultStrategy", "none");
        if (!"none".equals(defaultStrategy) && strategies.containsKey(defaultStrategy)) {
            setDefaultStrategy(defaultStrategy);
        }
    }
}