# Quick Start Guide - Locust Performance Tests

## 1️⃣ Install Locust

```bash
pip install -r requirements.txt
```

Or individually:
```bash
pip install locust
```

## 2️⃣ Start Microservices

Make sure all services are running:
```bash
cd ..
docker-compose up -d
```

Or verify each service:
```bash
curl http://localhost:8700/actuator/health  # User Service
curl http://localhost:8500/actuator/health  # Product Service
curl http://localhost:8300/actuator/health  # Order Service
curl http://localhost:8400/actuator/health  # Payment Service
```

## 3️⃣ Run Tests

### Option A: Run All Tests (Recommended)

**Windows:**
```bash
run-all-tests.bat
```

**Linux/Mac:**
```bash
chmod +x run-all-tests.sh
./run-all-tests.sh
```

### Option B: Run Individual Tests with Web UI

```bash
# User Service
locust -f locustfile-user-service.py

# Then open: http://localhost:8089
```

### Option C: Run Individual Tests Headless

```bash
# User Service - 10 users, 60 seconds
locust -f locustfile-user-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

# Product Service
locust -f locustfile-product-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

# Order Service
locust -f locustfile-order-service.py --users 10 --spawn-rate 2 --run-time 60s --headless

# Payment Service
locust -f locustfile-payment-service.py --users 10 --spawn-rate 2 --run-time 60s --headless
```

## 4️⃣ View Results

**Web UI Results:**
- Open http://localhost:8089
- Adjust users and spawn rate
- Click "Start swarming"
- Watch real-time results

**CSV Results:**
```bash
cat results_20240101_120000/user-service_stats.csv
cat results_20240101_120000/product-service_stats.csv
```

## Test Scenarios

Each test simulates:

### User Service
- ✅ List all users (most common)
- ✅ Get user by ID
- ✅ Create new user
- ✅ Health check

### Product Service
- ✅ List products (most common)
- ✅ Get product details
- ✅ Search products
- ✅ Health check

### Order Service
- ✅ List orders (most common)
- ✅ Get order details
- ✅ Get user orders
- ✅ Health check

### Payment Service
- ✅ List payments (most common)
- ✅ Get payment details
- ✅ Create payment
- ✅ Health check

## Common Commands

```bash
# Test with 5 users, 30 second duration
locust -f locustfile-user-service.py --users 5 --run-time 30s --headless

# Test with 50 users for stress testing
locust -f locustfile-user-service.py --users 50 --spawn-rate 10 --run-time 120s --headless

# Export detailed CSV
locust -f locustfile-user-service.py --users 10 --run-time 60s --headless --csv=my_results

# Run without stopping on failure
locust -f locustfile-user-service.py --users 10 --run-time 60s --headless
```

## Troubleshooting

### "Connection refused"
```bash
# Check if service is running
curl http://localhost:8700/actuator/health

# Start services
docker-compose up -d
```

### "No module named 'locust'"
```bash
pip install locust
```

### High failure rate
- Reduce users: `--users 5` instead of 50
- Check service logs: `docker-compose logs user-service`
- Increase wait time between requests

## Performance Baselines

If all tests pass with:
- ✅ 0% failure rate
- ✅ < 150ms average response
- ✅ < 20 requests/second

Your services are performing well! 🎉

## Next Steps

1. Run `run-all-tests.bat` (or `.sh`)
2. Monitor results in real-time
3. Check CSV files for detailed metrics
4. Identify bottlenecks
5. Optimize services if needed
6. Run tests weekly to track performance

---

For more details, see [README.md](README.md)
