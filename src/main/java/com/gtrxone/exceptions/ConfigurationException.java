package com.gtrxone.exceptions;

/**
 * Exception thrown when there is an issue with configuration.
 * This could be due to missing properties, invalid property values, or issues loading configuration files.
 */
public class ConfigurationException extends RuntimeException {

    /**
     * Constructs a new ConfigurationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConfigurationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ConfigurationException with the specified cause.
     *
     * @param cause the cause
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}