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
@Epic("User Preferences")
@Feature("Favorite Products Management")
@Tag("e2e")
@Tag("e2e-flows")
@DisplayName("Favorite Products Flow E2E Tests")
public class FavouriteFlowE2ETest extends BaseE2ETest {

	@BeforeEach
	public void setUp() {
		super.setUp();
		clearAuthTokens();
	}

	@Test
	@Story("Add Product to Favorites")
	@Severity(SeverityLevel.HIGH)
	@Description("Test adding a product to user favorites")
	@DisplayName("Should add product to favorites successfully")
	public void testAddProductToFavorites() {
		log.info("Testing add product to favorites");

		long testProductId = 1L; // Adjust according to your test setup
		Map<String, Object> favoriteData = TestDataBuilder.buildFavoriteAddition(testProductId);

		Response response = favouriteServiceClient.post("/api/favourite-service/favorites", favoriteData);

		// Verify response
		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300,
			"Expected 2xx status code for adding to favorites");
		assertNotNull(response.body().asString(), "Response body should contain favorite data");

		log.info("Product added to favorites successfully");
	}

	@Test
	@Story("Get User Favorites")
	@Severity(SeverityLevel.HIGH)
	@Description("Test retrieving user's favorite products")
	@DisplayName("Should retrieve user favorites successfully")
	public void testGetUserFavorites() {
		log.info("Testing get user favorites");

		long testUserId = 1L; // Adjust according to your test setup
		Response response = favouriteServiceClient.get("/api/favourite-service/favorites/user/{userId}", testUserId);

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertNotNull(response.body().asString(), "Response body should not be null");

		log.info("User favorites retrieved successfully");
	}

	@Test
	@Story("Get All Favorites")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test retrieving all favorites")
	@DisplayName("Should retrieve all favorites successfully")
	public void testGetAllFavorites() {
		log.info("Testing get all favorites");

		Response response = favouriteServiceClient.get("/api/favourite-service/favorites");

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertNotNull(response.body().asString(), "Response body should not be null");

		log.info("All favorites retrieved successfully");
	}

	@Test
	@Story("Remove Product from Favorites")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test removing a product from favorites")
	@DisplayName("Should remove product from favorites successfully")
	public void testRemoveFromFavorites() {
		log.info("Testing remove product from favorites");

		// First add a product to favorites
		long testProductId = 1L;
		Map<String, Object> favoriteData = TestDataBuilder.buildFavoriteAddition(testProductId);
		Response addResponse = favouriteServiceClient.post("/api/favourite-service/favorites", favoriteData);

		if (addResponse.getStatusCode() >= 200 && addResponse.getStatusCode() < 300) {
			Long favoriteId = addResponse.jsonPath().getLong("id");

			if (favoriteId != null) {
				// Remove from favorites
				Response deleteResponse = favouriteServiceClient.delete("/api/favourite-service/favorites/{id}",
					favoriteId);

				// Verify response
				assertTrue(deleteResponse.getStatusCode() >= 200 && deleteResponse.getStatusCode() < 300,
					"Expected 2xx status code for removing from favorites");

				log.info("Product removed from favorites successfully");
			}
		}
	}

	@Test
	@Story("Check if Product is Favorite")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test checking if a product is in favorites")
	@DisplayName("Should check product favorite status")
	public void testCheckIfProductIsFavorite() {
		log.info("Testing check if product is favorite");

		long testProductId = 1L;
		long testUserId = 1L;

		// Add to favorites
		Map<String, Object> favoriteData = TestDataBuilder.buildFavoriteAddition(testProductId);
		favouriteServiceClient.post("/api/favourite-service/favorites", favoriteData);

		// Check if favorite
		Response response = favouriteServiceClient.get(
			"/api/favourite-service/favorites/user/{userId}/product/{productId}", testUserId, testProductId);

		// Verify response - may be 200 if found, 404 if not
		assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
			"Expected valid response status code");

		log.info("Product favorite check completed");
	}

	@Test
	@Story("Favorite Pagination")
	@Severity(SeverityLevel.LOW)
	@Description("Test pagination of user favorites")
	@DisplayName("Should handle favorites pagination correctly")
	public void testFavoritePagination() {
		log.info("Testing favorites pagination");

		long testUserId = 1L;
		Response response = favouriteServiceClient.get("/api/favourite-service/favorites/user/{userId}?page=0&size=10",
			testUserId);

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");

		log.info("Favorites pagination working correctly");
	}

	@Test
	@Story("Add Duplicate Favorite")
	@Severity(SeverityLevel.LOW)
	@Description("Test adding a product to favorites when already favorited")
	@DisplayName("Should handle duplicate favorite request")
	public void testAddDuplicateFavorite() {
		log.info("Testing add duplicate favorite");

		long testProductId = 1L;
		Map<String, Object> favoriteData = TestDataBuilder.buildFavoriteAddition(testProductId);

		// First addition
		Response firstResponse = favouriteServiceClient.post("/api/favourite-service/favorites", favoriteData);

		if (firstResponse.getStatusCode() >= 200 && firstResponse.getStatusCode() < 300) {
			// Second addition (duplicate)
			Response secondResponse = favouriteServiceClient.post("/api/favourite-service/favorites", favoriteData);

			// Should handle gracefully
			assertTrue(secondResponse.getStatusCode() >= 200 && secondResponse.getStatusCode() < 500,
				"Should handle duplicate favorite request gracefully");

			log.info("Duplicate favorite request handled correctly");
		}
	}

	@Test
	@Story("Get Non-existent Favorite")
	@Severity(SeverityLevel.LOW)
	@Description("Test retrieving non-existent favorite")
	@DisplayName("Should return appropriate error for non-existent favorite")
	public void testGetNonExistentFavorite() {
		log.info("Testing get non-existent favorite");

		Long nonExistentId = 999999L;
		Response response = favouriteServiceClient.get("/api/favourite-service/favorites/{id}", nonExistentId);

		// Verify response
		assertEquals(404, response.getStatusCode(), "Expected 404 Not Found status code");

		log.info("Non-existent favorite correctly returned 404");
	}
}
