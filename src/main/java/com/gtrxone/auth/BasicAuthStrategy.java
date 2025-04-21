package com.gtrxone.auth;

import com.gtrxone.config.ConfigManager;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthenticationStrategy for Basic Authentication.
 */
@Slf4j
public class BasicAuthStrategy implements AuthenticationStrategy {

    private final String username;
    private final String password;
    private final ConfigManager configManager;

    /**
     * Constructs a new BasicAuthStrategy with the specified credentials.
     *
     * @param username The username
     * @param password The password
     */
    public BasicAuthStrategy(String username, String password) {
        this.username = username;
        this.password = password;
        this.configManager = ConfigManager.getInstance();
        
        log.debug("Created BasicAuthStrategy for user: {}", username);
    }

    /**
     * Constructs a new BasicAuthStrategy with credentials from configuration.
     */
    public BasicAuthStrategy() {
        this.configManager = ConfigManager.getInstance();
        this.username = configManager.getProperty("auth.basic.username");
        this.password = configManager.getProperty("auth.basic.password");
        
        log.debug("Created BasicAuthStrategy for user: {}", username);
    }

    @Override
    public RequestSpecification authenticate(RequestSpecification requestSpec) {
        log.debug("Applying Basic Authentication for user: {}", username);
        return requestSpec.auth().basic(username, password);
    }

    @Override
    public String getType() {
        return "Basic";
    }

    @Override
    public boolean isValid() {
        // Basic auth credentials are always considered valid
        // In a real implementation, you might want to validate them against the server
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    @Override
    public boolean refresh() {
        // Basic auth doesn't need refreshing
        return true;
    }
}