@echo off
REM Performance Tests Runner Script for Windows
REM Runs Locust tests sequentially for all 5 services

setlocal enabledelayedexpansion

REM Configuration
set USERS=10
set SPAWN_RATE=2
set RUN_TIME=60s
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
set TIMESTAMP=%mydate%_%mytime%
set RESULTS_DIR=results_%TIMESTAMP%

echo.
echo ========================================
echo Performance Tests for Microservices
echo ========================================
echo.

REM Check if services are running
echo Checking services...

REM User Service
echo Checking User Service on port 8700...
curl -s http://localhost:8700/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] User Service is running
) else (
    echo [FAIL] User Service is NOT running
    goto services_not_running
)

REM Product Service
echo Checking Product Service on port 8500...
curl -s http://localhost:8500/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Product Service is running
) else (
    echo [FAIL] Product Service is NOT running
    goto services_not_running
)

REM Order Service
echo Checking Order Service on port 8300...
curl -s http://localhost:8300/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Order Service is running
) else (
    echo [FAIL] Order Service is NOT running
    goto services_not_running
)

REM Payment Service
echo Checking Payment Service on port 8400...
curl -s http://localhost:8400/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Payment Service is running
) else (
    echo [FAIL] Payment Service is NOT running
    goto services_not_running
)

echo.
echo All services are running!
echo.
echo Creating results directory: %RESULTS_DIR%
mkdir %RESULTS_DIR%

REM Test 1: User Service
echo.
echo ========================================
echo Testing: User Service (Port 8700)
echo ========================================
locust -f locustfile-user-service.py ^
    --users %USERS% ^
    --spawn-rate %SPAWN_RATE% ^
    --run-time %RUN_TIME% ^
    --headless ^
    --csv=%RESULTS_DIR%\user-service ^
    --host=http://localhost:8700 ^
    --stop-timeout 30

echo.
echo [OK] User Service test completed
echo.

REM Test 2: Product Service
echo.
echo ========================================
echo Testing: Product Service (Port 8500)
echo ========================================
locust -f locustfile-product-service.py ^
    --users %USERS% ^
    --spawn-rate %SPAWN_RATE% ^
    --run-time %RUN_TIME% ^
    --headless ^
    --csv=%RESULTS_DIR%\product-service ^
    --host=http://localhost:8500 ^
    --stop-timeout 30

echo.
echo [OK] Product Service test completed
echo.

REM Test 3: Order Service
echo.
echo ========================================
echo Testing: Order Service (Port 8300)
echo ========================================
locust -f locustfile-order-service.py ^
    --users %USERS% ^
    --spawn-rate %SPAWN_RATE% ^
    --run-time %RUN_TIME% ^
    --headless ^
    --csv=%RESULTS_DIR%\order-service ^
    --host=http://localhost:8300 ^
    --stop-timeout 30

echo.
echo [OK] Order Service test completed
echo.

REM Test 4: Payment Service
echo.
echo ========================================
echo Testing: Payment Service (Port 8400)
echo ========================================
locust -f locustfile-payment-service.py ^
    --users %USERS% ^
    --spawn-rate %SPAWN_RATE% ^
    --run-time %RUN_TIME% ^
    --headless ^
    --csv=%RESULTS_DIR%\payment-service ^
    --host=http://localhost:8400 ^
    --stop-timeout 30

echo.
echo [OK] Payment Service test completed
echo.

REM Summary
echo.
echo ========================================
echo All Performance Tests Completed!
echo ========================================
echo.
echo Results saved to: %RESULTS_DIR%
echo.
echo To view results:
echo   type %RESULTS_DIR%\user-service_stats.csv
echo   type %RESULTS_DIR%\product-service_stats.csv
echo   type %RESULTS_DIR%\order-service_stats.csv
echo   type %RESULTS_DIR%\payment-service_stats.csv
echo.
pause
exit /b 0

:services_not_running
echo.
echo [ERROR] Some services are not running.
echo Please start them first: docker-compose up -d
echo.
pause
exit /b 1
