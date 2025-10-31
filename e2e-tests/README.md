# E2E Tests - Ecommerce Microservices Backend

This module contains comprehensive End-to-End (E2E) tests for the ecommerce microservices backend application. The tests validate complete user workflows and integration between microservices.

## Overview

### Test Structure

```
e2e-tests/
├── src/test/java/com/selimhorri/app/e2e/
│   ├── base/                           # Base classes
│   │   └── BaseE2ETest.java           # Base class with common setup
│   ├── flows/                          # Complete user flow tests
│   │   ├── UserFlowE2ETest.java       # User authentication & profile
│   │   ├── ProductFlowE2ETest.java    # Product catalog operations
│   │   ├── OrderFlowE2ETest.java      # Order management
│   │   └── FavouriteFlowE2ETest.java  # User favorites
│   ├── integration/                    # Service integration tests
│   │   ├── UserProductIntegrationTest.java           # User + Product
│   │   └── OrderPaymentShippingIntegrationTest.java  # Order + Payment + Shipping
│   ├── resilience/                     # Resilience & fault tolerance tests
│   │   └── CircuitBreakerE2ETest.java # Circuit breaker patterns
│   └── utils/                          # Utility classes
│       ├── ApiClient.java             # HTTP client for API calls
│       └── TestDataBuilder.java       # Test data generators
└── src/test/resources/
    └── application-test.yml           # Test configuration
```

## Prerequisites

1. **Java 11 or higher** installed
2. **Maven 3.6.3+** available
3. **All microservices running** on expected ports:
   - API Gateway: `http://localhost:8080`
   - User Service: `http://localhost:8700`
   - Product Service: `http://localhost:8500`
   - Order Service: `http://localhost:8300`
   - Payment Service: `http://localhost:8400`
   - Shipping Service: `http://localhost:8600`
   - Favourite Service: `http://localhost:8800`
   - Proxy Client: `http://localhost:8900`

## Running Tests

### Run all E2E tests
```bash
mvn clean test -Dgroups="e2e"
```

### Run specific test category

**Flow Tests Only:**
```bash
mvn clean test -Dgroups="e2e-flows"
```

**Integration Tests Only:**
```bash
mvn clean test -Dgroups="e2e-integration"
```

**Resilience Tests Only:**
```bash
mvn clean test -Dgroups="e2e-resilience"
```

### Run specific test class
```bash
mvn clean test -Dtest=UserFlowE2ETest
mvn clean test -Dtest=ProductFlowE2ETest
mvn clean test -Dtest=OrderFlowE2ETest
```

### Run specific test method
```bash
mvn clean test -Dtest=UserFlowE2ETest#testUserLogin
```

### Run with detailed logging
```bash
mvn clean test -Dgroups="e2e" -X
```

### Run in parallel
```bash
mvn clean test -Dgroups="e2e" -DparallelTestCount=4
```

## Test Categories

### 1. Flow Tests (flows package)

Tests complete user workflows:

- **UserFlowE2ETest**
  - User registration
  - User login
  - Get user profile
  - Update user profile
  - Invalid login attempts
  - Duplicate registration

- **ProductFlowE2ETest**
  - List all products
  - Get product details
  - Create product
  - Update product
  - Search products by name
  - Filter by price range
  - Delete product
  - Product pagination
  - Handle non-existent products

- **OrderFlowE2ETest**
  - List orders
  - Get order details
  - Create order
  - Update order status
  - Complete order flow (order → payment → shipping)
  - Get user orders
  - Cancel order
  - Order pagination

- **FavouriteFlowE2ETest**
  - Add product to favorites
  - Get user favorites
  - Get all favorites
  - Remove from favorites
  - Check favorite status
  - Handle duplicate favorites

### 2. Integration Tests (integration package)

Tests interactions between multiple services:

- **UserProductIntegrationTest**
  - Authenticated user browsing products
  - User adding products to favorites
  - User viewing and editing profile
  - User searching and viewing product details
  - Unauthorized access prevention
  - Data isolation between users

- **OrderPaymentShippingIntegrationTest**
  - Complete order processing (create → pay → ship)
  - Payment processing
  - Shipping creation
  - Order status tracking
  - Multiple orders independence
  - Order cancellation
  - Payment history
  - Shipping status tracking

### 3. Resilience Tests (resilience package)

Tests system resilience and fault tolerance:

- **CircuitBreakerE2ETest**
  - Service health checks
  - Request timeout handling
  - API Gateway availability
  - Service discovery
  - Graceful degradation
  - Retry mechanisms
  - Load distribution
  - Circuit breaker monitoring
  - Connection pool management
  - Error response consistency

## Test Data

