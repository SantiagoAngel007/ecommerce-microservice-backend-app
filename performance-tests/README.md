# Performance and Load Tests with Locust

This directory contains simple performance and load tests for the ecommerce microservices using Locust.

## Overview

Locust is an easy-to-use, distributed, user load testing tool written in Python. These tests simulate real user scenarios for each microservice.

## Test Files

### 1. **locustfile-user-service.py**
Tests for User Service (http://localhost:8700)

**Test Cases:**
- Get all users (Task 1 - 3 weight)
- Get user by ID (Task 2 - 2 weight)
- Create new user (Task 3 - 2 weight)
- Health check (Task 4 - 1 weight)

### 2. **locustfile-product-service.py**
Tests for Product Service (http://localhost:8500)

**Test Cases:**
- List all products with pagination (Task 1 - 4 weight)
- Get product details (Task 2 - 3 weight)
- Search products by name (Task 3 - 2 weight)
- Health check (Task 4 - 1 weight)

### 3. **locustfile-order-service.py**
Tests for Order Service (http://localhost:8300)

**Test Cases:**
- List all orders with pagination (Task 1 - 4 weight)
- Get order details (Task 2 - 3 weight)
- Get user orders (Task 3 - 2 weight)
- Health check (Task 4 - 1 weight)

### 4. **locustfile-payment-service.py**
Tests for Payment Service (http://localhost:8400)

**Test Cases:**
- List all payments with pagination (Task 1 - 4 weight)
- Get payment details (Task 2 - 3 weight)
- Create new payment (Task 3 - 2 weight)
- Health check (Task 4 - 1 weight)

## Requirements

Install Locust:
```bash
pip install locust
```

## Running the Tests

### Prerequisites

Ensure all microservices are running:
```bash
docker-compose up -d
# or start services manually
```

### Option 1: Run with Web UI (Recommended)

```bash
# For User Service
locust -f locustfile-user-service.py --host=http://localhost:8700

# For Product Service
locust -f locustfile-product-service.py --host=http://localhost:8500

# For Order Service
locust -f locustfile-order-service.py --host=http://localhost:8300

# For Payment Service
locust -f locustfile-payment-service.py --host=http://localhost:8400
```

Then open your browser to: **http://localhost:8089**

### Option 2: Run Headless (Command Line)

```bash
# Simple run with 10 users, 2 spawn rate, 60 seconds duration
locust -f locustfile-user-service.py \
  --users 10 \
  --spawn-rate 2 \
  --run-time 60s \
  --headless \
  --csv=results_user

# More aggressive test: 50 users
locust -f locustfile-product-service.py \
  --users 50 \
  --spawn-rate 5 \
  --run-time 120s \
  --headless \
  --csv=results_product
```

### Option 3: Run All Services Tests in Sequence

```bash
#!/bin/bash
echo "Testing User Service..."
locust -f locustfile-user-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

echo "Testing Product Service..."
locust -f locustfile-product-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

echo "Testing Order Service..."
locust -f locustfile-order-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

echo "Testing Payment Service..."
locust -f locustfile-payment-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

echo "All tests completed!"
```

## Understanding Test Weights

Each task has a weight (number after @task) that determines how often it's executed:

- Higher weight = More frequently executed
- Weight 4 = Executed 4x more often than weight 1
- Weight 1 = Baseline frequency

Example: In user-service tests
- List all users (weight 3): 30% of requests
- Get user by ID (weight 2): 20% of requests
- Create user (weight 2): 20% of requests
- Health check (weight 1): 10% of requests

## Expected Results

### Successful Test Output

```
Name                    # requests      # failures   Median (ms)  Average (ms)  Min (ms)  Max (ms)
GET /api/.../users           150            0          45           52          20      150
GET /api/.../users/1         100            0          40           48          18      140
POST /api/.../users          100            0          80          95          50      250
GET /actuator/health          50            0          15           18          10       45

Total                        400            0
```

### Common Issues

**Connection Refused:** Services not running
```bash
docker-compose up -d
# or verify port is correct
```

**High Failure Rate:** Service overloaded or slow
- Reduce users: `--users 5` instead of 50
- Increase wait time: Edit `wait_time = between(2, 5)`

**Timeout Errors:** Services responding slowly
- Increase timeout in test file
- Check service logs: `docker-compose logs service-name`

## Configuration Options

### Web UI Parameters

| Parameter | Meaning |
|-----------|---------|
| Number of users | Total concurrent users to simulate |
| Spawn rate | Users created per second |
| Host | Base URL of the service |

### Command Line Parameters

```bash
locust -f locustfile.py \
  --users 50              # Total concurrent users
  --spawn-rate 5          # Users spawned per second
  --run-time 300s         # Test duration (5 minutes)
  --headless              # No web UI
  --csv=results           # Export CSV results
  --stop-timeout 30       # Graceful shutdown timeout
```

## Performance Baselines

For healthy microservices on local machine:

| Service | Metric | Target |
|---------|--------|--------|
| User Service | Avg Response | < 100ms |
| Product Service | Avg Response | < 80ms |
| Order Service | Avg Response | < 100ms |
| Payment Service | Avg Response | < 120ms |
| All Services | Failure Rate | 0% |

## Tips

1. **Start small:** Begin with 10 users to verify tests work
2. **Gradually increase:** Ramp up to 50-100 users for stress tests
3. **Monitor services:** Watch Docker logs during tests
4. **Run during off-hours:** Load tests may impact system performance
5. **Export results:** Use `--csv=results` to generate reports
6. **Repeat tests:** Run multiple times to identify anomalies

## Troubleshooting

### All requests fail
- Check if service is running: `curl http://localhost:8700/actuator/health`
- Check port number in locustfile
- Check network connectivity

### High response times
- Reduce number of users
- Check service CPU/memory usage
- Look for database bottlenecks

### Inconsistent results
- Run test multiple times
- Ensure services are fully warmed up (run a short test first)
- Check for other load on the system

## Next Steps

1. Run basic tests with 10 users
2. Analyze results and identify bottlenecks
3. Gradually increase load
4. Document findings and optimize services
5. Run periodic performance tests (weekly/monthly)

## References

- [Locust Documentation](https://docs.locust.io/)
- [Microservices Performance Testing Best Practices](https://www.locust.io/)
- [Load Testing Strategy Guide](https://docs.locust.io/en/stable/index.html)

---

**Note:** These are simple performance tests designed to verify basic functionality under load. For production-grade stress testing, consider additional parameters, more sophisticated scenarios, and monitoring tools.
