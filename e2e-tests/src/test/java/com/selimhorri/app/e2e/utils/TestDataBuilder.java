package com.selimhorri.app.e2e.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDataBuilder {

	/**
	 * Build a user registration request
	 */
	public static Map<String, Object> buildUserRegistration(String username, String email, String password) {
		Map<String, Object> user = new HashMap<>();
		user.put("username", username);
		user.put("email", email);
		user.put("password", password);
		user.put("fullName", "Test User");
		user.put("phoneNumber", "+1234567890");
		user.put("address", "123 Test Street");
		user.put("city", "Test City");
		user.put("state", "TS");
		user.put("postalCode", "12345");
		user.put("country", "Test Country");
		log.info("Built user registration data for: {}", username);
		return user;
	}

	/**
	 * Build a user login request
	 */
	public static Map<String, Object> buildUserLogin(String username, String password) {
		Map<String, Object> login = new HashMap<>();
		login.put("username", username);
		login.put("password", password);
		log.info("Built login data for: {}", username);
		return login;
	}

	/**
	 * Build a product creation request
	 */
	public static Map<String, Object> buildProductCreation(String name, String description, double price) {
		Map<String, Object> product = new HashMap<>();
		product.put("name", name);
		product.put("description", description);
		product.put("price", price);
		product.put("quantity", 100);
		product.put("sku", "SKU-" + System.currentTimeMillis());
		product.put("category", "Electronics");
		log.info("Built product data: {}", name);
		return product;
	}

	/**
	 * Build an order creation request
	 */
	public static Map<String, Object> buildOrderCreation(long userId, long productId, int quantity) {
		Map<String, Object> order = new HashMap<>();
		order.put("userId", userId);
		order.put("productId", productId);
		order.put("quantity", quantity);
		order.put("status", "PENDING");
		log.info("Built order data for user: {} and product: {}", userId, productId);
		return order;
	}

	/**
	 * Build a payment request
	 */
	public static Map<String, Object> buildPaymentRequest(long orderId, double amount) {
		Map<String, Object> payment = new HashMap<>();
		payment.put("orderId", orderId);
		payment.put("amount", amount);
		payment.put("paymentMethod", "CREDIT_CARD");
		payment.put("status", "PENDING");
		log.info("Built payment data for order: {}", orderId);
		return payment;
	}

	/**
	 * Build a shipping request
	 */
	public static Map<String, Object> buildShippingRequest(long orderId, String address) {
		Map<String, Object> shipping = new HashMap<>();
		shipping.put("orderId", orderId);
		shipping.put("shippingAddress", address);
		shipping.put("shippingMethod", "STANDARD");
		shipping.put("status", "PENDING");
		log.info("Built shipping data for order: {}", orderId);
		return shipping;
	}

	/**
	 * Build a favorite addition request
	 */
	public static Map<String, Object> buildFavoriteAddition(long productId) {
		Map<String, Object> favorite = new HashMap<>();
		favorite.put("productId", productId);
		log.info("Built favorite data for product: {}", productId);
		return favorite;
	}

	/**
	 * Build a product update request
	 */
	public static Map<String, Object> buildProductUpdate(String name, String description, double price) {
		Map<String, Object> product = new HashMap<>();
		product.put("name", name);
		product.put("description", description);
		product.put("price", price);
		log.info("Built product update data: {}", name);
		return product;
	}

	/**
	 * Build a user profile update request
	 */
	public static Map<String, Object> buildUserProfileUpdate(String fullName, String phoneNumber, String address) {
		Map<String, Object> profile = new HashMap<>();
		profile.put("fullName", fullName);
		profile.put("phoneNumber", phoneNumber);
		profile.put("address", address);
		log.info("Built user profile update");
		return profile;
	}

	/**
	 * Build an order status update request
	 */
	public static Map<String, Object> buildOrderStatusUpdate(String status) {
		Map<String, Object> update = new HashMap<>();
		update.put("status", status);
		log.info("Built order status update: {}", status);
		return update;
	}
}
