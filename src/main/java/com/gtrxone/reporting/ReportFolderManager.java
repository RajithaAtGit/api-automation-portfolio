package com.gtrxone.reporting;

import com.gtrxone.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager class for handling report folders.
 * Responsible for creating, cleaning up, and managing report folders.
 * Implements the Singleton pattern.
 */
@Slf4j
public class ReportFolderManager {

    private static volatile ReportFolderManager instance;
    private final ConfigManager configManager;
    private final String customResultsDir;
    private final boolean useTimestampedFolders;
    private final int maxReportHistory;
    private String currentResultsDir;

    /**
     * Private constructor to prevent direct instantiation.
     * Constructs a new ReportFolderManager.
     */
    private ReportFolderManager() {
        this.configManager = ConfigManager.getInstance();
        this.customResultsDir = configManager.getProperty("reporting.customResultsDir", ".");
        this.useTimestampedFolders = configManager.getBooleanProperty("reporting.useTimestampedFolders", true);
        this.maxReportHistory = configManager.getIntProperty("reporting.maxReportHistory", 10);

        // Create the custom results directory if it doesn't exist
        createCustomResultsDir();

        // Create the current results directory
        if (useTimestampedFolders) {
            this.currentResultsDir = createTimestampedResultsDir();
        } else {
            this.currentResultsDir = customResultsDir;
        }
    }

    /**
     * Gets the singleton instance of ReportFolderManager.
     * Uses double-checked locking for thread safety.
     *
     * @return The singleton instance of ReportFolderManager
     */
    public static ReportFolderManager getInstance() {
        if (instance == null) {
            synchronized (ReportFolderManager.class) {
                if (instance == null) {
                    instance = new ReportFolderManager();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the current results directory.
     *
     * @return The current results directory
     */
    public String getCurrentResultsDir() {
        return currentResultsDir;
    }

    /**
     * Gets the base results directory.
     *
     * @return The base results directory
     */
    public String getBaseResultsDir() {
        return customResultsDir;
    }

    /**
     * Gets the maximum number of report history folders to keep.
     *
     * @return The maximum number of report history folders
     */
    public int getMaxReportHistory() {
        return maxReportHistory;
    }

    /**
     * Creates the custom results directory if it doesn't exist.
     */
    private void createCustomResultsDir() {
        try {
            Path path = Paths.get(customResultsDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created custom results directory: {}", customResultsDir);
            }
        } catch (IOException e) {
            log.error("Failed to create custom results directory: {}", e.getMessage());
        }
    }

    /**
     * Creates a timestamped results directory.
     *
     * @return The path to the created directory
     */
    private String createTimestampedResultsDir() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String dirPath = customResultsDir + File.separator + timestamp;

        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created results directory: {}", dirPath);
            }
        } catch (IOException e) {
            log.error("Failed to create results directory: {}", e.getMessage());
            // Fallback to custom results directory
            return customResultsDir;
        }

        return dirPath;
    }

    /**
     * Copies Allure results from the default directory to the custom directory.
     *
     * @param sourceDir The source directory
     */
    public void copyAllureResults(String sourceDir) {
        try {
            File sourceDirectory = new File(sourceDir);
            File targetDirectory = new File(currentResultsDir);

            if (sourceDirectory.exists() && sourceDirectory.isDirectory()) {
                FileUtils.copyDirectory(sourceDirectory, targetDirectory);
                log.info("Copied Allure results from {} to {}", sourceDir, currentResultsDir);
            } else {
                log.warn("Source directory does not exist: {}", sourceDir);
            }

            // Clean up old report directories
            if (useTimestampedFolders) {
                cleanupOldReports();
            }
        } catch (IOException e) {
            log.error("Failed to copy Allure results: {}", e.getMessage());
        }
    }

    /**
     * Cleans up old report directories.
     */
    public void cleanupOldReports() {
        try {
            File customResultsDirFile = new File(customResultsDir);
            if (!customResultsDirFile.exists() || !customResultsDirFile.isDirectory()) {
                return;
            }

            // Get all timestamped directories
            List<File> reportDirs = Arrays.stream(customResultsDirFile.listFiles())
                    .filter(File::isDirectory)
                    .filter(dir -> dir.getName().matches("\\d{8}_\\d{6}"))
                    .sorted(Comparator.comparing(File::getName).reversed())
                    .collect(Collectors.toList());

            // Keep only the most recent directories
            if (reportDirs.size() > maxReportHistory) {
                for (int i = maxReportHistory; i < reportDirs.size(); i++) {
                    File dirToDelete = reportDirs.get(i);
                    FileUtils.deleteDirectory(dirToDelete);
                    log.info("Deleted old report directory: {}", dirToDelete.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            log.error("Failed to clean up old reports: {}", e.getMessage());
        }
    }

    /**
     * Registers a shutdown hook to copy Allure results when the JVM shuts down.
     *
     * @param sourceDir The source directory
     */
    public void registerShutdownHook(String sourceDir) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Copying Allure results...");
            copyAllureResults(sourceDir);
        }));
    }
}
