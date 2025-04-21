package com.gtrxone.services;

import com.gtrxone.core.BaseService;
import com.gtrxone.exceptions.ApiException;
import com.gtrxone.models.request.Product;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for interacting with the restful-api.dev API.
 * Provides methods for CRUD operations on products.
 */
@Slf4j
public class ProductService extends BaseService {

    private static final String BASE_ENDPOINT = "/objects";

    /**
     * Constructs a new ProductService.
     */
    public ProductService() {
        super();
        // Override the base URL from config.properties
        RestAssured.baseURI = "https://api.restful-api.dev";
        log.info("Initialized ProductService with base URL: {}", RestAssured.baseURI);
    }

    /**
     * Executes a request without authentication.
     *
     * @param requestFunction The function to execute the request
     * @return The response
     */
    protected Response executeRequestWithoutAuth(Function<RequestSpecification, Response> requestFunction) {
        int maxRetries = configManager.getIntProperty("api.maxRetries", 3);
        long retryDelayMs = configManager.getLongProperty("api.retryDelayMs", 1000);

        Response response = null;
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount <= maxRetries) {
            try {
                if (retryCount > 0) {
                    log.info("Retrying request (attempt {}/{})", retryCount, maxRetries);
                    Thread.sleep(retryDelayMs);
                }

                // Create a base request specification without authentication
                RequestSpecification requestSpec = RestAssured.given();

                response = requestFunction.apply(requestSpec);
                success = true;

            } catch (Exception e) {
                log.error("Request failed with exception: {}", e.getMessage());
                if (retryCount >= maxRetries) {
                    throw new ApiException("Request failed after " + maxRetries + " retries", e);
                }
                retryCount++;
            }
        }

        return response;
    }

    /**
     * Gets all products.
     *
     * @return The response containing all products
     */
    @Step("Get all products")
    public Response getAllProducts() {
        log.info("Getting all products");
        Response response = executeRequestWithoutAuth(requestSpec -> {
            return requestSpec.get(BASE_ENDPOINT);
        });

        // Log the response for debugging
        log.info("Response status code: {}", response.getStatusCode());
        log.info("Response body: {}", response.getBody().asString());

        return response;
    }

    /**
     * Gets products by IDs.
     *
     * @param ids The list of product IDs
     * @return The response containing the products with the specified IDs
     */
    @Step("Get products by IDs: {ids}")
    public Response getProductsByIds(List<Integer> ids) {
        log.info("Getting products by IDs: {}", ids);
        String idsParam = String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new));

        return executeRequestWithoutAuth(requestSpec -> {
            return requestSpec.queryParam("id", idsParam).get(BASE_ENDPOINT);
        });
    }

    /**
     * Gets a product by ID.
     *
     * @param id The product ID
     * @return The response containing the product with the specified ID
     */
    @Step("Get product by ID: {id}")
    public Response getProductById(int id) {
        log.info("Getting product by ID: {}", id);
        return executeRequestWithoutAuth(requestSpec -> {
            return requestSpec.get(BASE_ENDPOINT + "/" + id);
        });
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create
     * @return The response containing the created product
     */
    @Step("Create new product")
    public Response createProduct(Product product) {
        log.info("Creating new product: {}", product);
        return executeRequestWithoutAuth(requestSpec -> {
            return requestSpec
                    .header("Content-Type", "application/json")
                    .body(product)
                    .post(BASE_ENDPOINT);
        });
    }

    /**
     * Updates a product.
     *
     * @param id The product ID
     * @param product The updated product
     * @return The response containing the updated product
     */
    @Step("Update product with ID: {id}")
    public Response updateProduct(int id, Product product) {
        log.info("Updating product with ID {}: {}", id, product);
        return executeRequestWithoutAuth(requestSpec -> {
            return requestSpec
                    .header("Content-Type", "application/json")
                    .body(product)
                    .put(BASE_ENDPOINT + "/" + id);
        });
    }

    /**
     * Partially updates a product.
     *
     * @param id The product ID
     * @param updates The partial updates to apply
     * @return The response containing the updated product
     */
    @Step("Partially update product with ID: {id}")
    public Response partialUpdateProduct(int id, Map<String, Object> updates) {
        log.info("Partially updating product with ID {}: {}", id, updates);
        return executeRequestWithoutAuth(requestSpec -> {
            return requestSpec
                    .header("Content-Type", "application/json")
                    .body(updates)
                    .patch(BASE_ENDPOINT + "/" + id);
        });
    }

    /**
     * Deletes a product.
     *
     * @param id The product ID
     * @return The response indicating the result of the deletion
     */
    @Step("Delete product with ID: {id}")
    public Response deleteProduct(int id) {
        log.info("Deleting product with ID: {}", id);
        return executeRequestWithoutAuth(requestSpec -> {
            return requestSpec.delete(BASE_ENDPOINT + "/" + id);
        });
    }
}