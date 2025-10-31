"""
Performance and Load Tests for Product Service using Locust
Simple test cases simulating real product catalog scenarios
"""

from locust import HttpUser, task, between
import random


class ProductServiceUser(HttpUser):
    """Simulates user interactions with the Product Service"""

    wait_time = between(1, 3)  # Wait 1-3 seconds between requests
    base_url = "http://localhost:8500"

    @task(4)
    def list_products(self):
        """Task 1: List all products (most frequent)"""
        page = random.randint(0, 5)
        with self.client.get(
            f"/api/product-service/products?page={page}&size=10",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(3)
    def get_product_details(self):
        """Task 2: Get specific product details"""
        product_id = random.randint(1, 100)
        with self.client.get(
            f"/api/product-service/products/{product_id}",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:  # Both are valid
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(2)
    def search_products(self):
        """Task 3: Search products by name"""
        search_terms = ["laptop", "phone", "tablet", "keyboard", "mouse", "monitor"]
        search_term = random.choice(search_terms)
        with self.client.get(
            f"/api/product-service/products/search?name={search_term}",
            catch_response=True
        ) as response:
            if response.status_code == 200:
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
