package com.selimhorri.app.e2e.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.selimhorri.app.e2e.utils.ApiClient;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseE2ETest {

	protected static ApiClient apiClient;
	protected static ApiClient userServiceClient;
	protected static ApiClient productServiceClient;
	protected static ApiClient orderServiceClient;
	protected static ApiClient paymentServiceClient;
	protected static ApiClient shippingServiceClient;
	protected static ApiClient favouriteServiceClient;
	protected static ApiClient proxyClientServiceClient;

	// Service URLs - These should be configurable
	protected static final String API_GATEWAY_URL = "http://localhost:8080";
	protected static final String USER_SERVICE_URL = "http://localhost:8700";
	protected static final String PRODUCT_SERVICE_URL = "http://localhost:8500";
	protected static final String ORDER_SERVICE_URL = "http://localhost:8300";
	protected static final String PAYMENT_SERVICE_URL = "http://localhost:8400";
	protected static final String SHIPPING_SERVICE_URL = "http://localhost:8600";
	protected static final String FAVOURITE_SERVICE_URL = "http://localhost:8800";
	protected static final String PROXY_CLIENT_SERVICE_URL = "http://localhost:8900";

	@BeforeAll
	public static void setUpAll() {
		log.info("Setting up E2E test environment");

		// Initialize API clients
		apiClient = new ApiClient(API_GATEWAY_URL);
		userServiceClient = new ApiClient(USER_SERVICE_URL);
		productServiceClient = new ApiClient(PRODUCT_SERVICE_URL);
		orderServiceClient = new ApiClient(ORDER_SERVICE_URL);
		paymentServiceClient = new ApiClient(PAYMENT_SERVICE_URL);
		shippingServiceClient = new ApiClient(SHIPPING_SERVICE_URL);
		favouriteServiceClient = new ApiClient(FAVOURITE_SERVICE_URL);
		proxyClientServiceClient = new ApiClient(PROXY_CLIENT_SERVICE_URL);

		// Configure RestAssured
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		log.info("E2E test environment setup completed");
	}

	@BeforeEach
	public void setUp() {
		log.info("Starting test: {}", getTestMethodName());
	}

	protected String getTestMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	protected void clearAuthTokens() {
		apiClient.clearAuthToken();
		userServiceClient.clearAuthToken();
		productServiceClient.clearAuthToken();
		orderServiceClient.clearAuthToken();
		paymentServiceClient.clearAuthToken();
		shippingServiceClient.clearAuthToken();
		favouriteServiceClient.clearAuthToken();
		proxyClientServiceClient.clearAuthToken();
	}

	// Utility method to get service URL by name
	protected String getServiceUrl(String serviceName) {
		switch (serviceName.toLowerCase()) {
			case "user":
				return USER_SERVICE_URL;
			case "product":
				return PRODUCT_SERVICE_URL;
			case "order":
				return ORDER_SERVICE_URL;
			case "payment":
				return PAYMENT_SERVICE_URL;
			case "shipping":
				return SHIPPING_SERVICE_URL;
			case "favourite":
				return FAVOURITE_SERVICE_URL;
			case "proxy":
				return PROXY_CLIENT_SERVICE_URL;
			case "gateway":
			default:
				return API_GATEWAY_URL;
		}
	}

	// Utility method to get API client by name
	protected ApiClient getApiClient(String serviceName) {
		switch (serviceName.toLowerCase()) {
			case "user":
				return userServiceClient;
			case "product":
				return productServiceClient;
			case "order":
				return orderServiceClient;
			case "payment":
				return paymentServiceClient;
			case "shipping":
				return shippingServiceClient;
			case "favourite":
				return favouriteServiceClient;
			case "proxy":
				return proxyClientServiceClient;
			case "gateway":
			default:
				return apiClient;
		}
	}
}
