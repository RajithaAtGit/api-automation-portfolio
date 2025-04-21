package com.gtrxone.auth;

import com.gtrxone.config.ConfigManager;
import com.gtrxone.exceptions.ApiException;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of AuthenticationStrategy for OAuth 2.0 Authentication.
 */
@Slf4j
public class OAuth2Strategy implements AuthenticationStrategy {

    private final ConfigManager configManager;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpiry;
    private final String clientId;
    private final String clientSecret;
    private final String tokenEndpoint;
    private final String refreshEndpoint;
    private final String scope;
    private final GrantType grantType;
    private final int expiryBufferMinutes;

    /**
     * Enum representing the OAuth 2.0 grant types.
     */
    public enum GrantType {
        CLIENT_CREDENTIALS,
        PASSWORD,
        AUTHORIZATION_CODE,
        IMPLICIT
    }

    /**
     * Constructs a new OAuth2Strategy with the specified access token.
     *
     * @param accessToken The access token
     */
    public OAuth2Strategy(String accessToken) {
        this.configManager = ConfigManager.getInstance();
        this.accessToken = accessToken;
        this.refreshToken = null;
        this.clientId = null;
        this.clientSecret = null;
        this.tokenEndpoint = configManager.getProperty("auth.oauth2.tokenEndpoint", "/oauth/token");
        this.refreshEndpoint = configManager.getProperty("auth.oauth2.refreshEndpoint", "/oauth/token");
        this.scope = null;
        this.grantType = GrantType.CLIENT_CREDENTIALS;
        this.expiryBufferMinutes = configManager.getIntProperty("auth.oauth2.expiryBufferMinutes", 5);
        
        // Set token expiry to a future time
        int expiryMinutes = configManager.getIntProperty("auth.oauth2.expiryMinutes", 60);
        this.tokenExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);
        
        log.debug("Created OAuth2Strategy with provided access token");
    }

    /**
     * Constructs a new OAuth2Strategy with client credentials.
     *
     * @param clientId The client ID
     * @param clientSecret The client secret
     * @param scope The scope
     */
    public OAuth2Strategy(String clientId, String clientSecret, String scope) {
        this.configManager = ConfigManager.getInstance();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.tokenEndpoint = configManager.getProperty("auth.oauth2.tokenEndpoint", "/oauth/token");
        this.refreshEndpoint = configManager.getProperty("auth.oauth2.refreshEndpoint", "/oauth/token");
        this.grantType = GrantType.CLIENT_CREDENTIALS;
        this.expiryBufferMinutes = configManager.getIntProperty("auth.oauth2.expiryBufferMinutes", 5);
        
        // Tokens will be obtained when needed
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpiry = null;
        
        log.debug("Created OAuth2Strategy with client credentials");
    }

    /**
     * Constructs a new OAuth2Strategy with credentials from configuration.
     */
    public OAuth2Strategy() {
        this.configManager = ConfigManager.getInstance();
        this.clientId = configManager.getProperty("auth.oauth2.clientId");
        this.clientSecret = configManager.getProperty("auth.oauth2.clientSecret");
        this.scope = configManager.getProperty("auth.oauth2.scope", "");
        this.tokenEndpoint = configManager.getProperty("auth.oauth2.tokenEndpoint", "/oauth/token");
        this.refreshEndpoint = configManager.getProperty("auth.oauth2.refreshEndpoint", "/oauth/token");
        this.expiryBufferMinutes = configManager.getIntProperty("auth.oauth2.expiryBufferMinutes", 5);
        
        String grantTypeStr = configManager.getProperty("auth.oauth2.grantType", "CLIENT_CREDENTIALS");
        this.grantType = GrantType.valueOf(grantTypeStr.toUpperCase());
        
        // Tokens will be obtained when needed
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpiry = null;
        
        log.debug("Created OAuth2Strategy from configuration");
    }

    @Override
    public RequestSpecification authenticate(RequestSpecification requestSpec) {
        ensureValidToken();
        
        log.debug("Applying OAuth 2.0 Authentication");
        return requestSpec.header("Authorization", "Bearer " + accessToken);
    }

    @Override
    public String getType() {
        return "OAuth2";
    }

    @Override
    public boolean isValid() {
        if (accessToken == null || accessToken.isEmpty()) {
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
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("No refresh token available, obtaining new access token");
            return obtainToken();
        }
        
        try {
            // This is a placeholder for actual token refresh logic
            // In a real implementation, you would call the refresh endpoint
            log.info("Refreshing OAuth 2.0 token");
            
            // Simulate token refresh
            accessToken = "refreshed_oauth2_token_" + System.currentTimeMillis();
            refreshToken = "refreshed_refresh_token_" + System.currentTimeMillis();
            int expiryMinutes = configManager.getIntProperty("auth.oauth2.expiryMinutes", 60);
            tokenExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to refresh OAuth 2.0 token: {}", e.getMessage());
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
            if (accessToken == null) {
                if (!obtainToken()) {
                    throw new ApiException("Failed to obtain OAuth 2.0 token");
                }
            } else {
                if (!refresh()) {
                    throw new ApiException("Failed to refresh OAuth 2.0 token");
                }
            }
        }
    }

    /**
     * Obtains a new token using the configured grant type.
     *
     * @return True if the token was obtained successfully, false otherwise
     */
    private boolean obtainToken() {
        if (clientId == null || clientSecret == null) {
            log.error("Cannot obtain token: no client credentials provided");
            return false;
        }
        
        try {
            // This is a placeholder for actual token acquisition logic
            // In a real implementation, you would call the token endpoint with the appropriate grant type
            log.info("Obtaining OAuth 2.0 token with grant type: {}", grantType);
            
            // Simulate token acquisition
            accessToken = "new_oauth2_token_" + System.currentTimeMillis();
            refreshToken = "new_refresh_token_" + System.currentTimeMillis();
            int expiryMinutes = configManager.getIntProperty("auth.oauth2.expiryMinutes", 60);
            tokenExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to obtain OAuth 2.0 token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the current access token.
     *
     * @return The current access token
     */
    public String getAccessToken() {
        ensureValidToken();
        return accessToken;
    }

    /**
     * Gets the current refresh token.
     *
     * @return The current refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Gets the token parameters for the configured grant type.
     *
     * @return The token parameters
     */
    private Map<String, String> getTokenParameters() {
        Map<String, String> params = new HashMap<>();
        
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        
        switch (grantType) {
            case CLIENT_CREDENTIALS:
                params.put("grant_type", "client_credentials");
                if (scope != null && !scope.isEmpty()) {
                    params.put("scope", scope);
                }
                break;
            case PASSWORD:
                params.put("grant_type", "password");
                params.put("username", configManager.getProperty("auth.oauth2.username"));
                params.put("password", configManager.getProperty("auth.oauth2.password"));
                if (scope != null && !scope.isEmpty()) {
                    params.put("scope", scope);
                }
                break;
            case AUTHORIZATION_CODE:
                params.put("grant_type", "authorization_code");
                params.put("code", configManager.getProperty("auth.oauth2.code"));
                params.put("redirect_uri", configManager.getProperty("auth.oauth2.redirectUri"));
                break;
            case IMPLICIT:
                // Implicit grant type doesn't use the token endpoint
                log.warn("Implicit grant type doesn't use the token endpoint");
                break;
        }
        
        return params;
    }
}