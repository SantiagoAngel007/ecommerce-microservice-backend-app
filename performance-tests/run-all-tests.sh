#!/bin/bash

##############################################################################
# Performance Tests Runner Script for All Microservices
# Runs Locust tests sequentially for all 5 services
##############################################################################

set -e

# Configuration
USERS=10
SPAWN_RATE=2
RUN_TIME=60s
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results_${TIMESTAMP}"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Performance Tests for Microservices${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if services are running
check_service() {
    local port=$1
    local name=$2

    echo -e "${YELLOW}Checking $name on port $port...${NC}"

    if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $name is running${NC}"
        return 0
    else
        echo -e "${RED}✗ $name is NOT running on port $port${NC}"
        echo "Please start the service: docker-compose up -d"
        return 1
    fi
}

# Create results directory
mkdir -p "$RESULTS_DIR"
echo -e "${BLUE}Results will be saved to: $RESULTS_DIR${NC}"
echo ""

# Check all services
echo -e "${YELLOW}Checking services...${NC}"
all_running=true

check_service 8700 "User Service" || all_running=false
check_service 8500 "Product Service" || all_running=false
check_service 8300 "Order Service" || all_running=false
check_service 8400 "Payment Service" || all_running=false

echo ""

if [ "$all_running" = false ]; then
    echo -e "${RED}Some services are not running. Please start them first.${NC}"
    exit 1
fi

echo -e "${GREEN}All services are running!${NC}"
echo ""

# Function to run test
run_test() {
    local service_name=$1
    local port=$2
    local locustfile=$3

    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}Testing: $service_name (Port $port)${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo -e "Users: $USERS | Spawn Rate: $SPAWN_RATE | Duration: $RUN_TIME"
    echo ""

    locust -f "$locustfile" \
        --users $USERS \
        --spawn-rate $SPAWN_RATE \
        --run-time $RUN_TIME \
        --headless \
        --csv="${RESULTS_DIR}/${service_name}" \
        --host="http://localhost:${port}" \
        --stop-timeout 30

    echo ""
    echo -e "${GREEN}✓ $service_name test completed${NC}"
    echo ""
}

# Run all tests
run_test "user-service" 8700 "locustfile-user-service.py"
run_test "product-service" 8500 "locustfile-product-service.py"
run_test "order-service" 8300 "locustfile-order-service.py"
run_test "payment-service" 8400 "locustfile-payment-service.py"

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}All Performance Tests Completed!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "Results saved to: ${YELLOW}${RESULTS_DIR}/${NC}"
echo ""
echo "To view results:"
echo -e "  ${YELLOW}cat ${RESULTS_DIR}/user-service_stats.csv${NC}"
echo -e "  ${YELLOW}cat ${RESULTS_DIR}/product-service_stats.csv${NC}"
echo -e "  ${YELLOW}cat ${RESULTS_DIR}/order-service_stats.csv${NC}"
echo -e "  ${YELLOW}cat ${RESULTS_DIR}/payment-service_stats.csv${NC}"
echo ""
