package com.gtrxone.reporting;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Test class for TestMethodProcessor.
 */
public class TestMethodProcessorTest {

    /**
     * Test that the TestMethodProcessor can be instantiated.
     */
    @Test
    public void testTestMethodProcessorInstantiation() {
        TestMethodProcessor processor = new TestMethodProcessor();
        Assert.assertNotNull(processor, "TestMethodProcessor should be instantiated");
    }
}
