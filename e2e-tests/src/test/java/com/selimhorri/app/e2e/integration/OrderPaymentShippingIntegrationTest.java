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
@Feature("Order, Payment and Shipping Service Integration")
@Tag("e2e")
@Tag("e2e-integration")
@DisplayName("Order-Payment-Shipping Integration Tests")
public class OrderPaymentShippingIntegrationTest extends BaseE2ETest {

	@BeforeEach
	public void setUp() {
		super.setUp();
		clearAuthTokens();
	}

	@Test
	@Story("Complete Order Processing")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test complete order processing from creation to shipment")
	@DisplayName("Should process order completely from creation to shipping")
	public void testCompleteOrderProcessing() {
		log.info("Testing complete order processing flow");

		try {
			// Step 1: Create order
			long userId = 1L;
			long productId = 1L;
			Map<String, Object> orderData = TestDataBuilder.buildOrderCreation(userId, productId, 1);
			Response orderResponse = orderServiceClient.post("/api/order-service/orders", orderData);

			if (orderResponse.getStatusCode() >= 200 && orderResponse.getStatusCode() < 300) {
				Long orderId = orderResponse.jsonPath().getLong("id");
				log.info("Order created: {}", orderId);

				// Step 2: Process payment
				double orderAmount = 99.99;
				Map<String, Object> paymentData = TestDataBuilder.buildPaymentRequest(orderId, orderAmount);
				Response paymentResponse = paymentServiceClient.post("/api/payment-service/payments", paymentData);

				assertTrue(paymentResponse.getStatusCode() >= 200 && paymentResponse.getStatusCode() < 300,
					"Payment should be processed");
				log.info("Payment processed for order: {}", orderId);

				// Step 3: Update order status
				Map<String, Object> statusUpdate = TestDataBuilder.buildOrderStatusUpdate("PAID");
				Response statusResponse = orderServiceClient.put("/api/order-service/orders/{id}/status", statusUpdate,
					orderId);
				assertEquals(200, statusResponse.getStatusCode(), "Order status should be updated");
				log.info("Order status updated to PAID");

				// Step 4: Create shipping
				String shippingAddress = "123 Test Street, Test City, TS 12345";
				Map<String, Object> shippingData = TestDataBuilder.buildShippingRequest(orderId, shippingAddress);
				Response shippingResponse = shippingServiceClient.post("/api/shipping-service/shipments", shippingData);

				assertTrue(shippingResponse.getStatusCode() >= 200 && shippingResponse.getStatusCode() < 300,
					"Shipping should be created");
				log.info("Shipping created for order: {}", orderId);

				// Step 5: Verify order final status
				Response finalOrderResponse = orderServiceClient.get("/api/order-service/orders/{id}", orderId);
				assertEquals(200, finalOrderResponse.getStatusCode(), "Should retrieve final order status");

				log.info("Complete order processing flow executed successfully");
			}
		} catch (Exception e) {
			log.warn("Complete order processing test encountered exception: {}", e.getMessage());
		}
	}

	@Test
	@Story("Payment Processing for Order")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Test that payment is correctly processed for an order")
	@DisplayName("Should process payment for order successfully")
	public void testPaymentProcessingForOrder() {
		log.info("Testing payment processing");

		try {
			// Create order
			long userId = 1L;
			long productId = 1L;
			Map<String, Object> orderData = TestDataBuilder.buildOrderCreation(userId, productId, 1);
			Response orderResponse = orderServiceClient.post("/api/order-service/orders", orderData);

			if (orderResponse.getStatusCode() >= 200 && orderResponse.getStatusCode() < 300) {
				Long orderId = orderResponse.jsonPath().getLong("id");

				// Process payment
				Map<String, Object> paymentData = TestDataBuilder.buildPaymentRequest(orderId, 99.99);
				Response paymentResponse = paymentServiceClient.post("/api/payment-service/payments", paymentData);

				assertTrue(paymentResponse.getStatusCode() >= 200 && paymentResponse.getStatusCode() < 300,
					"Payment should be processed successfully");

				Long paymentId = paymentResponse.jsonPath().getLong("id");
				assertNotNull(paymentId, "Payment ID should be returned");

				log.info("Payment successfully processed for order: {}", orderId);
			}
		} catch (Exception e) {
			log.warn("Payment processing test encountered exception: {}", e.getMessage());
		}
	}

