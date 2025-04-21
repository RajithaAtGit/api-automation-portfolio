package com.gtrxone.auth;

import com.gtrxone.config.ConfigManager;
import com.gtrxone.exceptions.ApiException;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of AuthenticationStrategy for Bearer Token Authentication.
 * Supports JWT tokens.
 */
@Slf4j
public class BearerTokenStrategy implements AuthenticationStrategy {

    private final ConfigManager configManager;
    private String token;
    private LocalDateTime tokenExpiry;
    private final String tokenEndpoint;
    private final String refreshEndpoint;
    private final String username;
    private final String password;
    private final int expiryBufferMinutes;

    /**
     * Constructs a new BearerTokenStrategy with the specified token.
     *
     * @param token The bearer token
     */
    public BearerTokenStrategy(String token) {
        this.configManager = ConfigManager.getInstance();
        this.token = token;
        this.tokenEndpoint = configManager.getProperty("auth.endpoints.login", "/auth/login");
        this.refreshEndpoint = configManager.getProperty("auth.endpoints.refreshToken", "/auth/refresh");
        this.username = null;
        this.password = null;
        this.expiryBufferMinutes = configManager.getIntProperty("auth.tokenExpiryBufferMinutes", 5);

        // Set token expiry to a future time
        int expiryMinutes = configManager.getIntProperty("auth.tokenExpiryMinutes", 60);
        this.tokenExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);

        log.debug("Created BearerTokenStrategy with provided token");
    }

    /**
     * Constructs a new BearerTokenStrategy with credentials for obtaining a token.
     *
     * @param username The username
     * @param password The password
     */
    public BearerTokenStrategy(String username, String password) {
        this.configManager = ConfigManager.getInstance();
        this.username = username;
        this.password = password;
        this.tokenEndpoint = configManager.getProperty("auth.endpoints.login", "/auth/login");
        this.refreshEndpoint = configManager.getProperty("auth.endpoints.refreshToken", "/auth/refresh");
        this.expiryBufferMinutes = configManager.getIntProperty("auth.tokenExpiryBufferMinutes", 5);

        // Token will be obtained when needed
        this.token = null;
        this.tokenExpiry = null;

        log.debug("Created BearerTokenStrategy for user: {}", username);
    }

    /**
     * Constructs a new BearerTokenStrategy with credentials from configuration.
     */
    public BearerTokenStrategy() {
        this.configManager = ConfigManager.getInstance();
        this.username = configManager.getProperty("auth.bearer.username");
        this.password = configManager.getProperty("auth.bearer.password");
        this.tokenEndpoint = configManager.getProperty("auth.endpoints.login", "/auth/login");
        this.refreshEndpoint = configManager.getProperty("auth.endpoints.refreshToken", "/auth/refresh");
        this.expiryBufferMinutes = configManager.getIntProperty("auth.tokenExpiryBufferMinutes", 5);

        // Token will be obtained when needed
        this.token = null;
        this.tokenExpiry = null;

        log.debug("Created BearerTokenStrategy for user from configuration: {}", username);
    }

    @Override
    public RequestSpecification authenticate(RequestSpecification requestSpec) {
        ensureValidToken();

        log.debug("Applying Bearer Token Authentication");
        return requestSpec.header("Authorization", "Bearer " + token);
    }

    @Override
    public String getType() {
        return "Bearer";
    }

    @Override
    public boolean isValid() {
        if (token == null || token.isEmpty()) {
            return false;
        }

        if (tokenExpiry == null) {
            return false;
        }

        // Check if token is about to expire
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(tokenExpiry.minus(expiryBufferMinutes, ChronoUnit.MINUTES));
    }

    @Override
    public boolean refresh() {
        if (username == null || password == null) {
            log.error("Cannot refresh token: no credentials provided");
            return false;
        }

        try {
            // This is a placeholder for actual token refresh logic
            // In a real implementation, you would call the refresh endpoint
            log.info("Refreshing bearer token");

            // Simulate token refresh
            token = "refreshed_token_" + System.currentTimeMillis();
            int expiryMinutes = configManager.getIntProperty("auth.tokenExpiryMinutes", 60);
            tokenExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);

            return true;
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Ensures that a valid token is available.
     * If the token is missing or expired, it will be obtained or refreshed.
     *
     * @throws ApiException if the token cannot be obtained or refreshed
     */
    private void ensureValidToken() {
        if (!isValid()) {
            if (token == null) {
                obtainToken();
            } else {
                if (!refresh()) {
                    throw new ApiException("Failed to refresh bearer token");
                }
            }
        }
    }

    /**
     * Obtains a new token using the provided credentials.
     *
     * @throws ApiException if the token cannot be obtained
     */
    private void obtainToken() {
        if (username == null || password == null) {
            throw new ApiException("Cannot obtain token: no credentials provided");
        }

        try {
            // This is a placeholder for actual token acquisition logic
            // In a real implementation, you would call the token endpoint
            log.info("Obtaining bearer token for user: {}", username);

            // Simulate token acquisition
            token = "new_token_" + System.currentTimeMillis();
            int expiryMinutes = configManager.getIntProperty("auth.tokenExpiryMinutes", 60);
            tokenExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);

        } catch (Exception e) {
            throw new ApiException("Failed to obtain bearer token: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the current token.
     *
     * @return The current token
     */
    public String getToken() {
        ensureValidToken();
        return token;
    }
}