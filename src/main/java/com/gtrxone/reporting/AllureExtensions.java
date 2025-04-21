package com.gtrxone.reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Custom extensions for Allure reporting.
 * Provides additional functionality for Allure reporting.
 */
@Slf4j
public class AllureExtensions {

    private static final AllureLifecycle lifecycle = Allure.getLifecycle();

    /**
     * Private constructor to prevent instantiation.
     */
    private AllureExtensions() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Logs a step in the Allure report.
     *
     * @param name The step name
     * @param status The step status
     */
    public static void logStep(String name, Status status) {
        String uuid = UUID.randomUUID().toString();

        StepResult result = new StepResult()
                .setName(name)
                .setStatus(status);

        lifecycle.startStep(uuid, result);
        lifecycle.stopStep(uuid);
    }

    /**
     * Logs a successful step in the Allure report.
     *
     * @param name The step name
     */
    public static void logSuccessStep(String name) {
        logStep(name, Status.PASSED);
    }

    /**
     * Logs a failed step in the Allure report.
     *
     * @param name The step name
     */
    public static void logFailedStep(String name) {
        logStep(name, Status.FAILED);
    }

    /**
     * Logs a skipped step in the Allure report.
     *
     * @param name The step name
     */
    public static void logSkippedStep(String name) {
        logStep(name, Status.SKIPPED);
    }

    /**
     * Logs a broken step in the Allure report.
     *
     * @param name The step name
     */
    public static void logBrokenStep(String name) {
        logStep(name, Status.BROKEN);
    }

    /**
     * Logs a request and response in the Allure report.
     *
     * @param url The request URL
     * @param method The request method
     * @param requestHeaders The request headers
     * @param requestBody The request body
     * @param response The response
     */
    public static void logRequestResponse(String url, String method, String requestHeaders, String requestBody, Response response) {
        String uuid = UUID.randomUUID().toString();

        StepResult result = new StepResult()
                .setName(method + " " + url);

        lifecycle.startStep(uuid, result);

        // Log request details
        Allure.addAttachment("Request URL", "text/plain", url);
        Allure.addAttachment("Request Method", "text/plain", method);
        Allure.addAttachment("Request Headers", "text/plain", requestHeaders);

        if (requestBody != null && !requestBody.isEmpty()) {
            Allure.addAttachment("Request Body", "application/json", requestBody, "json");
        }

        // Log response details
        if (response != null) {
            Allure.addAttachment("Response Status", "text/plain", response.getStatusLine());
            Allure.addAttachment("Response Headers", "text/plain", response.getHeaders().toString());

            String responseBody = response.getBody().asString();
            if (responseBody != null && !responseBody.isEmpty()) {
                Allure.addAttachment("Response Body", "application/json", responseBody, "json");
            }

            // Set step status based on response status code
            int statusCode = response.getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                result.setStatus(Status.PASSED);
            } else if (statusCode >= 400 && statusCode < 500) {
                result.setStatus(Status.FAILED);
            } else if (statusCode >= 500) {
                result.setStatus(Status.BROKEN);
            } else {
                result.setStatus(Status.PASSED);
            }
        } else {
            result.setStatus(Status.BROKEN);
        }

        lifecycle.updateStep(uuid, step -> {
            step.setStatus(result.getStatus());
            step.setName(result.getName());
        });
        lifecycle.stopStep(uuid);
    }

    /**
     * Creates a custom category for Allure reporting.
     *
     * @param name The category name
     * @param description The category description
     */
    public static void createCategory(String name, String description) {
        // This is a placeholder for future implementation
        // Allure 2 Java API doesn't provide a direct way to create categories programmatically
        // Categories are typically defined in allure-categories.json file
        log.info("Creating Allure category: {} - {}", name, description);
    }

    /**
     * Sets the test description in the Allure report.
     *
     * @param description The test description
     */
    public static void setDescription(String description) {
        Allure.description(description);
    }

    /**
     * Sets the test description HTML in the Allure report.
     *
     * @param descriptionHtml The test description HTML
     */
    public static void setDescriptionHtml(String descriptionHtml) {
        Allure.descriptionHtml(descriptionHtml);
    }

    /**
     * Adds a parameter to the Allure report.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public static void addParameter(String name, String value) {
        Allure.parameter(name, value);
    }

    /**
     * Adds a link to the Allure report.
     *
     * @param name The link name
     * @param url The link URL
     */
    public static void addLink(String name, String url) {
        Allure.link(name, url);
    }

    /**
     * Adds an issue link to the Allure report.
     *
     * @param issueId The issue ID
     */
    public static void addIssueLink(String issueId) {
        Allure.issue(issueId, "https://jira.example.com/browse/" + issueId);
    }

    /**
     * Adds a test case link to the Allure report.
     *
     * @param testCaseId The test case ID
     */
    public static void addTestCaseLink(String testCaseId) {
        Allure.tms(testCaseId, "https://testcase.example.com/browse/" + testCaseId);
    }
}
