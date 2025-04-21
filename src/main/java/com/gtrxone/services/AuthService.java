package com.gtrxone.services;

import com.gtrxone.core.BaseService;
import com.gtrxone.exceptions.ApiException;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class for authentication operations.
 * Handles authentication operations such as login, logout, token refresh, etc.
 */
@Slf4j
public class AuthService extends BaseService {
    
    @Getter
    private String authToken;
    
    private final String loginEndpoint;
    private final String logoutEndpoint;
    private final String refreshTokenEndpoint;
    
    /**
     * Constructs a new AuthService.
     */
    public AuthService() {
        super();
        this.loginEndpoint = configManager.getProperty("auth.endpoints.login", "/auth/login");
        this.logoutEndpoint = configManager.getProperty("auth.endpoints.logout", "/auth/logout");
        this.refreshTokenEndpoint = configManager.getProperty("auth.endpoints.refreshToken", "/auth/refresh");
    }
    
    /**
     * Logs in with the specified credentials.
     *
     * @param username The username
     * @param password The password
     * @return The authentication token
     * @throws ApiException if the login fails
     */
    @Step("Login with username: {username}")
    public String login(String username, String password) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        
        Response response = post(loginEndpoint, requestBody);
        
        if (response.getStatusCode() != 200) {
            throw new ApiException("Login failed with status code: " + response.getStatusCode());
        }
        
        authToken = extractToken(response);
        log.info("Successfully logged in as: {}", username);
        
        return authToken;
    }
    
    /**
     * Logs out the current user.
     *
     * @return True if the logout was successful, false otherwise
     */
    @Step("Logout")
    public boolean logout() {
        if (authToken == null) {
            log.warn("No active session to logout");
            return false;
        }
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authToken);
        
        Response response = post(logoutEndpoint, null, null, headers);
        
        boolean success = response.getStatusCode() == 200;
        
        if (success) {
            authToken = null;
            log.info("Successfully logged out");
        } else {
            log.error("Logout failed with status code: {}", response.getStatusCode());
        }
        
        return success;
    }
    
    /**
     * Refreshes the authentication token.
     *
     * @return The new authentication token
     * @throws ApiException if the token refresh fails
     */
    @Step("Refresh authentication token")
    public String refreshToken() {
        if (authToken == null) {
            throw new ApiException("No authentication token to refresh");
        }
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authToken);
        
        Response response = post(refreshTokenEndpoint, null, null, headers);
        
        if (response.getStatusCode() != 200) {
            throw new ApiException("Token refresh failed with status code: " + response.getStatusCode());
        }
        
        authToken = extractToken(response);
        log.info("Successfully refreshed authentication token");
        
        return authToken;
    }
    
    /**
     * Validates the current authentication token.
     *
     * @return True if the token is valid, false otherwise
     */
    @Step("Validate authentication token")
    public boolean validateToken() {
        if (authToken == null) {
            return false;
        }
        
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + authToken);
            
            Response response = get("/auth/validate", null, headers);
            
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extracts the authentication token from the response.
     *
     * @param response The response
     * @return The authentication token
     * @throws ApiException if the token cannot be extracted
     */
    private String extractToken(Response response) {
        try {
            return response.jsonPath().getString("token");
        } catch (Exception e) {
            throw new ApiException("Failed to extract authentication token from response", e);
        }
    }
    
    /**
     * Gets the authentication headers.
     *
     * @return The authentication headers
     * @throws ApiException if there is no authentication token
     */
    public Map<String, String> getAuthHeaders() {
        if (authToken == null) {
            throw new ApiException("No authentication token available");
        }
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authToken);
        
        return headers;
    }
    
    /**
     * Checks if the user is authenticated.
     *
     * @return True if the user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return authToken != null;
    }
    
    /**
     * Clears the authentication token.
     */
    public void clearToken() {
        authToken = null;
        log.info("Authentication token cleared");
    }
}