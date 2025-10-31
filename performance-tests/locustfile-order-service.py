"""
Performance and Load Tests for Order Service using Locust
Simple test cases simulating real order management scenarios
"""

from locust import HttpUser, task, between
import random


class OrderServiceUser(HttpUser):
    """Simulates user interactions with the Order Service"""

    wait_time = between(1, 3)  # Wait 1-3 seconds between requests
    base_url = "http://localhost:8300"

    @task(4)
    def list_orders(self):
        """Task 1: List all orders (most frequent)"""
        page = random.randint(0, 5)
        with self.client.get(
            f"/api/order-service/orders?page={page}&size=10",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(3)
    def get_order_details(self):
        """Task 2: Get specific order details"""
        order_id = random.randint(1, 100)
        with self.client.get(
            f"/api/order-service/orders/{order_id}",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:  # Both are valid
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(2)
    def get_user_orders(self):
        """Task 3: Get orders for a specific user"""
        user_id = random.randint(1, 50)
        with self.client.get(
            f"/api/order-service/orders/user/{user_id}",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:  # Both are valid
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(1)
    def health_check(self):
        """Task 4: Check service health (least frequent)"""
        with self.client.get(
            "/actuator/health",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Health check failed: {response.status_code}")
