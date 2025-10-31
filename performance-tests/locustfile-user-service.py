"""
Performance and Load Tests for User Service using Locust
Simple test cases simulating real user scenarios
"""

from locust import HttpUser, task, between
import random
import string


class UserServiceUser(HttpUser):
    """Simulates user interactions with the User Service"""

    wait_time = between(1, 3)  # Wait 1-3 seconds between requests
    base_url = "http://localhost:8700"

    def on_start(self):
        """Setup before starting tasks"""
        self.user_id = None
        self.auth_token = None

    @task(3)
    def get_all_users(self):
        """Task 1: Get all users (most frequent)"""
        with self.client.get(
            "/api/user-service/users",
            catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(2)
    def get_user_by_id(self):
        """Task 2: Get specific user by ID"""
        user_id = random.randint(1, 100)
        with self.client.get(
            f"/api/user-service/users/{user_id}",
            catch_response=True
        ) as response:
            if response.status_code in [200, 404]:  # Both are valid
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")

    @task(2)
    def create_user(self):
        """Task 3: Create a new user"""
        unique_suffix = ''.join(random.choices(string.ascii_lowercase, k=5))
        user_data = {
            "username": f"perftest_{unique_suffix}",
            "email": f"perf_{unique_suffix}@test.com",
            "password": "TestPassword123!",
            "fullName": "Performance Test User",
            "phoneNumber": "+1234567890",
            "address": "123 Test St",
            "city": "Test City",
            "state": "TS",
            "postalCode": "12345",
            "country": "Test Country"
        }

        with self.client.post(
            "/api/user-service/users",
            json=user_data,
            catch_response=True
        ) as response:
            if response.status_code in [201, 200]:
                try:
                    self.user_id = response.json().get("id")
                    response.success()
                except:
                    response.failure("Failed to parse response")
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
