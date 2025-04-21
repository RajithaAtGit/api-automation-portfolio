package com.gtrxone.auth;

import io.restassured.specification.RequestSpecification;

/**
 * Interface for authentication strategies.
 * Part of the Authentication and Authorization feature.
 * Implements the Strategy Pattern for interchangeable authentication mechanisms.
 */
public interface AuthenticationStrategy {

    /**
     * Applies authentication to the request specification.
     *
     * @param requestSpec The request specification to authenticate
     * @return The authenticated request specification
     */
    RequestSpecification authenticate(RequestSpecification requestSpec);
    
    /**
     * Gets the authentication type.
     *
     * @return The authentication type
     */
    String getType();
    
    /**
     * Checks if the authentication credentials are valid.
     *
     * @return True if the credentials are valid, false otherwise
     */
    boolean isValid();
    
    /**
     * Refreshes the authentication credentials if needed.
     *
     * @return True if the refresh was successful, false otherwise
     */
    boolean refresh();
}