package com.gtrxone.config;

import com.gtrxone.exceptions.ConfigurationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * Represents configuration for a specific environment.
 * Provides typed access to configuration properties.
 */
@Slf4j
public class EnvironmentConfig {
    @Getter
    private final Properties properties;

    /**
     * Constructs a new EnvironmentConfig with the specified properties.
     *
     * @param properties The properties for this environment
     */
    public EnvironmentConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * Gets a property value.
     *
     * @param key The property key
     * @return The property value
     * @throws ConfigurationException if the property is not found
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new ConfigurationException("Property not found: " + key);
        }
        return value;
    }

    /**
     * Gets a property value with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value or the default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets a property as an integer.
     *
     * @param key The property key
     * @return The property value as an integer
     * @throws ConfigurationException if the property is not found or cannot be parsed as an integer
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Property " + key + " is not a valid integer: " + value, e);
        }
    }

    /**
     * Gets a property as an integer with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value as an integer or the default value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Property {} is not a valid integer: {}. Using default value: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets a property as a boolean.
     *
     * @param key The property key
     * @return The property value as a boolean
     * @throws ConfigurationException if the property is not found
     */
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets a property as a boolean with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value as a boolean or the default value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets a property as a long.
     *
     * @param key The property key
     * @return The property value as a long
     * @throws ConfigurationException if the property is not found or cannot be parsed as a long
     */
    public long getLongProperty(String key) {
        String value = getProperty(key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Property " + key + " is not a valid long: " + value, e);
        }
    }

    /**
     * Gets a property as a double.
     *
     * @param key The property key
     * @return The property value as a double
     * @throws ConfigurationException if the property is not found or cannot be parsed as a double
     */
    public double getDoubleProperty(String key) {
        String value = getProperty(key);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Property " + key + " is not a valid double: " + value, e);
        }
    }
}