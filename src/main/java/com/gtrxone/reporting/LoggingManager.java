package com.gtrxone.reporting;

import com.gtrxone.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manager class for logging configuration.
 * Provides methods for configuring and managing logging.
 */
@Slf4j
public class LoggingManager {

    private static final LoggingManager instance = new LoggingManager();
    private final ConfigManager configManager;
    private Level logLevel;
    private boolean logToFile;
    private boolean logToConsole;
    private String logDirectory;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private LoggingManager() {
        this.configManager = ConfigManager.getInstance();
        initializeLogging();
    }

    /**
     * Gets the singleton instance of LoggingManager.
     *
     * @return The singleton instance of LoggingManager
     */
    public static LoggingManager getInstance() {
        return instance;
    }

    /**
     * Initializes logging based on configuration.
     */
    private void initializeLogging() {
        // Get logging configuration from properties
        String logLevelStr = configManager.getProperty("logging.level", "INFO");
        this.logLevel = Level.getLevel(logLevelStr);
        this.logToFile = configManager.getBooleanProperty("logging.file.enabled", true);
        this.logToConsole = configManager.getBooleanProperty("logging.console.enabled", true);
        this.logDirectory = configManager.getProperty("logging.directory", "logs");

        // Create log directory if it doesn't exist
        createLogDirectory();

        // Update Log4j2 configuration
        updateLog4j2Configuration();

        // Log initialization
        log.info("Logging initialized with level: {}", logLevel);
        log.info("Log to file: {}", logToFile);
        log.info("Log to console: {}", logToConsole);
        log.info("Log directory: {}", logDirectory);
    }

    /**
     * Creates the log directory if it doesn't exist.
     */
    private void createLogDirectory() {
        try {
            Path path = Paths.get(logDirectory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created log directory: {}", logDirectory);
            }
        } catch (Exception e) {
            log.error("Failed to create log directory: {}", e.getMessage());
        }
    }

    /**
     * Updates the Log4j2 configuration.
     */
    private void updateLog4j2Configuration() {
        try {
            // Get Log4j2 context
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            // Set system property for log directory
            System.setProperty("logging.directory", logDirectory);

            // Update root logger level
            LoggerConfig rootLogger = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            rootLogger.setLevel(logLevel);

            // Update appenders
            if (!logToConsole) {
                rootLogger.removeAppender("ConsoleAppender");
            }

            if (!logToFile) {
                rootLogger.removeAppender("FileAppender");
            }

            // Update context
            context.updateLoggers();
            log.debug("Log4j2 configuration updated");
        } catch (Exception e) {
            log.error("Failed to update Log4j2 configuration: {}", e.getMessage());
        }
    }

    /**
     * Sets the log level.
     *
     * @param level The log level
     */
    public void setLogLevel(Level level) {
        this.logLevel = level;
        updateLog4j2Configuration();
        log.info("Log level set to: {}", level);
    }

    /**
     * Sets whether to log to file.
     *
     * @param logToFile Whether to log to file
     */
    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
        updateLog4j2Configuration();
        log.info("Log to file set to: {}", logToFile);
    }

    /**
     * Sets whether to log to console.
     *
     * @param logToConsole Whether to log to console
     */
    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
        updateLog4j2Configuration();
        log.info("Log to console set to: {}", logToConsole);
    }

    /**
     * Sets the log directory.
     *
     * @param logDirectory The log directory
     */
    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
        createLogDirectory();
        updateLog4j2Configuration();
        log.info("Log directory set to: {}", logDirectory);
    }

    /**
     * Gets the log level.
     *
     * @return The log level
     */
    public Level getLogLevel() {
        return logLevel;
    }

    /**
     * Gets whether to log to file.
     *
     * @return Whether to log to file
     */
    public boolean isLogToFile() {
        return logToFile;
    }

    /**
     * Gets whether to log to console.
     *
     * @return Whether to log to console
     */
    public boolean isLogToConsole() {
        return logToConsole;
    }

    /**
     * Gets the log directory.
     *
     * @return The log directory
     */
    public String getLogDirectory() {
        return logDirectory;
    }
}