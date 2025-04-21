package com.gtrxone.reporting;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.io.File;

/**
 * Test class for CustomReportListener.
 */
public class CustomReportListenerTest {

    /**
     * Test that the CustomReportListener can be instantiated.
     */
    @Test
    public void testCustomReportListenerInstantiation() {
        CustomReportListener listener = new CustomReportListener();
        Assert.assertNotNull(listener, "CustomReportListener should be instantiated");
    }

    /**
     * Test that the ReportFolderManager is properly initialized.
     */
    @Test
    public void testReportFolderManagerInitialization() {
        ReportFolderManager manager = ReportFolderManager.getInstance();
        Assert.assertNotNull(manager, "ReportFolderManager should be instantiated");

        String baseDir = manager.getBaseResultsDir();
        Assert.assertNotNull(baseDir, "Base results directory should not be null");

        File baseDirFile = new File(baseDir);
        Assert.assertTrue(baseDirFile.exists() || baseDirFile.mkdirs(), "Base results directory should exist or be created");
    }

    /**
     * Test that the current results directory is properly set.
     */
    @Test
    public void testCurrentResultsDirectory() {
        ReportFolderManager manager = ReportFolderManager.getInstance();
        String currentDir = manager.getCurrentResultsDir();
        Assert.assertNotNull(currentDir, "Current results directory should not be null");

        File currentDirFile = new File(currentDir);
        Assert.assertTrue(currentDirFile.exists() || currentDirFile.mkdirs(), "Current results directory should exist or be created");
    }

    /**
     * Test that the maximum report history is properly set.
     */
    @Test
    public void testMaxReportHistory() {
        ReportFolderManager manager = ReportFolderManager.getInstance();
        int maxHistory = manager.getMaxReportHistory();
        Assert.assertTrue(maxHistory > 0, "Maximum report history should be greater than 0");
    }
}
