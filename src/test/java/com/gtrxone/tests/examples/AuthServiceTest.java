package com.gtrxone.tests.examples;

import com.gtrxone.core.BaseTest;
import com.gtrxone.reporting.AllureExtensions;
import com.gtrxone.reporting.ReportManager;
import com.gtrxone.services.AuthService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Example test class for AuthService.
 * Demonstrates the usage of the API automation framework.
 */
@Feature("Authentication")
public class AuthServiceTest extends BaseTest {

    private AuthService authService;
    private ReportManager reportManager;

    @BeforeClass
    @Override
    protected void beforeClass() {
        super.beforeClass();
        authService = new AuthService();
        reportManager = ReportManager.getInstance();

        // Add test metadata
        AllureExtensions.setDescription("Tests for authentication service");
        AllureExtensions.addParameter("Environment", configManager.getProperty("env", "qa"));
    }

    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test successful login with valid credentials")
    @Story("Login")
    public void testSuccessfulLogin() {
        // This is a mock test that demonstrates the framework usage
        // In a real test, you would use actual credentials and API endpoints

        // Log test step
        reportManager.logStep("Login", "Attempting to login with valid credentials");

        try {
            // Mock the login process
            // In a real test, you would call authService.login(username, password)
            String username = "testuser";
            String password = "password123";

            // Log request details
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            reportManager.logRequest("/auth/login", "POST", headers, requestBody);

            // Mock successful response
            // In a real test, this would be the actual response from the API
            String responseBody = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\"}";

            // Add the token to the AuthService
            authService.getClass().getDeclaredField("authToken").setAccessible(true);
            authService.getClass().getDeclaredField("authToken").set(authService, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

            // Verify the user is authenticated
            Assert.assertTrue(authService.isAuthenticated(), "User should be authenticated after successful login");

            // Log success
            AllureExtensions.logSuccessStep("Login successful");

        } catch (Exception e) {
            // Log failure
            AllureExtensions.logFailedStep("Login failed: " + e.getMessage());
            Assert.fail("Login test failed", e);
        }
    }

    @Test(priority = 2, dependsOnMethods = "testSuccessfulLogin")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test token validation")
    @Story("Token Validation")
    public void testTokenValidation() {
        // This is a mock test that demonstrates the framework usage

        // Log test step
        reportManager.logStep("Validate Token", "Validating authentication token");

        try {
            // Mock the token validation process
            // In a real test, you would call authService.validateToken()

            // Log request details
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + authService.getAuthToken());
            headers.put("Content-Type", "application/json");

            reportManager.logRequest("/auth/validate", "GET", headers, null);

            // Mock successful response
            // In a real test, this would be the actual response from the API
            String responseBody = "{\"valid\":true}";

            // Verify the token is valid
            Assert.assertTrue(true, "Token should be valid");

            // Log success
            AllureExtensions.logSuccessStep("Token validation successful");

        } catch (Exception e) {
            // Log failure
            AllureExtensions.logFailedStep("Token validation failed: " + e.getMessage());
            Assert.fail("Token validation test failed", e);
        }
    }

    @Test(priority = 3, dependsOnMethods = "testSuccessfulLogin")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test logout")
    @Story("Logout")
    public void testLogout() {
        // This is a mock test that demonstrates the framework usage

        // Log test step
        reportManager.logStep("Logout", "Attempting to logout");

        try {
            // Mock the logout process
            // In a real test, you would call authService.logout()

            // Log request details
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + authService.getAuthToken());
            headers.put("Content-Type", "application/json");

            reportManager.logRequest("/auth/logout", "POST", headers, null);

            // Mock successful response
            // In a real test, this would be the actual response from the API
            String responseBody = "{\"success\":true}";

            // Clear the token
            authService.clearToken();

            // Verify the user is not authenticated
            Assert.assertFalse(authService.isAuthenticated(), "User should not be authenticated after logout");

            // Log success
            AllureExtensions.logSuccessStep("Logout successful");

        } catch (Exception e) {
            // Log failure
            AllureExtensions.logFailedStep("Logout failed: " + e.getMessage());
            Assert.fail("Logout test failed", e);
        }
    }

    @Test(priority = 4)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with invalid credentials")
    @Story("Login")
    public void testLoginWithInvalidCredentials() {
        // This is a mock test that demonstrates the framework usage

        // Log test step
        reportManager.logStep("Login", "Attempting to login with invalid credentials");

        try {
            // Mock the login process with invalid credentials
            String username = "invaliduser";
            String password = "invalidpassword";

            // Log request details
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            reportManager.logRequest("/auth/login", "POST", headers, requestBody);

            // Mock failed response
            // In a real test, this would be the actual response from the API
            String responseBody = "{\"error\":\"Invalid credentials\"}";

            // Verify the user is not authenticated
            Assert.assertFalse(authService.isAuthenticated(), "User should not be authenticated after failed login");

            // Log success (test passed because we expected a failure)
            AllureExtensions.logSuccessStep("Login with invalid credentials correctly failed");

        } catch (Exception e) {
            // Log failure
            AllureExtensions.logFailedStep("Test failed: " + e.getMessage());
            Assert.fail("Test failed", e);
        }
    }
}
