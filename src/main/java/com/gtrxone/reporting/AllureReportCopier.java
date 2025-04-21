/*
 * Proprietary and Confidential
 *
 * Copyright (c) 2025 R N W Gunawardana. All Rights Reserved.
 *
 * This software and its documentation are proprietary and confidential information of R N W Gunawardana.
 * No part of this software, including but not limited to the source code, documentation, specifications,
 * and design, may be reproduced, stored in a retrieval system, transmitted in any form or by any means,
 * or distributed in any way without the explicit prior written permission of R N W Gunawardana.
 *
 * UNAUTHORIZED COPYING, REPRODUCTION, MODIFICATION, DISTRIBUTION, OR USE OF THIS SOFTWARE,
 * VIA ANY MEDIUM, IS STRICTLY PROHIBITED.
 *
 * The receipt or possession of this software does not convey any rights to use, modify, or distribute it.
 * Use of this software is subject to a valid license agreement with R N W Gunawardana.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING, BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIMS,
 * DAMAGES OR OTHER LIABILITIES ARISING FROM THE USE OF THIS SOFTWARE.
 *
 * AI Training Restriction Notice:
 * Use of this codebase for training artificial intelligence and machine learning models is strictly
 * prohibited without explicit written permission from R N W Gunawardana. Any unauthorized use for
 * AI/ML training purposes will be considered a violation of intellectual property rights and may
 * result in legal action.
 */
package com.gtrxone.reporting;

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
 * Utility class for copying Allure reports to custom directories.
 * This class provides a simple way to copy Allure results to a custom directory.
 */
@Slf4j
public class AllureReportCopier {

    /**
     * Private constructor to prevent instantiation.
     */
    private AllureReportCopier() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Copies Allure results to a custom directory.
     * Creates a timestamped folder within the custom directory and copies all Allure results there.
     * Also cleans up old report folders if the number of folders exceeds the configured maximum.
     *
     * @param customDir The custom directory to copy results to
     * @return The path to the directory where results were copied
     */
    public static String copyAllureResults(String customDir) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String targetDir = customDir + File.separator + timestamp;

        try {
            // Create the target directory
            Path targetPath = Paths.get(targetDir);
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
                log.info("Created target directory: {}", targetDir);
            }

            // Copy Allure results
            String sourceDir = "target/allure-results";
            File source = new File(sourceDir);
            if (!source.exists() || !source.isDirectory()) {
                log.warn("Source directory not found: {}", sourceDir);
                return null;
            }

            // Copy all files from source to target
            for (File file : source.listFiles()) {
                if (file.isFile()) {
                    FileUtils.copyFileToDirectory(file, new File(targetDir));
                }
            }

            log.info("Copied Allure results from {} to {}", sourceDir, targetDir);

            // Clean up old report folders
            cleanupOldReports(customDir);

            return targetDir;
        } catch (IOException e) {
            log.error("Failed to copy Allure results: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Cleans up old report folders to maintain the configured history limit.
     *
     * @param baseDir The base directory containing the report folders
     */
    private static void cleanupOldReports(String baseDir) {
        // Get the maximum number of report folders to keep from ReportFolderManager
        int maxReportHistory = ReportFolderManager.getInstance().getMaxReportHistory();

        if (maxReportHistory <= 0) {
            return;
        }

        try {
            File baseDirFile = new File(baseDir);
            if (!baseDirFile.exists() || !baseDirFile.isDirectory()) {
                return;
            }

            // Get all timestamped directories
            List<File> reportDirs = Arrays.stream(baseDirFile.listFiles())
                    .filter(File::isDirectory)
                    .sorted(Comparator.comparing(File::lastModified).reversed())
                    .collect(Collectors.toList());

            // Delete oldest directories if we have more than the max history
            if (reportDirs.size() > maxReportHistory) {
                log.info("Found {} report directories, keeping {} most recent", reportDirs.size(), maxReportHistory);
                for (int i = maxReportHistory; i < reportDirs.size(); i++) {
                    File dirToDelete = reportDirs.get(i);
                    try {
                        FileUtils.deleteDirectory(dirToDelete);
                        log.info("Deleted old report directory: {}", dirToDelete.getAbsolutePath());
                    } catch (IOException e) {
                        log.error("Failed to delete old report directory {}: {}", 
                                dirToDelete.getAbsolutePath(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during cleanup of old reports: {}", e.getMessage());
        }
    }

    /**
     * Copies Allure results to a custom directory.
     * Uses the default custom directory from config.properties.
     *
     * @return The path to the directory where results were copied
     */
    public static String copyAllureResults() {
        // Get the custom directory from ReportFolderManager
        String customDir = ReportFolderManager.getInstance().getBaseResultsDir();
        return copyAllureResults(customDir);
    }

    /**
     * Main method for running the copier directly.
     * This is useful for debugging or running the copier from the command line.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        String resultDir = copyAllureResults();
        if (resultDir != null) {
            log.info("Allure results copied to: {}", resultDir);
            log.info("To view the report, run: allure serve {}", resultDir);
        } else {
            log.error("Failed to copy Allure results");
        }
    }
}
