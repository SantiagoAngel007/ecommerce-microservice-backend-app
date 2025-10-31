"""
Performance and Load Tests for Payment Service using Locust
Simple test cases simulating real payment processing scenarios
"""

from locust import HttpUser, task, between
import random


class PaymentServiceUser(HttpUser):
    """Simulates user interactions with the Payment Service"""

    wait_time = between(1, 3)  # Wait 1-3 seconds between requests
    base_url = "http://localhost:8400"

    @task(4)
    def list_payments(self):
        """Task 1: List all payments (most frequent)"""
        page = random.randint(0, 5)
        with self.client.get(
            f"/api/payment-service/payments?page={page}&size=10",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(3)
    def get_payment_details(self):
        """Task 2: Get specific payment details"""
        payment_id = random.randint(1, 100)
        with self.client.get(
            f"/api/payment-service/payments/{payment_id}",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:  # Both are valid
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(2)
    def create_payment(self):
        """Task 3: Create a new payment"""
        order_id = random.randint(1, 50)
        amount = round(random.uniform(10.00, 500.00), 2)

        payment_data = {
            "orderId": order_id,
            "amount": amount,
            "paymentMethod": "CREDIT_CARD",
            "status": "PENDING"
        }

        with self.client.post(
            "/api/payment-service/payments",
            json=payment_data,
            catch_response=True
        ) as response:
            if response.status_code in [201, 200]:
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
