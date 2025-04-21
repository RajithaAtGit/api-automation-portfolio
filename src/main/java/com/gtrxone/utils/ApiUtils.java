package com.gtrxone.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtrxone.exceptions.ApiException;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Utility class for API testing.
 * Provides common utility functions for API testing.
 */
@Slf4j
public class ApiUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Private constructor to prevent instantiation.
     */
    private ApiUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts an object to a JSON string.
     *
     * @param object The object to convert
     * @return The JSON string
     * @throws ApiException if the conversion fails
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ApiException("Failed to convert object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object.
     *
     * @param json The JSON string
     * @param clazz The class to convert to
     * @param <T> The type of the object
     * @return The object
     * @throws ApiException if the conversion fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new ApiException("Failed to convert JSON to object", e);
        }
    }

    /**
     * Converts a JSON string to a map.
     *
     * @param json The JSON string
     * @return The map
     * @throws ApiException if the conversion fails
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new ApiException("Failed to convert JSON to map", e);
        }
    }

    /**
     * Converts a map to a JSON string.
     *
     * @param map The map
     * @return The JSON string
     * @throws ApiException if the conversion fails
     */
    public static String mapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new ApiException("Failed to convert map to JSON", e);
        }
    }

    /**
     * Parses a JSON string to a JsonNode.
     *
     * @param json The JSON string
     * @return The JsonNode
     * @throws ApiException if the parsing fails
     */
    public static JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new ApiException("Failed to parse JSON", e);
        }
    }

    /**
     * Creates an Awaitility condition factory with default settings.
     *
     * @return The condition factory
     */
    public static ConditionFactory await() {
        return Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(2))
                .pollDelay(Duration.ofMillis(100));
    }

    /**
     * Creates an Awaitility condition factory with custom settings.
     *
     * @param timeoutSeconds The timeout in seconds
     * @param pollIntervalSeconds The poll interval in seconds
     * @return The condition factory
     */
    public static ConditionFactory await(int timeoutSeconds, int pollIntervalSeconds) {
        return Awaitility.await()
                .atMost(Duration.ofSeconds(timeoutSeconds))
                .pollInterval(Duration.ofSeconds(pollIntervalSeconds))
                .pollDelay(Duration.ofMillis(100));
    }

    /**
     * Polls until the condition is met.
     *
     * @param callable The callable to execute
     * @param predicate The predicate to check
     * @param <T> The type of the result
     * @return The result
     */
    public static <T> T pollUntil(Callable<T> callable, Predicate<T> predicate) {
        return await().until(callable, predicate);
    }

    /**
     * Polls until the condition is met with custom settings.
     *
     * @param callable The callable to execute
     * @param predicate The predicate to check
     * @param timeoutSeconds The timeout in seconds
     * @param pollIntervalSeconds The poll interval in seconds
     * @param <T> The type of the result
     * @return The result
     */
    public static <T> T pollUntil(Callable<T> callable, Predicate<T> predicate, int timeoutSeconds, int pollIntervalSeconds) {
        return await(timeoutSeconds, pollIntervalSeconds).until(callable, predicate);
    }

    /**
     * Retries an operation until it succeeds or the maximum number of retries is reached.
     *
     * @param operation The operation to retry
     * @param maxRetries The maximum number of retries
     * @param retryDelayMs The delay between retries in milliseconds
     * @param <T> The type of the result
     * @return The result
     * @throws ApiException if the operation fails after all retries
     */
    public static <T> T retry(Callable<T> operation, int maxRetries, long retryDelayMs) {
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount <= maxRetries) {
            try {
                if (retryCount > 0) {
                    log.info("Retrying operation (attempt {}/{})", retryCount, maxRetries);
                    Thread.sleep(retryDelayMs);
                }

                return operation.call();

            } catch (Exception e) {
                lastException = e;
                log.error("Operation failed with exception: {}", e.getMessage());
                retryCount++;
            }
        }

        throw new ApiException("Operation failed after " + maxRetries + " retries", lastException);
    }

    /**
     * Checks if a response is successful (status code 2xx).
     *
     * @param response The response to check
     * @return True if the response is successful, false otherwise
     */
    public static boolean isSuccessful(Response response) {
        int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Checks if a response is a client error (status code 4xx).
     *
     * @param response The response to check
     * @return True if the response is a client error, false otherwise
     */
    public static boolean isClientError(Response response) {
        int statusCode = response.getStatusCode();
        return statusCode >= 400 && statusCode < 500;
    }

    /**
     * Checks if a response is a server error (status code 5xx).
     *
     * @param response The response to check
     * @return True if the response is a server error, false otherwise
     */
    public static boolean isServerError(Response response) {
        int statusCode = response.getStatusCode();
        return statusCode >= 500 && statusCode < 600;
    }
}
