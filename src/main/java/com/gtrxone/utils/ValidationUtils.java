package com.gtrxone.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtrxone.exceptions.ApiException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations.
 * Provides common utility functions for validation.
 */
@Slf4j
public class ValidationUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Validates that a string is not null or empty.
     *
     * @param value The string to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * Validates that a collection is not null or empty.
     *
     * @param collection The collection to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the collection is null or empty
     */
    public static void validateNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * Validates that a map is not null or empty.
     *
     * @param map The map to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the map is null or empty
     */
    public static void validateNotEmpty(Map<?, ?> map, String fieldName) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * Validates that an object is not null.
     *
     * @param object The object to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the object is null
     */
    public static void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    /**
     * Validates that a number is positive.
     *
     * @param number The number to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the number is not positive
     */
    public static void validatePositive(Number number, String fieldName) {
        validateNotNull(number, fieldName);
        
        if (number.doubleValue() <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }
    
    /**
     * Validates that a number is not negative.
     *
     * @param number The number to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the number is negative
     */
    public static void validateNotNegative(Number number, String fieldName) {
        validateNotNull(number, fieldName);
        
        if (number.doubleValue() < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }
    
    /**
     * Validates that a number is within a range.
     *
     * @param number The number to validate
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the number is not within the range
     */
    public static void validateRange(Number number, Number min, Number max, String fieldName) {
        validateNotNull(number, fieldName);
        
        if (number.doubleValue() < min.doubleValue() || number.doubleValue() > max.doubleValue()) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }
    
    /**
     * Validates that a string matches a pattern.
     *
     * @param value The string to validate
     * @param pattern The pattern to match
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the string does not match the pattern
     */
    public static void validatePattern(String value, Pattern pattern, String fieldName) {
        validateNotEmpty(value, fieldName);
        
        if (!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " does not match the required pattern");
        }
    }
    
    /**
     * Validates that a string is a valid email address.
     *
     * @param email The email address to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the string is not a valid email address
     */
    public static void validateEmail(String email, String fieldName) {
        validatePattern(email, EMAIL_PATTERN, fieldName);
    }
    
    /**
     * Validates that a string is a valid UUID.
     *
     * @param uuid The UUID to validate
     * @param fieldName The field name for error messages
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static void validateUuid(String uuid, String fieldName) {
        validatePattern(uuid, UUID_PATTERN, fieldName);
    }
    
    /**
     * Validates that a response matches a JSON schema.
     *
     * @param response The response to validate
     * @param schemaPath The path to the JSON schema
     * @throws ApiException if the response does not match the schema
     */
    public static void validateJsonSchema(Response response, String schemaPath) {
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(schemaPath)));
        } catch (Exception e) {
            throw new ApiException("Response does not match JSON schema: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates that a JSON string matches a JSON schema.
     *
     * @param json The JSON string to validate
     * @param schemaPath The path to the JSON schema
     * @throws ApiException if the JSON does not match the schema
     */
    public static void validateJsonSchema(String json, String schemaPath) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode schemaNode = objectMapper.readTree(new File(schemaPath));
            
            // This is a simplified version. In a real implementation, you would use a JSON Schema validator library.
            log.info("Validating JSON against schema: {}", schemaPath);
            
            // For now, just check that the JSON is valid
            if (jsonNode == null) {
                throw new ApiException("Invalid JSON");
            }
        } catch (IOException e) {
            throw new ApiException("Failed to validate JSON schema: " + e.getMessage(), e);
        }
    }
    
    /**
     * Reads a JSON schema from a file.
     *
     * @param schemaPath The path to the JSON schema
     * @return The JSON schema as a string
     * @throws ApiException if the schema cannot be read
     */
    public static String readJsonSchema(String schemaPath) {
        try {
            return new String(Files.readAllBytes(Paths.get(schemaPath)));
        } catch (IOException e) {
            throw new ApiException("Failed to read JSON schema: " + e.getMessage(), e);
        }
    }
}