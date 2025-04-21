package com.gtrxone.tests.restfulapi;

import com.gtrxone.core.BaseTest;
import com.gtrxone.models.request.Product;
import com.gtrxone.reporting.AllureExtensions;
import com.gtrxone.reporting.ReportManager;
import com.gtrxone.services.ProductService;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for ProductService.
 * Tests the product lifecycle flow on the restful-api.dev API.
 * The tests are executed in order to simulate a complete lifecycle:
 * 1. Create a product
 * 2. Get the product
 * 3. Update the product
 * 4. Partially update the product
 * 5. Delete the product
 * 6. Verify the product was deleted
 */
@Feature("Restful API Demo")
public class ProductServiceTest extends BaseTest {

    private ProductService productService;
    private ReportManager reportManager;
    private String productId; // Store product ID between test methods
    private Product product; // Store product data between test methods
    private Product updatedProduct; // Store updated product data between test methods

    @BeforeClass
    @Override
    protected void beforeClass() {
        super.beforeClass();
        productService = new ProductService();
        reportManager = ReportManager.getInstance();

        // Add test metadata
        AllureExtensions.setDescription("Tests for restful-api.dev API lifecycle flow");
        AllureExtensions.addParameter("Environment", "restful-api");

        // Initialize test data
        Product.ProductData productData = Product.ProductData.builder()
                .year(2023)
                .price(999.99)
                .cpuModel("Intel Core i7")
                .hardDiskSize("1 TB")
                .color("Silver")
                .build();

        product = Product.builder()
                .name("Flow Test Laptop")
                .data(productData)
                .build();

        Product.ProductData updatedProductData = Product.ProductData.builder()
                .year(2024)
                .price(1299.99)
                .cpuModel("Intel Core i9")
                .hardDiskSize("2 TB")
                .color("Space Gray")
                .build();

        updatedProduct = Product.builder()
                .name("Updated Flow Test Laptop")
                .data(updatedProductData)
                .build();
    }

