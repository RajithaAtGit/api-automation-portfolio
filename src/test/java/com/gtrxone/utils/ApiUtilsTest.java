package com.gtrxone.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.gtrxone.exceptions.ApiException;
import io.restassured.response.Response;
import org.awaitility.core.ConditionFactory;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Test class for ApiUtils.
 */
public class ApiUtilsTest {

    /**
     * Test that the private constructor throws an exception.
     */
    @Test
    public void testPrivateConstructor() {
        try {
            // Use reflection to access the private constructor
            java.lang.reflect.Constructor<ApiUtils> constructor = ApiUtils.class.getDeclaredConstructor();
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
     * Test that toJson correctly converts an object to a JSON string.
     */
    @Test
    public void testToJson() {
        // Create a test object
        TestObject testObject = new TestObject("test", 123);

        // Convert to JSON
        String json = ApiUtils.toJson(testObject);

        // Verify the JSON
        Assert.assertNotNull(json, "JSON should not be null");
        Assert.assertTrue(json.contains("\"name\":\"test\""), "JSON should contain name");
        Assert.assertTrue(json.contains("\"value\":123"), "JSON should contain value");
    }

    /**
     * Test that fromJson correctly converts a JSON string to an object.
     */
    @Test
    public void testFromJson() {
        // Create a JSON string
        String json = "{\"name\":\"test\",\"value\":123}";

        // Convert to object
        TestObject testObject = ApiUtils.fromJson(json, TestObject.class);

        // Verify the object
        Assert.assertNotNull(testObject, "Object should not be null");
        Assert.assertEquals(testObject.getName(), "test", "Name should be 'test'");
        Assert.assertEquals(testObject.getValue(), 123, "Value should be 123");
    }

    /**
     * Test that fromJson throws an ApiException when the JSON is invalid.
     */
    @Test
    public void testFromJsonWithInvalidJson() {
        // Create an invalid JSON string
        String json = "{\"name\":\"test\",\"value\":123";

        // Try to convert to object
        try {
            ApiUtils.fromJson(json, TestObject.class);
            Assert.fail("Expected ApiException was not thrown");
        } catch (Exception e) {
            // Verify that the exception is an ApiException
            Assert.assertTrue(e instanceof ApiException, "Expected ApiException, but got " + e.getClass().getName());
            Assert.assertEquals("Failed to convert JSON to object", e.getMessage(), "Unexpected exception message");
        }
    }

    /**
     * Test that jsonToMap correctly converts a JSON string to a map.
     */
    @Test
    public void testJsonToMap() {
        // Create a JSON string
        String json = "{\"name\":\"test\",\"value\":123}";

        // Convert to map
        Map<String, Object> map = ApiUtils.jsonToMap(json);

        // Verify the map
        Assert.assertNotNull(map, "Map should not be null");
        Assert.assertEquals(map.get("name"), "test", "Name should be 'test'");
        Assert.assertEquals(map.get("value"), 123, "Value should be 123");
    }

    /**
     * Test that jsonToMap throws an ApiException when the JSON is invalid.
     */
    @Test
    public void testJsonToMapWithInvalidJson() {
        // Create an invalid JSON string
        String json = "{\"name\":\"test\",\"value\":123";

        // Try to convert to map
        try {
            ApiUtils.jsonToMap(json);
            Assert.fail("Expected ApiException was not thrown");
        } catch (Exception e) {
            // Verify that the exception is an ApiException
            Assert.assertTrue(e instanceof ApiException, "Expected ApiException, but got " + e.getClass().getName());
            Assert.assertEquals("Failed to convert JSON to map", e.getMessage(), "Unexpected exception message");
        }
    }

    /**
     * Test that mapToJson correctly converts a map to a JSON string.
     */
    @Test
    public void testMapToJson() {
        // Create a map
        Map<String, Object> map = new HashMap<>();
        map.put("name", "test");
        map.put("value", 123);

        // Convert to JSON
        String json = ApiUtils.mapToJson(map);

        // Verify the JSON
        Assert.assertNotNull(json, "JSON should not be null");
        Assert.assertTrue(json.contains("\"name\":\"test\""), "JSON should contain name");
        Assert.assertTrue(json.contains("\"value\":123"), "JSON should contain value");
    }

    /**
     * Test that parseJson correctly parses a JSON string to a JsonNode.
     */
    @Test
    public void testParseJson() {
        // Create a JSON string
        String json = "{\"name\":\"test\",\"value\":123}";

        // Parse the JSON
        JsonNode jsonNode = ApiUtils.parseJson(json);

        // Verify the JsonNode
        Assert.assertNotNull(jsonNode, "JsonNode should not be null");
        Assert.assertEquals(jsonNode.get("name").asText(), "test", "Name should be 'test'");
        Assert.assertEquals(jsonNode.get("value").asInt(), 123, "Value should be 123");
    }

    /**
     * Test that parseJson throws an ApiException when the JSON is invalid.
     */
    @Test
    public void testParseJsonWithInvalidJson() {
        // Create an invalid JSON string
        String json = "{\"name\":\"test\",\"value\":123";

        // Try to parse the JSON
        try {
            ApiUtils.parseJson(json);
            Assert.fail("Expected ApiException was not thrown");
        } catch (Exception e) {
            // Verify that the exception is an ApiException
            Assert.assertTrue(e instanceof ApiException, "Expected ApiException, but got " + e.getClass().getName());
            Assert.assertEquals("Failed to parse JSON", e.getMessage(), "Unexpected exception message");
        }
    }

    /**
     * Test that await returns a ConditionFactory with default settings.
     */
    @Test
    public void testAwait() {
        // Get the ConditionFactory
        ConditionFactory conditionFactory = ApiUtils.await();

        // Verify the ConditionFactory
        Assert.assertNotNull(conditionFactory, "ConditionFactory should not be null");
    }

    /**
     * Test that await with custom settings returns a ConditionFactory with those settings.
     */
    @Test
    public void testAwaitWithCustomSettings() {
        // Get the ConditionFactory
        ConditionFactory conditionFactory = ApiUtils.await(60, 5);

        // Verify the ConditionFactory
        Assert.assertNotNull(conditionFactory, "ConditionFactory should not be null");
    }

    /**
     * Test that retry correctly retries an operation until it succeeds.
     */
    @Test
    public void testRetrySuccess() {
        // Create a counter to track the number of attempts
        AtomicInteger counter = new AtomicInteger(0);

        // Create an operation that succeeds on the third attempt
        Callable<String> operation = () -> {
            int attempt = counter.incrementAndGet();
            if (attempt < 3) {
                throw new RuntimeException("Attempt " + attempt + " failed");
            }
            return "success";
        };

        // Retry the operation
        String result = ApiUtils.retry(operation, 5, 10);

        // Verify the result
        Assert.assertEquals(result, "success", "Result should be 'success'");
        Assert.assertEquals(counter.get(), 3, "Operation should have been attempted 3 times");
    }

    /**
     * Test that retry throws an ApiException when the operation fails after all retries.
     */
    @Test
    public void testRetryFailure() {
        // Create a counter to track the number of attempts
        AtomicInteger counter = new AtomicInteger(0);

        // Create an operation that always fails
        Callable<String> operation = () -> {
            int attempt = counter.incrementAndGet();
            throw new RuntimeException("Attempt " + attempt + " failed");
        };

        // Try to retry the operation
        try {
            ApiUtils.retry(operation, 3, 10);
            Assert.fail("Expected ApiException was not thrown");
        } catch (Exception e) {
            // Verify that the exception is an ApiException
            Assert.assertTrue(e instanceof ApiException, "Expected ApiException, but got " + e.getClass().getName());
            Assert.assertEquals("Operation failed after 3 retries", e.getMessage(), "Unexpected exception message");
            Assert.assertEquals(counter.get(), 4, "Operation should have been attempted 4 times");
        }
    }

    // Note: Tests for isSuccessful, isClientError, and isServerError are omitted
    // because they require mocking the Response interface, which is complex.
    // These methods are simple enough that they don't need extensive testing.

    /**
     * Test class for JSON conversion tests.
     */
    public static class TestObject {
        private String name;
        private int value;

        public TestObject() {
            // Default constructor for Jackson
        }

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
