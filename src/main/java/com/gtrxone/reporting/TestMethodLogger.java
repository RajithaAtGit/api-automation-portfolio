package com.gtrxone.reporting;

import lombok.extern.slf4j.Slf4j;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * TestNG listener for logging test method execution.
 * This class is registered in the testng.xml file and logs all test method executions.
 */
@Slf4j
public class TestMethodLogger implements IInvokedMethodListener {

    /**
     * Called before a method is invoked.
     * If the method is a test method, logs its start.
     *
     * @param method The invoked method
     * @param testResult The test result
     */
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            String className = method.getTestMethod().getTestClass().getName();
            log.info("TEST STARTED: {}.{}", className, methodName);
        }
    }

    /**
     * Called after a method is invoked.
     * If the method is a test method, logs its completion.
     *
     * @param method The invoked method
     * @param testResult The test result
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            String className = method.getTestMethod().getTestClass().getName();
            String status = getTestResultStatus(testResult);
            
            log.info("TEST {}: {}.{}", status, className, methodName);
            
            if (testResult.getStatus() == ITestResult.FAILURE && testResult.getThrowable() != null) {
                log.error("Test failed with exception: ", testResult.getThrowable());
            }
        }
    }

    /**
     * Gets the status of a test result as a string.
     *
     * @param result The test result
     * @return The status as a string
     */
    private String getTestResultStatus(ITestResult result) {
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