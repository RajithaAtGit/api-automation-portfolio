package com.gtrxone;

import com.gtrxone.reporting.AllureReportCopier;
import com.gtrxone.reporting.ReportFolderManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Test class for AllureReportCopier.
 */
public class AllureReportCopierTest {

    /**
     * Tests copying Allure results to a custom directory.
     */
    @Test
    public void testCopyAllureResults() {
        // Copy Allure results
        String resultDir = AllureReportCopier.copyAllureResults();

        // Verify that the results were copied
        Assert.assertNotNull(resultDir, "Result directory should not be null");

        File resultDirFile = new File(resultDir);
        Assert.assertTrue(resultDirFile.exists(), "Result directory should exist");
        Assert.assertTrue(resultDirFile.isDirectory(), "Result directory should be a directory");

        // Verify that files were copied
        File[] files = resultDirFile.listFiles();
        Assert.assertNotNull(files, "Files should not be null");
        Assert.assertTrue(files.length > 0, "Files should have been copied");

        System.out.println("Allure results copied to: " + resultDir);
        System.out.println("To view the report, run: allure serve " + resultDir);
    }

    /**
     * Tests the cleanup functionality by creating multiple reports and verifying that only
     * the configured maximum number of reports are kept.
     */
    @Test
    public void testCleanupOldReports() {
        // Get the maximum number of report folders to keep
        int maxReportHistory = ReportFolderManager.getInstance().getMaxReportHistory();

        // Create more reports than the configured maximum
        for (int i = 0; i < maxReportHistory + 2; i++) {
            AllureReportCopier.copyAllureResults();

            // Add a small delay to ensure different timestamps
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        // Verify that only the configured maximum number of reports are kept
        String baseDir = ReportFolderManager.getInstance().getBaseResultsDir();
        File baseDirFile = new File(baseDir);

        // Get all directories in the base directory
        File[] reportDirs = baseDirFile.listFiles(File::isDirectory);

        // Verify that the number of directories is less than or equal to the configured maximum
        Assert.assertNotNull(reportDirs, "Report directories should not be null");
        Assert.assertTrue(reportDirs.length <= maxReportHistory, 
                "Number of report directories (" + reportDirs.length + 
                ") should be less than or equal to the configured maximum (" + maxReportHistory + ")");

        System.out.println("Cleanup test passed. Found " + reportDirs.length + 
                " report directories, configured maximum is " + maxReportHistory);

        // Print the list of report directories
        System.out.println("Report directories:");
        Arrays.stream(reportDirs)
                .map(File::getName)
                .sorted()
                .forEach(name -> System.out.println("  - " + name));
    }
}