The `TestDataBuilder` class provides factory methods for creating test data:

```java
// User data
TestDataBuilder.buildUserRegistration(username, email, password);
TestDataBuilder.buildUserLogin(username, password);
TestDataBuilder.buildUserProfileUpdate(fullName, phoneNumber, address);

// Product data
TestDataBuilder.buildProductCreation(name, description, price);
TestDataBuilder.buildProductUpdate(name, description, price);

// Order data
TestDataBuilder.buildOrderCreation(userId, productId, quantity);
TestDataBuilder.buildOrderStatusUpdate(status);

// Payment data
TestDataBuilder.buildPaymentRequest(orderId, amount);

// Shipping data
TestDataBuilder.buildShippingRequest(orderId, address);

// Favorite data
TestDataBuilder.buildFavoriteAddition(productId);
```

## API Client

The `ApiClient` class provides a fluent API for making HTTP requests:

```java
ApiClient client = new ApiClient("http://localhost:8080");

// Simple GET
Response response = client.get("/api/user-service/users");

// GET with path parameters
Response response = client.get("/api/user-service/users/{id}", userId);

// POST with body
Response response = client.post("/api/user-service/users", userData);

// PUT with body and path parameters
Response response = client.put("/api/user-service/users/{id}", updateData, userId);

// DELETE
Response response = client.delete("/api/user-service/users/{id}", userId);

// Authentication
client.setAuthToken(token);
client.clearAuthToken();
```

## Base Test Class

All E2E tests extend `BaseE2ETest` which provides:

- Pre-configured `ApiClient` instances for all services
- Service URL constants
- Common setup/teardown
- Utility methods for getting clients by service name
- Auth token management

## Assertions

Tests use JUnit 5 assertions:

```java
assertEquals(expectedValue, actualValue);
assertNotNull(value);
assertTrue(condition);
assertFalse(condition);
assertNotEquals(value1, value2);
```

## Reporting

### Generate Allure Reports

Install Allure:
```bash
# On Windows
choco install allure

# On macOS
brew install allure

# On Linux
sudo apt-add-repository ppa:qameta/allure
sudo apt-get update
sudo apt-get install allure
```

Generate report after tests:
```bash
mvn allure:report
```

View report:
```bash
allure open target/site/allure-report
```

### Generate standard reports
```bash
mvn surefire-report:report
```

View report at: `target/site/surefire-report.html`

## Configuration

### Service URLs

Edit `BaseE2ETest.java` to change service URLs:

```java
protected static final String API_GATEWAY_URL = "http://localhost:8080";
protected static final String USER_SERVICE_URL = "http://localhost:8700";
// ... other services
```

Or use `application-test.yml`:

```yaml
services:
  api-gateway:
    url: http://localhost:8080
  user-service:
    url: http://localhost:8700
```

### Logging

Adjust logging level in `application-test.yml`:

```yaml
logging:
  level:
    root: INFO
    com.selimhorri.app: DEBUG
```

## Best Practices

1. **Use unique data**: Tests use timestamps to create unique test data
2. **Cleanup**: Tests should clean up their own data when possible
3. **Assertions**: Always verify both success status and response content
4. **Logging**: Each test logs its actions for debugging
5. **Isolation**: Tests should be independent and not rely on execution order
6. **Tags**: Use tags to organize and run specific test categories
7. **Allure annotations**: Use @Epic, @Feature, @Story for better reporting
8. **Error handling**: Handle test environment variations gracefully

## Troubleshooting

### Services not responding
- Verify all services are running on expected ports
- Check service logs for errors
- Ensure Docker containers are running if using Docker Compose

### Test failures
- Check service logs for error messages
- Verify test data prerequisites exist
- Check network connectivity between services
- Review API response in test output

### Timeout errors
- Increase timeout in `BaseE2ETest`
- Check service performance
- Verify network latency

## CI/CD Integration

### GitHub Actions

Add to your workflow:

```yaml
- name: Run E2E Tests
  run: mvn clean test -Dgroups="e2e" -DfailIfNoTests=false

- name: Upload Allure Results
  if: always()
  uses: actions/upload-artifact@v2
  with:
    name: allure-results
    path: target/allure-results
```

### Jenkins

```groovy
stage('E2E Tests') {
  steps {
    sh 'mvn clean test -Dgroups="e2e"'
  }
}
```

## Contributing

When adding new E2E tests:

1. Follow the existing package structure
2. Extend `BaseE2ETest`
3. Use `TestDataBuilder` for test data
4. Add Allure annotations
5. Include proper logging
6. Test both success and failure cases
7. Handle test environment variations
8. Update this README if needed

## License

Same as parent project
