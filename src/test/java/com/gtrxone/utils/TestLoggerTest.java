package com.gtrxone.utils;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Test class for TestLogger.
 */
public class TestLoggerTest {

    /**
     * Test that logTestStart logs the start of a test method.
     */
    @Test
    public void testLogTestStart() {
        // This test simply verifies that the method doesn't throw an exception
        TestLogger.logTestStart(TestLoggerTest.class, "testLogTestStart");
    }

    /**
     * Test that logTestEnd logs the end of a test method with class, method name, and status.
     */
    @Test
    public void testLogTestEndWithStatus() {
        // This test simply verifies that the method doesn't throw an exception
        TestLogger.logTestEnd(TestLoggerTest.class, "testLogTestEndWithStatus", "PASSED");
    }

    /**
     * Test that logTestEnd logs the end of a test method with class, method name, status, and throwable.
     */
    @Test
    public void testLogTestEndWithThrowable() {
        // This test simply verifies that the method doesn't throw an exception
        Exception exception = new RuntimeException("Test exception");
        TestLogger.logTestEnd(TestLoggerTest.class, "testLogTestEndWithThrowable", "FAILED", exception);
    }

    /**
     * Test that the private constructor throws an exception.
     */
    @Test
    public void testPrivateConstructor() {
        try {
            // Use reflection to access the private constructor
            java.lang.reflect.Constructor<TestLogger> constructor = TestLogger.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            Assert.fail("Expected IllegalStateException was not thrown");
        } catch (Exception e) {
            // Verify that the cause is IllegalStateException
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof IllegalStateException, "Expected IllegalStateException, but got " + cause.getClass().getName());
            Assert.assertEquals("Utility class", cause.getMessage(), "Unexpected exception message");
        }
    }

    /**
     * Test that getTestResultStatus returns the correct status string.
     */
    @Test
    public void testGetTestResultStatus() throws Exception {
        // Use reflection to access the private method
        java.lang.reflect.Method method = TestLogger.class.getDeclaredMethod("getTestResultStatus", org.testng.ITestResult.class);
        method.setAccessible(true);

        // Create a simple implementation of ITestResult for testing
        org.testng.ITestResult successResult = new SimpleTestResult(org.testng.ITestResult.SUCCESS);
        org.testng.ITestResult failureResult = new SimpleTestResult(org.testng.ITestResult.FAILURE);
        org.testng.ITestResult skippedResult = new SimpleTestResult(org.testng.ITestResult.SKIP);
        org.testng.ITestResult unknownResult = new SimpleTestResult(-1);

        // Invoke the method and verify the results
        Assert.assertEquals("PASSED", method.invoke(null, successResult), "Success status should be PASSED");
        Assert.assertEquals("FAILED", method.invoke(null, failureResult), "Failure status should be FAILED");
        Assert.assertEquals("SKIPPED", method.invoke(null, skippedResult), "Skipped status should be SKIPPED");
        Assert.assertEquals("UNKNOWN", method.invoke(null, unknownResult), "Unknown status should be UNKNOWN");
    }

    /**
     * Simple implementation of ITestResult for testing.
     */
    private static class SimpleTestResult implements org.testng.ITestResult {
        private final int status;

        public SimpleTestResult(int status) {
            this.status = status;
        }

        @Override
        public int getStatus() {
            return status;
        }

        // Implement only the methods needed for testing
        @Override public void setStatus(int status) { }
        @Override public org.testng.ITestNGMethod getMethod() { return null; }
        @Override public Object[] getParameters() { return new Object[0]; }
        @Override public void setParameters(Object[] parameters) { }
        @Override public org.testng.IClass getTestClass() { return null; }
        @Override public Throwable getThrowable() { return null; }
        @Override public void setThrowable(Throwable throwable) { }
        @Override public long getStartMillis() { return 0; }
        @Override public long getEndMillis() { return 0; }
        @Override public void setEndMillis(long millis) { }
        @Override public String getName() { return null; }
        @Override public boolean isSuccess() { return false; }
        @Override public String getHost() { return null; }
        @Override public Object getInstance() { return null; }
        @Override public Object[] getFactoryParameters() { return new Object[0]; }
        @Override public String getTestName() { return null; }
        @Override public String getInstanceName() { return null; }
        @Override public org.testng.ITestContext getTestContext() { return null; }
        @Override public void setTestName(String name) { }
        @Override public boolean wasRetried() { return false; }
        @Override public void setWasRetried(boolean wasRetried) { }
        @Override public int compareTo(org.testng.ITestResult o) { return 0; }
        @Override public Object getAttribute(String name) { return null; }
        @Override public void setAttribute(String name, Object value) { }
        @Override public java.util.Set<String> getAttributeNames() { return null; }
        @Override public Object removeAttribute(String name) { return null; }
        @Override public String id() { return "test-id"; }
    }
}
