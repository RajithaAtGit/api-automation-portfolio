package com.gtrxone.utils;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;

/**
 * Utility class for logging test method execution.
 * This class provides static methods for logging test method execution.
 */
@Slf4j
public class TestLogger {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestLogger() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Logs the start of a test method.
     *
     * @param testClass The test class
     * @param methodName The method name
     */
    public static void logTestStart(Class<?> testClass, String methodName) {
        log.info("TEST STARTED: {}.{}", testClass.getName(), methodName);
    }

    /**
     * Logs the end of a test method.
     *
     * @param testClass The test class
     * @param methodName The method name
     * @param status The test status (PASSED, FAILED, SKIPPED)
     */
    public static void logTestEnd(Class<?> testClass, String methodName, String status) {
        log.info("TEST COMPLETED: {}.{} - {}", testClass.getName(), methodName, status);
    }

    /**
     * Logs the end of a test method.
     *
     * @param testClass The test class
     * @param methodName The method name
     * @param status The test status (PASSED, FAILED, SKIPPED)
     * @param throwable The exception that caused the test to fail (if any)
     */
    public static void logTestEnd(Class<?> testClass, String methodName, String status, Throwable throwable) {
        log.info("TEST COMPLETED: {}.{} - {}", testClass.getName(), methodName, status);
        if (throwable != null) {
            log.error("Test failed with exception: ", throwable);
        }
    }

    /**
     * Logs the end of a test method based on the test result.
     *
     * @param result The test result
     */
    public static void logTestEnd(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String status = getTestResultStatus(result);
        log.info("TEST COMPLETED: {}.{} - {}", className, methodName, status);
        if (result.getThrowable() != null) {
            log.error("Test failed with exception: ", result.getThrowable());
        }
    }

    /**
     * Gets the status of a test result as a string.
     *
     * @param result The test result
     * @return The status as a string
     */
    private static String getTestResultStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }
}