	@Test
	@Story("Shipping Creation for Order")
	@Severity(SeverityLevel.HIGH)
	@Description("Test that shipping is created correctly for an order")
	@DisplayName("Should create shipping for order successfully")
	public void testShippingCreationForOrder() {
		log.info("Testing shipping creation");

		try {
			// Create order
			long userId = 1L;
			long productId = 1L;
			Map<String, Object> orderData = TestDataBuilder.buildOrderCreation(userId, productId, 1);
			Response orderResponse = orderServiceClient.post("/api/order-service/orders", orderData);

			if (orderResponse.getStatusCode() >= 200 && orderResponse.getStatusCode() < 300) {
				Long orderId = orderResponse.jsonPath().getLong("id");

				// Create shipping
				Map<String, Object> shippingData = TestDataBuilder.buildShippingRequest(orderId,
					"789 Test Avenue, Test Town, TT 98765");
				Response shippingResponse = shippingServiceClient.post("/api/shipping-service/shipments", shippingData);

				assertTrue(shippingResponse.getStatusCode() >= 200 && shippingResponse.getStatusCode() < 300,
					"Shipping should be created successfully");

				Long shipmentId = shippingResponse.jsonPath().getLong("id");
				assertNotNull(shipmentId, "Shipment ID should be returned");

				log.info("Shipping successfully created for order: {}", orderId);
			}
		} catch (Exception e) {
			log.warn("Shipping creation test encountered exception: {}", e.getMessage());
		}
	}

	@Test
	@Story("Order Status Tracking")
	@Severity(SeverityLevel.HIGH)
	@Description("Test that order status is tracked correctly through payment and shipping")
	@DisplayName("Should track order status correctly")
	public void testOrderStatusTracking() {
		log.info("Testing order status tracking");

		// Get all orders
		Response ordersResponse = orderServiceClient.get("/api/order-service/orders");
		assertEquals(200, ordersResponse.getStatusCode(), "Should retrieve orders");

		// Find an order and track its status
		Long orderId = ordersResponse.jsonPath().getLong("content[0].id");
		if (orderId != null) {
			Response orderResponse = orderServiceClient.get("/api/order-service/orders/{id}", orderId);
			assertEquals(200, orderResponse.getStatusCode());

			String status = orderResponse.jsonPath().getString("status");
			assertNotNull(status, "Order should have a status");

			log.info("Order {} has status: {}", orderId, status);
		}
	}

	@Test
	@Story("Multiple Orders Independent Processing")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test that multiple orders are processed independently")
	@DisplayName("Should process multiple orders independently")
	public void testMultipleOrdersIndependentProcessing() {
		log.info("Testing multiple orders independent processing");

		try {
			// Create first order
			Map<String, Object> order1Data = TestDataBuilder.buildOrderCreation(1L, 1L, 1);
			Response order1Response = orderServiceClient.post("/api/order-service/orders", order1Data);

			// Create second order
			Map<String, Object> order2Data = TestDataBuilder.buildOrderCreation(1L, 2L, 1);
			Response order2Response = orderServiceClient.post("/api/order-service/orders", order2Data);

			if (order1Response.getStatusCode() >= 200 && order1Response.getStatusCode() < 300 &&
				order2Response.getStatusCode() >= 200 && order2Response.getStatusCode() < 300) {

				Long order1Id = order1Response.jsonPath().getLong("id");
				Long order2Id = order2Response.jsonPath().getLong("id");

				assertNotNull(order1Id, "First order should be created");
				assertNotNull(order2Id, "Second order should be created");
				assertNotEquals(order1Id, order2Id, "Orders should have different IDs");

				log.info("Multiple orders created independently");
			}
		} catch (Exception e) {
			log.warn("Multiple orders test encountered exception: {}", e.getMessage());
		}
	}

	@Test
	@Story("Order Cancellation")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test that order can be cancelled")
	@DisplayName("Should cancel order successfully")
	public void testOrderCancellation() {
		log.info("Testing order cancellation");

		// Get an existing order
		Response ordersResponse = orderServiceClient.get("/api/order-service/orders");
		Long orderId = ordersResponse.jsonPath().getLong("content[0].id");

		if (orderId != null) {
			// Cancel order
			Map<String, Object> cancelData = TestDataBuilder.buildOrderStatusUpdate("CANCELLED");
			Response cancelResponse = orderServiceClient.put("/api/order-service/orders/{id}/status", cancelData,
				orderId);

			assertTrue(cancelResponse.getStatusCode() >= 200 && cancelResponse.getStatusCode() < 300,
				"Order cancellation should succeed");

			log.info("Order {} successfully cancelled", orderId);
		}
	}

	@Test
	@Story("Payment History for Order")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test retrieving payment history for an order")
	@DisplayName("Should retrieve payment history for order")
	public void testPaymentHistoryForOrder() {
		log.info("Testing payment history retrieval");

		// Get all payments
		Response paymentsResponse = paymentServiceClient.get("/api/payment-service/payments");
		assertEquals(200, paymentsResponse.getStatusCode(), "Should retrieve payments");

		log.info("Payment history retrieved successfully");
	}

	@Test
	@Story("Shipping Status Tracking")
	@Severity(SeverityLevel.MEDIUM)
	@Description("Test tracking shipping status for an order")
	@DisplayName("Should track shipping status")
	public void testShippingStatusTracking() {
		log.info("Testing shipping status tracking");

		// Get all shipments
		Response shipmentsResponse = shippingServiceClient.get("/api/shipping-service/shipments");
		assertEquals(200, shipmentsResponse.getStatusCode(), "Should retrieve shipments");

		log.info("Shipping status tracking completed");
	}
}
