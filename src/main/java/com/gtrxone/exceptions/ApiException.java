package com.gtrxone.exceptions;

/**
 * Exception thrown when there is an issue with API requests or responses.
 * This could be due to network issues, invalid responses, or other API-related problems.
 */
public class ApiException extends RuntimeException {

    /**
     * Constructs a new ApiException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new ApiException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ApiException with the specified cause.
     *
     * @param cause the cause
     */
    public ApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ApiException with the specified status code and detail message.
     *
     * @param statusCode the HTTP status code
     * @param message the detail message
     */
    public ApiException(int statusCode, String message) {
        super("API Error (Status " + statusCode + "): " + message);
    }
}