package com.gtrxone.reporting;

import com.gtrxone.config.ConfigManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for test reporting and logging.
 * Implements the Observer pattern to listen to test execution events.
 */
@Slf4j
public class ReportManager {

    private static final ReportManager instance = new ReportManager();
    private final ConfigManager configManager;
    private final Map<String, Object> testData = new HashMap<>();
    private final boolean maskSensitiveData;
    private final String[] sensitiveFields = {"password", "token", "secret", "key", "auth", "credential"};

    /**
     * Private constructor to prevent direct instantiation.
     */
    private ReportManager() {
        this.configManager = ConfigManager.getInstance();
        this.maskSensitiveData = configManager.getBooleanProperty("reporting.maskSensitiveData", true);
    }

    /**
     * Gets the singleton instance of ReportManager.
     *
     * @return The singleton instance of ReportManager
     */
    public static ReportManager getInstance() {
        return instance;
    }

    /**
     * Logs a step in the test.
     *
     * @param stepName The step name
     * @param details The step details
     */
    public void logStep(String stepName, String details) {
        log.info("Step: {} - {}", stepName, details);
        Allure.step(stepName + ": " + details);
    }

    /**
     * Logs a request in the test report.
     *
     * @param url The request URL
     * @param method The request method
     * @param headers The request headers
     * @param body The request body
     */
    public void logRequest(String url, String method, Map<String, String> headers, String body) {
        log.info("Request URL: {}", url);
        log.info("Request Method: {}", method);
        log.info("Request Headers: {}", maskSensitiveData(headers));

        String maskedBody = maskSensitiveData(body);
        log.info("Request Body: {}", maskedBody);

        // Attach to Allure report
        Allure.addAttachment("Request", "application/json", maskedBody, "json");
    }

    /**
     * Logs a response in the test report.
     *
     * @param response The response
     */
    public void logResponse(Response response) {
        log.info("Response Status Code: {}", response.getStatusCode());
        log.info("Response Status Line: {}", response.getStatusLine());
        log.info("Response Headers: {}", response.getHeaders());

        String responseBody = response.getBody().asString();
        String maskedBody = maskSensitiveData(responseBody);
        log.info("Response Body: {}", maskedBody);

        // Attach to Allure report
        Allure.addAttachment("Response", "application/json", maskedBody, "json");
    }

    /**
     * Attaches a screenshot to the test report.
     *
     * @param screenshot The screenshot bytes
     * @param name The screenshot name
     */
    @Attachment(value = "{name}", type = "image/png")
    public byte[] attachScreenshot(byte[] screenshot, String name) {
        return screenshot;
    }

    /**
     * Attaches a text file to the test report.
     *
     * @param content The file content
     * @param name The file name
     * @param contentType The content type
     */
    public void attachTextFile(String content, String name, String contentType) {
        Allure.addAttachment(name, contentType, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), "txt");

        // Save to file system as well
        try {
            String fileName = getLogFileName(name);
            Files.write(Paths.get(fileName), content.getBytes(StandardCharsets.UTF_8));
            log.info("Text file saved to: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to save text file: {}", e.getMessage());
        }
    }

    /**
     * Attaches a binary file to the test report.
     *
     * @param content The file content
     * @param name The file name
     * @param contentType The content type
     */
    public void attachBinaryFile(byte[] content, String name, String contentType) {
        Allure.addAttachment(name, contentType, new ByteArrayInputStream(content), "bin");

        // Save to file system as well
        try {
            String fileName = getLogFileName(name);
            Files.write(Paths.get(fileName), content);
            log.info("Binary file saved to: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to save binary file: {}", e.getMessage());
        }
    }

    /**
     * Adds a parameter to the test report.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public void addParameter(String name, String value) {
        String maskedValue = maskSensitiveData(name, value);
        Allure.parameter(name, maskedValue);
    }

    /**
     * Adds a label to the test report.
     *
     * @param name The label name
     * @param value The label value
     */
    public void addLabel(String name, String value) {
        Allure.label(name, value);
    }

    /**
     * Adds a link to the test report.
     *
     * @param name The link name
     * @param url The link URL
     */
    public void addLink(String name, String url) {
        Allure.link(name, url);
    }

    /**
     * Adds test data to the test context.
     *
     * @param key The data key
     * @param value The data value
     */
    public void addTestData(String key, Object value) {
        testData.put(key, value);
    }

    /**
     * Gets test data from the test context.
     *
     * @param key The data key
     * @param <T> The data type
     * @return The data value
     */
    @SuppressWarnings("unchecked")
    public <T> T getTestData(String key) {
        return (T) testData.get(key);
    }

    /**
     * Clears all test data from the test context.
     */
    public void clearTestData() {
        testData.clear();
    }

    /**
     * Masks sensitive data in a string.
     *
     * @param input The input string
     * @return The masked string
     */
    private String maskSensitiveData(String input) {
        if (!maskSensitiveData || input == null || input.isEmpty()) {
            return input;
        }

        String result = input;
        for (String field : sensitiveFields) {
            result = result.replaceAll("\"" + field + "\"\\s*:\\s*\"[^\"]*\"", "\"" + field + "\":\"*****\"");
        }

        return result;
    }

    /**
     * Masks sensitive data in a map.
     *
     * @param headers The headers map
     * @return The masked map
     */
    private Map<String, String> maskSensitiveData(Map<String, String> headers) {
        if (!maskSensitiveData || headers == null || headers.isEmpty()) {
            return headers;
        }

        Map<String, String> maskedHeaders = new HashMap<>(headers);
        for (String field : sensitiveFields) {
            for (Map.Entry<String, String> entry : maskedHeaders.entrySet()) {
                if (entry.getKey().toLowerCase().contains(field.toLowerCase())) {
                    maskedHeaders.put(entry.getKey(), "*****");
                }
            }
        }

        return maskedHeaders;
    }

    /**
     * Masks sensitive data in a key-value pair.
     *
     * @param key The key
     * @param value The value
     * @return The masked value
     */
    private String maskSensitiveData(String key, String value) {
        if (!maskSensitiveData || value == null || value.isEmpty()) {
            return value;
        }

        for (String field : sensitiveFields) {
            if (key.toLowerCase().contains(field.toLowerCase())) {
                return "*****";
            }
        }

        return value;
    }


    /**
     * Gets a log file name.
     *
     * @param name The log name
     * @return The file name
     */
    private String getLogFileName(String name) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String directory = configManager.getProperty("reporting.logDir", "target" + File.separator + "logs");

        // Create directory if it doesn't exist
        try {
            Path path = Paths.get(directory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error("Failed to create log directory: {}", e.getMessage());
        }

        return directory + File.separator + name + "_" + timestamp + ".txt";
    }
}
