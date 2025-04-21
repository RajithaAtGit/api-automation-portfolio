package com.gtrxone.reporting;

import com.gtrxone.config.ConfigManager;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener for enhanced reporting.
 * Implements both ISuiteListener and ITestListener to capture events at both suite and test levels.
 */
@Slf4j
public class CustomReportListener implements ISuiteListener, ITestListener {

    private final ReportFolderManager reportFolderManager;
    private final ConfigManager configManager;

    /**
     * Constructs a new CustomReportListener.
     */
    public CustomReportListener() {
        this.reportFolderManager = ReportFolderManager.getInstance();
        this.configManager = ConfigManager.getInstance();
        log.info("CustomReportListener initialized");
    }

    /**
     * Called when a test suite starts.
     *
     * @param suite The test suite
     */
    @Override
    public void onStart(ISuite suite) {
        log.info("Test suite starting: {}", suite.getName());
        log.info("Reports will be stored in: {}", reportFolderManager.getCurrentResultsDir());

        // Register shutdown hook to copy Allure results
        String allureResultsDir = configManager.getProperty("allure.results.directory", "target/allure-results");
        reportFolderManager.registerShutdownHook(allureResultsDir);
    }

    /**
     * Called when a test suite finishes.
     *
     * @param suite The test suite
     */
    @Override
    public void onFinish(ISuite suite) {
        log.info("Test suite finished: {}", suite.getName());

        // Copy Allure results to custom directory
        String allureResultsDir = configManager.getProperty("allure.results.directory", "target/allure-results");
        reportFolderManager.copyAllureResults(allureResultsDir);

        log.info("Allure results copied to: {}", reportFolderManager.getCurrentResultsDir());
        log.info("To view the report, run: allure serve {}", reportFolderManager.getCurrentResultsDir());
    }

    /**
     * Called when a test starts.
     *
     * @param result The test result
     */
    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.debug("Test starting: {}.{}", className, methodName);
    }

    /**
     * Called when a test succeeds.
     *
     * @param result The test result
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.debug("Test passed: {}.{}", className, methodName);
    }

    /**
     * Called when a test fails.
     *
     * @param result The test result
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.debug("Test failed: {}.{}", className, methodName);

        // Add failure details to Allure report
        if (result.getThrowable() != null) {
            Allure.addAttachment("Failure Details", result.getThrowable().toString());
        }
    }

    /**
     * Called when a test is skipped.
     *
     * @param result The test result
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.debug("Test skipped: {}.{}", className, methodName);
    }

    /**
     * Called when a test fails but is within success percentage.
     *
     * @param result The test result
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        log.debug("Test failed but within success percentage: {}.{}", className, methodName);
    }

    /**
     * Called when a test fails with a timeout.
     *
     * @param result The test result
     */
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }

    /**
     * Called when a test context starts.
     *
     * @param context The test context
     */
    @Override
    public void onStart(ITestContext context) {
        log.debug("Test context starting: {}", context.getName());
    }

    /**
     * Called when a test context finishes.
     *
     * @param context The test context
     */
    @Override
    public void onFinish(ITestContext context) {
        log.debug("Test context finished: {}", context.getName());

        // Log test execution summary
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;

        log.info("Test execution summary for {}: Total={}, Passed={}, Failed={}, Skipped={}", 
                context.getName(), total, passed, failed, skipped);
    }
}
