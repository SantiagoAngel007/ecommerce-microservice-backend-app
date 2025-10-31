package com.selimhorri.app.e2e.flows;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.e2e.base.BaseE2ETest;
import com.selimhorri.app.e2e.utils.TestDataBuilder;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Epic("Product Catalog")
@Feature("Product Management")
@Tag("e2e")
@Tag("e2e-flows")
@DisplayName("Product Catalog Flow E2E Tests")
public class ProductFlowE2ETest extends BaseE2ETest {

	private static final String UNIQUE_SUFFIX = String.valueOf(System.currentTimeMillis());
	private static final String TEST_PRODUCT_NAME = "E2E Test Product " + UNIQUE_SUFFIX;
	private static final String TEST_PRODUCT_DESCRIPTION = "This is an E2E test product";
	private static final double TEST_PRODUCT_PRICE = 99.99;

	@BeforeEach
	public void setUp() {
		super.setUp();
		clearAuthTokens();
	}

	@Test
	@Story("List All Products")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test retrieving all products from the catalog")
	@DisplayName("Should retrieve all products successfully")
	public void testListAllProducts() {
		log.info("Testing list all products");

		Response response = productServiceClient.get("/api/product-service/products");

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertNotNull(response.body().asString(), "Response body should not be null");

		log.info("All products retrieved successfully");
	}

	@Test
	@Story("Get Product Details")
	@Severity(SeverityLevel.HIGH)
	@Description("Test retrieving product details by ID")
	@DisplayName("Should retrieve product details by ID")
	public void testGetProductById() {
		log.info("Testing get product by ID");

		// First, get all products to find an existing product ID
		Response listResponse = productServiceClient.get("/api/product-service/products");
		assertEquals(200, listResponse.getStatusCode());

		// Extract first product ID if available
		Long productId = listResponse.jsonPath().getLong("content[0].id");

		if (productId != null) {
			// Get specific product
			Response response = productServiceClient.get("/api/product-service/products/{id}", productId);

			// Verify response
			assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
			assertNotNull(response.jsonPath().getString("name"), "Product name should not be null");

			log.info("Product details retrieved successfully for ID: {}", productId);
		} else {
			log.warn("No products found in catalog, skipping product detail test");
		}
	}

	@Test
	@Story("Create Product")
	@Severity(SeverityLevel.HIGH)
	@Description("Test creating a new product")
	@DisplayName("Should create a new product successfully")
	public void testCreateProduct() {
		log.info("Testing create product");

		Map<String, Object> productData = TestDataBuilder.buildProductCreation(
			TEST_PRODUCT_NAME, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE);

		Response response = productServiceClient.post("/api/product-service/products", productData);

		// Verify response
		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300,
			"Expected 2xx status code for product creation");
		assertNotNull(response.body().asString(), "Response body should contain product data");

		log.info("Product created successfully");
	}

	@Test
	@Story("Update Product")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test updating product information")
	@DisplayName("Should update product successfully")
	public void testUpdateProduct() {
		log.info("Testing update product");

		// First create a product
		Map<String, Object> productData = TestDataBuilder.buildProductCreation(
			TEST_PRODUCT_NAME, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE);
		Response createResponse = productServiceClient.post("/api/product-service/products", productData);

		if (createResponse.getStatusCode() >= 200 && createResponse.getStatusCode() < 300) {
			Long productId = createResponse.jsonPath().getLong("id");

			// Update product
			Map<String, Object> updateData = TestDataBuilder.buildProductUpdate(
				"Updated Product Name", "Updated description", 149.99);
			Response updateResponse = productServiceClient.put("/api/product-service/products/{id}", updateData, productId);

			// Verify response
			assertEquals(200, updateResponse.getStatusCode(), "Expected 200 OK status code");

			log.info("Product updated successfully");
		}
	}

	@Test
	@Story("Search Products")
	@Severity(SeverityLevel.HIGH)
	@Description("Test searching products by name")
	@DisplayName("Should search products by name")
	public void testSearchProductsByName() {
		log.info("Testing search products by name");

		Response response = productServiceClient.get("/api/product-service/products/search?name=Test");

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertNotNull(response.body().asString(), "Response body should not be null");

		log.info("Product search completed successfully");
	}

	@Test
	@Story("Filter Products by Price")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test filtering products by price range")
	@DisplayName("Should filter products by price range")
	public void testFilterProductsByPrice() {
		log.info("Testing filter products by price");

		Response response = productServiceClient.get("/api/product-service/products?minPrice=10&maxPrice=100");

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");

		log.info("Product price filter completed successfully");
	}

	@Test
	@Story("Delete Product")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test deleting a product")
	@DisplayName("Should delete product successfully")
	public void testDeleteProduct() {
		log.info("Testing delete product");

		// First create a product
		Map<String, Object> productData = TestDataBuilder.buildProductCreation(
			TEST_PRODUCT_NAME, TEST_PRODUCT_DESCRIPTION, TEST_PRODUCT_PRICE);
		Response createResponse = productServiceClient.post("/api/product-service/products", productData);

		if (createResponse.getStatusCode() >= 200 && createResponse.getStatusCode() < 300) {
			Long productId = createResponse.jsonPath().getLong("id");

			// Delete product
			Response deleteResponse = productServiceClient.delete("/api/product-service/products/{id}", productId);

			// Verify response
			assertTrue(deleteResponse.getStatusCode() >= 200 && deleteResponse.getStatusCode() < 300,
				"Expected 2xx status code for product deletion");

			log.info("Product deleted successfully");
		}
	}

	@Test
	@Story("Product Pagination")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test product list pagination")
	@DisplayName("Should handle product pagination correctly")
	public void testProductPagination() {
		log.info("Testing product pagination");

		Response response = productServiceClient.get("/api/product-service/products?page=0&size=10");

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertNotNull(response.jsonPath().getString("content"), "Pagination content should not be null");

		log.info("Product pagination working correctly");
	}

	@Test
	@Story("Get Product Not Found")
	@Severity(SeverityLevel.LOW)
	@Description("Test retrieving non-existent product")
	@DisplayName("Should return 404 for non-existent product")
	public void testGetNonExistentProduct() {
		log.info("Testing get non-existent product");

		Long nonExistentId = 999999L;
		Response response = productServiceClient.get("/api/product-service/products/{id}", nonExistentId);

		// Verify response
		assertEquals(404, response.getStatusCode(), "Expected 404 Not Found status code");

		log.info("Non-existent product correctly returned 404");
	}
}
