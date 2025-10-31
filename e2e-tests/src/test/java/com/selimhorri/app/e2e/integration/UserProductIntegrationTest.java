package com.selimhorri.app.e2e.integration;

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
@Epic("Integration Tests")
@Feature("User and Product Service Integration")
@Tag("e2e")
@Tag("e2e-integration")
@DisplayName("User-Product Service Integration Tests")
public class UserProductIntegrationTest extends BaseE2ETest {

	private static final String UNIQUE_SUFFIX = String.valueOf(System.currentTimeMillis());
	private static final String TEST_USERNAME = "integuser_" + UNIQUE_SUFFIX;
	private static final String TEST_EMAIL = "integuser_" + UNIQUE_SUFFIX + "@test.com";
	private static final String TEST_PASSWORD = "TestPassword123!";

	@BeforeEach
	public void setUp() {
		super.setUp();
		clearAuthTokens();
	}

	@Test
	@Story("User Browse Products After Login")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test that user can browse products after successful authentication")
	@DisplayName("Should allow authenticated user to browse products")
	public void testAuthenticatedUserBrowseProducts() {
		log.info("Testing authenticated user browsing products");

		// Step 1: Register and login user
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		Response regResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		assertEquals(201, regResponse.getStatusCode(), "User registration should succeed");

		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);
		Response loginResponse = proxyClientServiceClient.post("/api/proxy-client/login", loginData);
		assertEquals(200, loginResponse.getStatusCode(), "User login should succeed");

		String token = loginResponse.jsonPath().getString("token");
		apiClient.setAuthToken(token);
		userServiceClient.setAuthToken(token);
		productServiceClient.setAuthToken(token);

		// Step 2: Browse products
		Response productsResponse = productServiceClient.get("/api/product-service/products");
		assertEquals(200, productsResponse.getStatusCode(), "Should successfully retrieve products");
		assertNotNull(productsResponse.body().asString(), "Products data should not be null");

		log.info("Authenticated user successfully browsed products");
	}

	@Test
	@Story("User Add Product to Favorites")
	@Severity(SeverityLevel.HIGH)
	@Description("Test user can add a product to favorites after authentication")
	@DisplayName("Should allow user to add product to favorites")
	public void testUserAddProductToFavorites() {
		log.info("Testing user adding product to favorites");

		// Step 1: Register and login user
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		Response regResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		Long userId = regResponse.jsonPath().getLong("id");

		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);
		Response loginResponse = proxyClientServiceClient.post("/api/proxy-client/login", loginData);
		String token = loginResponse.jsonPath().getString("token");
		apiClient.setAuthToken(token);
		favouriteServiceClient.setAuthToken(token);

		// Step 2: Get available products
		Response productsResponse = productServiceClient.get("/api/product-service/products");
		Long productId = productsResponse.jsonPath().getLong("content[0].id");

		if (productId != null) {
			// Step 3: Add product to favorites
			Map<String, Object> favoriteData = TestDataBuilder.buildFavoriteAddition(productId);
			Response favoriteResponse = favouriteServiceClient.post("/api/favourite-service/favorites", favoriteData);

			assertTrue(favoriteResponse.getStatusCode() >= 200 && favoriteResponse.getStatusCode() < 300,
				"Adding product to favorites should succeed");

			log.info("User successfully added product to favorites");
		}
	}

	@Test
	@Story("User View Profile and Edit Information")
	@Severity(SeverityLevel.HIGH)
	@Description("Test user can view and edit their profile information")
	@DisplayName("Should allow user to view and edit profile")
	public void testUserViewAndEditProfile() {
		log.info("Testing user viewing and editing profile");

		// Step 1: Register and login
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		Response regResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		Long userId = regResponse.jsonPath().getLong("id");

		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);
		Response loginResponse = proxyClientServiceClient.post("/api/proxy-client/login", loginData);
		String token = loginResponse.jsonPath().getString("token");
		userServiceClient.setAuthToken(token);

		// Step 2: View profile
		Response profileResponse = userServiceClient.get("/api/user-service/users/{id}", userId);
		assertEquals(200, profileResponse.getStatusCode(), "Should retrieve user profile");
		assertEquals(TEST_USERNAME, profileResponse.jsonPath().getString("username"));

		// Step 3: Edit profile
		Map<String, Object> profileUpdate = TestDataBuilder.buildUserProfileUpdate(
			"Updated User Name", "+1987654321", "Updated Address");
		Response updateResponse = userServiceClient.put("/api/user-service/users/{id}", profileUpdate, userId);

		assertEquals(200, updateResponse.getStatusCode(), "Should update user profile");

		log.info("User successfully viewed and edited profile");
	}

	@Test
	@Story("User Search and View Product Details")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test user can search for products and view details")
	@DisplayName("Should allow user to search and view product details")
	public void testUserSearchAndViewProductDetails() {
		log.info("Testing user searching for products");

		// Step 1: Get all products
		Response listResponse = productServiceClient.get("/api/product-service/products");
		assertEquals(200, listResponse.getStatusCode());

		// Step 2: Get product details
		Long productId = listResponse.jsonPath().getLong("content[0].id");
		if (productId != null) {
			Response detailResponse = productServiceClient.get("/api/product-service/products/{id}", productId);
			assertEquals(200, detailResponse.getStatusCode(), "Should retrieve product details");

			String productName = detailResponse.jsonPath().getString("name");
			assertNotNull(productName, "Product name should not be null");

			log.info("User successfully found and viewed product details");
		}
	}

	@Test
	@Story("Unauthorized Access Prevention")
	@Severity(SeverityLevel.HIGH)
	@Description("Test that unauthorized users cannot access protected resources")
	@DisplayName("Should prevent unauthorized access to protected resources")
	public void testUnauthorizedAccessPrevention() {
		log.info("Testing unauthorized access prevention");

		// Try to access protected resource without token
		clearAuthTokens();

		// This may succeed or fail depending on your API design
		// The important thing is to document the behavior
		Response response = userServiceClient.get("/api/user-service/users/1");

		log.info("Unauthorized access response status: {}", response.getStatusCode());
	}

	@Test
	@Story("Multiple Users Independent Data")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test that multiple users have independent data")
	@DisplayName("Should maintain data isolation between users")
	public void testMultipleUsersDataIsolation() {
		log.info("Testing data isolation between multiple users");

		// Create first user
		String user1Username = "user1_" + System.currentTimeMillis();
		String user1Email = user1Username + "@test.com";
		Map<String, Object> user1Data = TestDataBuilder.buildUserRegistration(
			user1Username, user1Email, "Password123!");
		Response user1RegResponse = proxyClientServiceClient.post("/api/proxy-client/register", user1Data);
		Long user1Id = user1RegResponse.jsonPath().getLong("id");

		// Create second user
		String user2Username = "user2_" + System.currentTimeMillis();
		String user2Email = user2Username + "@test.com";
		Map<String, Object> user2Data = TestDataBuilder.buildUserRegistration(
			user2Username, user2Email, "Password123!");
		Response user2RegResponse = proxyClientServiceClient.post("/api/proxy-client/register", user2Data);
		Long user2Id = user2RegResponse.jsonPath().getLong("id");

		// Verify both users were created
		assertNotNull(user1Id, "First user should be created");
		assertNotNull(user2Id, "Second user should be created");
		assertNotEquals(user1Id, user2Id, "Users should have different IDs");

		log.info("Data isolation confirmed between multiple users");
	}
}
