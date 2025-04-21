package com.gtrxone.reporting;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for logging test method execution.
 * This class is registered in the testng.xml file and logs all test method executions.
 */
@Slf4j
public class TestExecutionLogger implements ITestListener {

    /**
     * Called when a test method starts.
     *
     * @param result The test result
     */
    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.info("TEST STARTED: {}.{}", className, methodName);
    }

    /**
     * Called when a test method succeeds.
     *
     * @param result The test result
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.info("TEST PASSED: {}.{}", className, methodName);
    }

    /**
     * Called when a test method fails.
     *
     * @param result The test result
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.info("TEST FAILED: {}.{}", className, methodName);
        if (result.getThrowable() != null) {
            log.error("Test failed with exception: ", result.getThrowable());
        }
    }

    /**
     * Called when a test method is skipped.
     *
     * @param result The test result
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.info("TEST SKIPPED: {}.{}", className, methodName);
    }

    /**
     * Called when a test method fails but is within success percentage.
     *
     * @param result The test result
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.info("TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: {}.{}", className, methodName);
    }

    /**
     * Called when a test method fails with a timeout.
     *
     * @param result The test result
     */
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.info("TEST FAILED WITH TIMEOUT: {}.{}", className, methodName);
    }

    /**
     * Called before any test method is run.
     *
     * @param context The test context
     */
    @Override
    public void onStart(ITestContext context) {
        log.info("TEST EXECUTION STARTED: {}", context.getName());
    }

    /**
     * Called after all test methods are run.
     *
     * @param context The test context
     */
    @Override
    public void onFinish(ITestContext context) {
        log.info("TEST EXECUTION FINISHED: {}", context.getName());
    }
}