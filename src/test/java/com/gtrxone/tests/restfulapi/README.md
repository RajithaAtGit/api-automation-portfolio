# Restful API Tests

This package contains tests for the [restful-api.dev](https://restful-api.dev/) API, which demonstrates the capabilities of the API automation framework.

## API Overview

The restful-api.dev API provides a simple RESTful API for testing and demonstration purposes. It supports the following operations:

- **GET /objects** - List all objects
- **GET /objects?id=3&id=5&id=10** - List objects by IDs
- **GET /objects/{id}** - Get a single object
- **POST /objects** - Add a new object
- **PUT /objects/{id}** - Update an object
- **PATCH /objects/{id}** - Partially update an object
- **DELETE /objects/{id}** - Delete an object

## Test Cases

The `ProductServiceTest` class contains a series of test methods that simulate a complete product lifecycle flow. The tests are executed in order using TestNG's `dependsOnMethods` attribute to ensure that each step depends on the successful completion of the previous step.

The test methods are:

1. **testCreateProduct** - Creates a new product via POST request
2. **testGetProduct** - Gets the product by ID to verify creation
3. **testUpdateProduct** - Updates the product via PUT request
4. **testPartialUpdateProduct** - Partially updates the product via PATCH request
5. **testDeleteProduct** - Deletes the product
6. **testVerifyDeletion** - Verifies the product was deleted

### Product Lifecycle Flow

The product lifecycle flow is implemented as a series of separate test methods that execute in order. Each test method performs a specific step in the flow:

1. **testCreateProduct**: Creates a new object via POST request and stores the returned ID
2. **testGetProduct**: Sends a GET request using the stored ID to verify object creation
3. **testUpdateProduct**: Sends a PUT request to update the object
4. **testPartialUpdateProduct**: Sends a PATCH request to change the name field only
5. **testDeleteProduct**: Sends a DELETE request to delete the object
6. **testVerifyDeletion**: Verifies that the object was deleted by attempting to get it again

This approach ensures that the entire CRUD lifecycle works correctly and handles errors appropriately. It uses direct RestAssured calls to handle the string IDs returned by the API. The test methods share data through class-level variables, allowing the product ID to be passed between tests.

## Test Data

The tests use the following test data:

- **Product** - A model class representing a product with the following properties:
  - id: Integer
  - name: String
  - data: ProductData
    - year: Integer
    - price: Double
    - cpuModel: String
    - hardDiskSize: String
    - color: String

## Running the Tests

To run the tests, use the following Maven command:

```bash
mvn clean test -Denv=restful-api
```

This will run the tests using the restful-api environment configuration.

Alternatively, you can run the tests from your IDE by selecting the `ProductServiceTest` class and running it as a TestNG test.

## Test Results

The test results will be available in the Allure report. To view the report, run:

```bash
allure serve reporting
```

This will open the Allure report in your default browser.

## Dependencies

The tests depend on the following components:

- **ProductService** - A service class for interacting with the restful-api.dev API
- **Product** - A model class for representing products
- **BaseTest** - A base class for all test classes
- **ReportManager** - A utility class for managing test reports
- **AllureExtensions** - A utility class for extending Allure reporting capabilities

## Environment Configuration

The tests use the following environment configuration:

```properties
# API Configuration
api.baseUrl=https://api.restful-api.dev
api.timeout=30000
api.validateStatus=true

# Authentication Configuration
auth.defaultStrategy=none
```

This configuration is defined in the `src/test/resources/environments/restful-api.properties` file.