    /**
     * Step 1: Create a new product via POST request
     */
    @Test(priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new product via POST request")
    @Story("Product Lifecycle Flow")
    public void testCreateProduct() {
        // Log test step
        reportManager.logStep("Create Product", "Creating a new product via POST request");

        // Execute the POST request
        Response createResponse = productService.createProduct(product);
        reportManager.logResponse(createResponse);

        // Verify response status code is 200 or 201
        int createStatusCode = createResponse.getStatusCode();
        Assert.assertTrue(createStatusCode == 200 || createStatusCode == 201, 
                "Expected status code 200 or 201, but got " + createStatusCode);

        // Store the returned ID for use in subsequent tests
        productId = createResponse.jsonPath().getString("id");
        Assert.assertNotNull(productId, "Product ID should not be null");
        reportManager.logStep("Store Product ID", "Stored product ID: " + productId);

        // Log success
        AllureExtensions.logSuccessStep("Successfully created a new product with ID: " + productId);
    }

    /**
     * Step 2: Get the product by ID to verify creation
     */
    @Test(priority = 2, dependsOnMethods = "testCreateProduct")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get the product by ID to verify creation")
    @Story("Product Lifecycle Flow")
    public void testGetProduct() {
        // Log test step
        reportManager.logStep("Verify Product Creation", "Getting product with ID: " + productId);

        // Use direct RestAssured call to handle string ID
        Response getResponse = io.restassured.RestAssured.given()
                .get("https://api.restful-api.dev/objects/" + productId);
        reportManager.logResponse(getResponse);

        // Verify response status code is 200
        Assert.assertEquals(getResponse.getStatusCode(), 200, "Expected status code 200 for GET request");

        // Verify the product data matches what we created
        Assert.assertEquals(getResponse.jsonPath().getString("name"), "Flow Test Laptop", 
                "Product name should match the created product");

        // Log success
        AllureExtensions.logSuccessStep("Successfully verified product creation with ID: " + productId);
    }

    /**
     * Step 3: Update the product via PUT request
     */
    @Test(priority = 3, dependsOnMethods = "testGetProduct")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update the product via PUT request")
    @Story("Product Lifecycle Flow")
    public void testUpdateProduct() {
        // Log test step
        reportManager.logStep("Update Product", "Updating product with ID: " + productId);

        // Execute the PUT request using direct RestAssured call
        Response putResponse = io.restassured.RestAssured.given()
                .header("Content-Type", "application/json")
                .body(updatedProduct)
                .put("https://api.restful-api.dev/objects/" + productId);
        reportManager.logResponse(putResponse);

        // Verify response status code is 200
        Assert.assertEquals(putResponse.getStatusCode(), 200, "Expected status code 200 for PUT request");

        // Verify the product data was updated
        Assert.assertEquals(putResponse.jsonPath().getString("name"), "Updated Flow Test Laptop", 
                "Product name should be updated");
        Assert.assertEquals(putResponse.jsonPath().getDouble("data.price"), 1299.99, 
                "Product price should be updated");

        // Log success
        AllureExtensions.logSuccessStep("Successfully updated product with ID: " + productId);
    }

    /**
     * Step 4: Partially update the product via PATCH request
     */
    @Test(priority = 4, dependsOnMethods = "testUpdateProduct")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Partially update the product via PATCH request")
    @Story("Product Lifecycle Flow")
    public void testPartialUpdateProduct() {
        // Log test step
        reportManager.logStep("Partially Update Product", "Partially updating product with ID: " + productId);

        // Create partial update data with only the name field
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Partially Updated Flow Test Laptop");

        // Execute the PATCH request using direct RestAssured call
        Response patchResponse = io.restassured.RestAssured.given()
                .header("Content-Type", "application/json")
                .body(updates)
                .patch("https://api.restful-api.dev/objects/" + productId);
        reportManager.logResponse(patchResponse);

        // Verify response status code is 200
        Assert.assertEquals(patchResponse.getStatusCode(), 200, "Expected status code 200 for PATCH request");

        // Verify the name was updated but other fields remain unchanged
        Assert.assertEquals(patchResponse.jsonPath().getString("name"), "Partially Updated Flow Test Laptop", 
                "Product name should be updated");
        Assert.assertEquals(patchResponse.jsonPath().getDouble("data.price"), 1299.99, 
                "Product price should remain unchanged");

        // Log success
        AllureExtensions.logSuccessStep("Successfully partially updated product with ID: " + productId);
    }

    /**
     * Step 5: Delete the product
     */
    @Test(priority = 5, dependsOnMethods = "testPartialUpdateProduct")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete the product")
    @Story("Product Lifecycle Flow")
    public void testDeleteProduct() {
        // Log test step
        reportManager.logStep("Delete Product", "Deleting product with ID: " + productId);

        // Execute the DELETE request using direct RestAssured call
        Response deleteResponse = io.restassured.RestAssured.given()
                .delete("https://api.restful-api.dev/objects/" + productId);
        reportManager.logResponse(deleteResponse);

        // Verify response status code is 200
        Assert.assertEquals(deleteResponse.getStatusCode(), 200, "Expected status code 200 for DELETE request");

        // Log success
        AllureExtensions.logSuccessStep("Successfully deleted product with ID: " + productId);
    }

    /**
     * Step 6: Verify the product was deleted
     */
    @Test(priority = 6, dependsOnMethods = "testDeleteProduct")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify the product was deleted")
    @Story("Product Lifecycle Flow")
    public void testVerifyDeletion() {
        // Log test step
        reportManager.logStep("Verify Deletion", "Attempting to get deleted product with ID: " + productId);

        // Execute the GET request using direct RestAssured call
        Response verifyDeleteResponse = io.restassured.RestAssured.given()
                .get("https://api.restful-api.dev/objects/" + productId);
        reportManager.logResponse(verifyDeleteResponse);

        // The API might return 404 Not Found or an empty response for a deleted product
        // For this test, we'll check that the response doesn't contain the product data
        String responseBody = verifyDeleteResponse.getBody().asString();
        Assert.assertFalse(responseBody.contains("Partially Updated Flow Test Laptop"), 
                "Response should not contain the deleted product name");

        // Log success
        AllureExtensions.logSuccessStep("Successfully verified product deletion with ID: " + productId);
    }
}
