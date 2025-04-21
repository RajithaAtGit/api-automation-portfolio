package com.gtrxone.core;

import com.gtrxone.config.ConfigManager;
import com.gtrxone.utils.TestLogger;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base class for all test classes.
 * Implements the Template Method pattern to define the standard test lifecycle.
 */
@Slf4j
public abstract class BaseTest {

    protected ConfigManager configManager;

    /**
     * Setup method that runs before the test suite.
     * Initializes global resources and configurations.
     */
    @BeforeSuite(alwaysRun = true)
    protected void beforeSuite() {
        log.info("Starting test suite execution");
        initializeGlobalResources();
    }

    /**
     * Setup method that runs before each test class.
     * Initializes class-level resources and configurations.
     */
    @BeforeClass(alwaysRun = true)
    protected void beforeClass() {
        log.info("Starting test class: {}", getClass().getSimpleName());
        configManager = ConfigManager.getInstance();
        initializeClassResources();
    }

    /**
     * Setup method that runs before each test method.
     * Initializes test-specific resources and configurations.
     * 
     * @param params The test method parameters
     */
    @BeforeMethod(alwaysRun = true)
    protected void beforeMethod(Object[] params) {
        String methodName = getTestMethodName();
        log.info("Starting test method: {}", methodName);
        TestLogger.logTestStart(getClass(), methodName);
        initializeTestResources();
    }

    /**
     * Cleanup method that runs after each test method.
     * Cleans up test-specific resources.
     * 
     * @param result The test result
     */
    @AfterMethod(alwaysRun = true)
    protected void afterMethod(ITestResult result) {
        logTestResult(result);
        TestLogger.logTestEnd(result);
        cleanupTestResources();
    }

    /**
     * Cleanup method that runs after each test class.
     * Cleans up class-level resources.
     */
    @AfterClass(alwaysRun = true)
    protected void afterClass() {
        log.info("Finishing test class: {}", getClass().getSimpleName());
        cleanupClassResources();
    }

    /**
     * Cleanup method that runs after the test suite.
     * Cleans up global resources.
     */
    @AfterSuite(alwaysRun = true)
    protected void afterSuite() {
        log.info("Finishing test suite execution");
        cleanupGlobalResources();
    }

    /**
     * Initializes global resources for the test suite.
     * This method can be overridden by subclasses to provide custom initialization.
     */
    @Step("Initialize global resources")
    protected void initializeGlobalResources() {
        // Default implementation does nothing
    }

    /**
     * Initializes class-level resources for the test class.
     * This method can be overridden by subclasses to provide custom initialization.
     */
    @Step("Initialize class resources")
    protected void initializeClassResources() {
        // Default implementation does nothing
    }

    /**
     * Initializes test-specific resources for each test method.
     * This method can be overridden by subclasses to provide custom initialization.
     */
    @Step("Initialize test resources")
    protected void initializeTestResources() {
        // Default implementation does nothing
    }

    /**
     * Cleans up test-specific resources after each test method.
     * This method can be overridden by subclasses to provide custom cleanup.
     */
    @Step("Cleanup test resources")
    protected void cleanupTestResources() {
        // Default implementation does nothing
    }

    /**
     * Cleans up class-level resources after the test class.
     * This method can be overridden by subclasses to provide custom cleanup.
     */
    @Step("Cleanup class resources")
    protected void cleanupClassResources() {
        // Default implementation does nothing
    }

    /**
     * Cleans up global resources after the test suite.
     * This method can be overridden by subclasses to provide custom cleanup.
     */
    @Step("Cleanup global resources")
    protected void cleanupGlobalResources() {
        // Default implementation does nothing
    }

    /**
     * Logs the test result.
     * 
     * @param result The test result
     */
    private void logTestResult(ITestResult result) {
        String status;
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                status = "PASSED";
                break;
            case ITestResult.FAILURE:
                status = "FAILED";
                break;
            case ITestResult.SKIP:
                status = "SKIPPED";
                break;
            default:
                status = "UNKNOWN";
        }
        log.info("Test {} - {}", getTestMethodName(), status);

        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test failed with exception: ", result.getThrowable());
        }
    }

    /**
     * Gets the current test method name.
     * 
     * @return The test method name
     */
    protected String getTestMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }
}