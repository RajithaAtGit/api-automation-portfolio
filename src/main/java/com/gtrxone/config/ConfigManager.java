/*
 * Proprietary and Confidential
 *
 * Copyright (c) 2025 R N W Gunawardana. All Rights Reserved.
 *
 * This software and its documentation are proprietary and confidential information of R N W Gunawardana.
 * No part of this software, including but not limited to the source code, documentation, specifications,
 * and design, may be reproduced, stored in a retrieval system, transmitted in any form or by any means,
 * or distributed in any way without the explicit prior written permission of R N W Gunawardana.
 *
 * UNAUTHORIZED COPYING, REPRODUCTION, MODIFICATION, DISTRIBUTION, OR USE OF THIS SOFTWARE,
 * VIA ANY MEDIUM, IS STRICTLY PROHIBITED.
 *
 * The receipt or possession of this software does not convey any rights to use, modify, or distribute it.
 * Use of this software is subject to a valid license agreement with R N W Gunawardana.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING, BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIMS,
 * DAMAGES OR OTHER LIABILITIES ARISING FROM THE USE OF THIS SOFTWARE.
 *
 * AI Training Restriction Notice:
 * Use of this codebase for training artificial intelligence and machine learning models is strictly
 * prohibited without explicit written permission from R N W Gunawardana. Any unauthorized use for
 * AI/ML training purposes will be considered a violation of intellectual property rights and may
 * result in legal action.
 */
package com.gtrxone.config;

import com.gtrxone.exceptions.ConfigurationException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class responsible for managing configuration properties across the framework.
 * Implements the Singleton pattern to provide a single access point to configuration.
 */
@Slf4j
public class ConfigManager {
    private static volatile ConfigManager instance;
    private final Map<String, EnvironmentConfig> environmentConfigs = new ConcurrentHashMap<>();
    private String currentEnvironment;
    private static final String DEFAULT_ENV = "qa";
    private static final String ENV_PROPERTY = "env";

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the configuration manager with the default environment.
     */
    private ConfigManager() {
        currentEnvironment = System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
        log.info("Initializing ConfigManager with environment: {}", currentEnvironment);
        loadEnvironmentConfig(currentEnvironment);
    }

    /**
     * Gets the singleton instance of ConfigManager.
     * Uses double-checked locking for thread safety.
     *
     * @return The singleton instance of ConfigManager
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the configuration for the current environment.
     *
     * @return The EnvironmentConfig for the current environment
     */
    public EnvironmentConfig getCurrentConfig() {
        return getEnvironmentConfig(currentEnvironment);
    }

    /**
     * Gets the configuration for a specific environment.
     * Loads the configuration if it hasn't been loaded yet.
     *
     * @param environment The environment name
     * @return The EnvironmentConfig for the specified environment
     */
    public EnvironmentConfig getEnvironmentConfig(String environment) {
        if (!environmentConfigs.containsKey(environment)) {
            loadEnvironmentConfig(environment);
        }
        return environmentConfigs.get(environment);
    }

    /**
     * Switches the current environment.
     *
     * @param environment The new environment to switch to
     * @return The EnvironmentConfig for the new environment
     */
    public EnvironmentConfig switchEnvironment(String environment) {
        log.info("Switching environment from {} to {}", currentEnvironment, environment);
        currentEnvironment = environment;
        return getEnvironmentConfig(environment);
    }

    /**
     * Loads the configuration for a specific environment.
     *
     * @param environment The environment to load configuration for
     */
    private void loadEnvironmentConfig(String environment) {
        log.info("Loading configuration for environment: {}", environment);
        Properties properties = new Properties();

        // Load default properties
        loadProperties(properties, "src/main/resources/config.properties");

        // Load environment-specific properties
        loadProperties(properties, String.format("src/test/resources/environments/%s.properties", environment));

        // Create environment config
        EnvironmentConfig config = new EnvironmentConfig(properties);
        environmentConfigs.put(environment, config);
    }

    /**
     * Loads properties from a file.
     *
     * @param properties The Properties object to load into
     * @param filePath The path to the properties file
     */
    private void loadProperties(Properties properties, String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            log.debug("Loaded properties from: {}", filePath);
        } catch (IOException e) {
            log.warn("Could not load properties from: {}. Error: {}", filePath, e.getMessage());
        }
    }

    /**
     * Gets a configuration property value.
     *
     * @param key The property key
     * @return The property value
     * @throws ConfigurationException if the property is not found
     */
    public String getProperty(String key) {
        return getCurrentConfig().getProperty(key);
    }

    /**
     * Gets a configuration property value with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value or the default value
     */
    public String getProperty(String key, String defaultValue) {
        return getCurrentConfig().getProperty(key, defaultValue);
    }

    /**
     * Gets a configuration property as an integer.
     *
     * @param key The property key
     * @return The property value as an integer
     * @throws ConfigurationException if the property is not found or cannot be parsed as an integer
     */
    public int getIntProperty(String key) {
        return getCurrentConfig().getIntProperty(key);
    }

    /**
     * Gets a configuration property as a boolean.
     *
     * @param key The property key
     * @return The property value as a boolean
     * @throws ConfigurationException if the property is not found
     */
    public boolean getBooleanProperty(String key) {
        return getCurrentConfig().getBooleanProperty(key);
    }

    /**
     * Gets a configuration property as a boolean with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value as a boolean or the default value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return getCurrentConfig().getBooleanProperty(key, defaultValue);
    }

    /**
     * Gets a configuration property as a long.
     *
     * @param key The property key
     * @return The property value as a long
     * @throws ConfigurationException if the property is not found or cannot be parsed as a long
     */
    public long getLongProperty(String key) {
        return getCurrentConfig().getLongProperty(key);
    }

    /**
     * Gets a configuration property as a long with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value as a long or the default value
     */
    public long getLongProperty(String key, long defaultValue) {
        try {
            return getLongProperty(key);
        } catch (ConfigurationException e) {
            return defaultValue;
        }
    }

    /**
     * Gets a configuration property as an integer with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value as an integer or the default value
     */
    public int getIntProperty(String key, int defaultValue) {
        return getCurrentConfig().getIntProperty(key, defaultValue);
    }

    /**
     * Clears all loaded configurations.
     * Mainly used for testing purposes.
     */
    public void clearConfigurations() {
        environmentConfigs.clear();
        currentEnvironment = System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
        loadEnvironmentConfig(currentEnvironment);
    }
}
