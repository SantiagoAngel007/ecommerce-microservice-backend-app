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
@Epic("User Management")
@Feature("Authentication and User Profile")
@Tag("e2e")
@Tag("e2e-flows")
@DisplayName("User Authentication Flow E2E Tests")
public class UserFlowE2ETest extends BaseE2ETest {

	private static final String UNIQUE_SUFFIX = String.valueOf(System.currentTimeMillis());
	private static final String TEST_USERNAME = "e2euser_" + UNIQUE_SUFFIX;
	private static final String TEST_EMAIL = "e2euser_" + UNIQUE_SUFFIX + "@test.com";
	private static final String TEST_PASSWORD = "TestPassword123!";

	@BeforeEach
	public void setUp() {
		super.setUp();
		clearAuthTokens();
	}

	@Test
	@Story("User Registration")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test user registration through the proxy client service")
	@DisplayName("Should register a new user successfully")
	public void testUserRegistration() {
		log.info("Testing user registration for: {}", TEST_USERNAME);

		// Build registration data
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

		// Register user
		Response response = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);

		// Verify response
		assertEquals(201, response.getStatusCode(), "Expected 201 Created status code");
		assertNotNull(response.body().asString(), "Response body should not be null");

		log.info("User registration successful for: {}", TEST_USERNAME);
	}

	@Test
	@Story("User Login")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test user login and token generation")
	@DisplayName("Should login user and return authentication token")
	public void testUserLogin() {
		log.info("Testing user login");

		// First register the user
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		proxyClientServiceClient.post("/api/proxy-client/register", registrationData);

		// Build login data
		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);

		// Perform login
		Response response = proxyClientServiceClient.post("/api/proxy-client/login", loginData);

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");

		// Extract token
		String token = response.jsonPath().getString("token");
		assertNotNull(token, "Token should not be null");
		assertFalse(token.isEmpty(), "Token should not be empty");

		// Set token for subsequent requests
		apiClient.setAuthToken(token);

		log.info("User login successful, token obtained");
	}

	@Test
	@Story("Get User Profile")
	@Severity(SeverityLevel.HIGH)
	@Description("Test retrieving user profile information")
	@DisplayName("Should retrieve user profile after authentication")
	public void testGetUserProfile() {
		log.info("Testing get user profile");

		// Register and login
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		proxyClientServiceClient.post("/api/proxy-client/register", registrationData);

		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);
		Response loginResponse = proxyClientServiceClient.post("/api/proxy-client/login", loginData);
		String token = loginResponse.jsonPath().getString("token");
		apiClient.setAuthToken(token);

		// Get user profile
		Response response = userServiceClient.get("/api/user-service/users");

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertTrue(response.body().asString().length() > 0, "Response body should contain user data");

		log.info("User profile retrieved successfully");
	}

	@Test
	@Story("Get User by ID")
	@Severity(SeverityLevel.HIGH)
	@Description("Test retrieving specific user by ID")
	@DisplayName("Should retrieve user profile by ID")
	public void testGetUserById() {
		log.info("Testing get user by ID");

		// Register and login
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		Response regResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		Long userId = regResponse.jsonPath().getLong("id");

		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);
		Response loginResponse = proxyClientServiceClient.post("/api/proxy-client/login", loginData);
		String token = loginResponse.jsonPath().getString("token");
		apiClient.setAuthToken(token);

		// Get user by ID
		Response response = userServiceClient.get("/api/user-service/users/{id}", userId);

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");
		assertEquals(TEST_USERNAME, response.jsonPath().getString("username"),
			"Username should match registered username");

		log.info("User retrieved by ID successfully");
	}

	@Test
	@Story("Update User Profile")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test updating user profile information")
	@DisplayName("Should update user profile successfully")
	public void testUpdateUserProfile() {
		log.info("Testing update user profile");

		// Register and login
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		Response regResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		Long userId = regResponse.jsonPath().getLong("id");

		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, TEST_PASSWORD);
		Response loginResponse = proxyClientServiceClient.post("/api/proxy-client/login", loginData);
		String token = loginResponse.jsonPath().getString("token");
		apiClient.setAuthToken(token);

		// Update user profile
		Map<String, Object> updateData = TestDataBuilder.buildUserProfileUpdate(
			"Updated Name", "+9876543210", "456 New Street");
		Response response = userServiceClient.put("/api/user-service/users/{id}", updateData, userId);

		// Verify response
		assertEquals(200, response.getStatusCode(), "Expected 200 OK status code");

		log.info("User profile updated successfully");
	}

	@Test
	@Story("Invalid Login")
	@Severity(SeverityLevel.HIGH)
	@Description("Test login with invalid credentials")
	@DisplayName("Should reject login with invalid credentials")
	public void testInvalidLogin() {
		log.info("Testing invalid login");

		// Register user first
		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
		proxyClientServiceClient.post("/api/proxy-client/register", registrationData);

		// Try to login with wrong password
		Map<String, Object> loginData = TestDataBuilder.buildUserLogin(TEST_USERNAME, "WrongPassword123!");
		Response response = proxyClientServiceClient.post("/api/proxy-client/login", loginData);

		// Verify rejection
		assertNotEquals(200, response.getStatusCode(), "Login with wrong password should fail");

		log.info("Invalid login correctly rejected");
	}

	@Test
	@Story("Duplicate Registration")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test registration with duplicate username")
	@DisplayName("Should reject duplicate user registration")
	public void testDuplicateUserRegistration() {
		log.info("Testing duplicate user registration");

		Map<String, Object> registrationData = TestDataBuilder.buildUserRegistration(
			TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

		// First registration
		Response firstResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		assertEquals(201, firstResponse.getStatusCode(), "First registration should succeed");

		// Second registration with same username
		Response secondResponse = proxyClientServiceClient.post("/api/proxy-client/register", registrationData);
		assertNotEquals(201, secondResponse.getStatusCode(), "Duplicate registration should fail");

		log.info("Duplicate registration correctly rejected");
	}
}
