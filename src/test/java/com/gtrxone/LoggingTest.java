package com.gtrxone;

import com.gtrxone.reporting.LoggingManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * Test class for verifying the centralized logging mechanism.
 */
@Slf4j
public class LoggingTest {

    /**
     * Tests the centralized logging mechanism.
     * Logs messages at different levels to verify that they are properly logged.
     */
    @Test
    public void testCentralizedLogging() {
        // Initialize the logging manager
        LoggingManager loggingManager = LoggingManager.getInstance();
        
        // Log messages at different levels
        log.trace("This is a TRACE message");
        log.debug("This is a DEBUG message");
        log.info("This is an INFO message");
        log.warn("This is a WARN message");
        log.error("This is an ERROR message");
        
        // Log a message with an exception
        try {
            throw new RuntimeException("Test exception");
        } catch (Exception e) {
            log.error("This is an ERROR message with an exception", e);
        }
        
        // Log a message with parameters
        log.info("This is a message with parameters: {}, {}, {}", "param1", 123, true);
        
        // Print a message to verify the test ran
        System.out.println("[DEBUG_LOG] Centralized logging test completed. Check logs directory for log files.");
    }
}