package com.gtrxone.reporting;

import com.gtrxone.utils.TestLogger;
import lombok.extern.slf4j.Slf4j;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * TestNG listener for processing test methods.
 * This class intercepts all test method invocations and logs their execution.
 */
@Slf4j
public class TestMethodProcessor implements IInvokedMethodListener {

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
            Class<?> testClass = method.getTestMethod().getTestClass().getRealClass();
            TestLogger.logTestStart(testClass, methodName);
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
            TestLogger.logTestEnd(testResult);
        }
    }
}