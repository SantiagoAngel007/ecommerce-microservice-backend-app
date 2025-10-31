# Guía de Ejecución de Pruebas E2E

## 🚀 Inicio Rápido

### 1. Requisitos Previos

Asegúrate de tener:
- Java 11 o superior: `java -version`
- Maven 3.6.3+: `mvn -version`
- Git: `git --version`
- Docker (opcional, para docker-compose): `docker --version`

### 2. Clonar/Navegar al Proyecto

```bash
cd /ruta/a/ecommerce-microservice-backend-app
```

### 3. Construir el Proyecto

```bash
# Compilar todo el proyecto (puede tomar 5-10 minutos la primera vez)
mvn clean install -DskipTests=true

# O solo compilar sin instalar en el repositorio local
mvn clean compile
```

### 4. Iniciar los Microservicios

**Opción A: Usando Docker Compose (Recomendado)**
```bash
# Desde la raíz del proyecto
docker-compose up -d

# Verificar que todos los servicios están corriendo
docker-compose ps

# Ver logs de un servicio específico
docker-compose logs user-service
docker-compose logs api-gateway

# Ver todos los logs en tiempo real
docker-compose logs -f

# Detener los servicios
docker-compose down
```

**Opción B: Iniciar servicios manualmente (en terminales separadas)**
```bash
# Terminal 1: Service Discovery (Eureka)
cd service-discovery
mvn spring-boot:run

# Terminal 2: Cloud Config Server
cd cloud-config
mvn spring-boot:run

# Terminal 3: API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 4: User Service
cd user-service
mvn spring-boot:run

# Terminal 5: Product Service
cd product-service
mvn spring-boot:run

# ... y así para los demás servicios
```

### 5. Verificar que los Servicios Están Listos

```bash
# Comprobar salud del API Gateway
curl http://localhost:8080/actuator/health

# Comprobar salud de otros servicios
curl http://localhost:8700/actuator/health  # User Service
curl http://localhost:8500/actuator/health  # Product Service
curl http://localhost:8300/actuator/health  # Order Service

# Todas las respuestas deben ser:
# {"status":"UP"}
```

### 6. Ejecutar Tests E2E

```bash
# Desde la raíz del proyecto

# Ejecutar TODOS los tests E2E
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml

# Esperar a que terminen (puede tomar 5-15 minutos)
```

---

## 📋 Comandos de Ejecución Detallados

### Ejecutar por Categoría

```bash
# Ejecutar solo tests de FLUJOS (User, Product, Order, Favorite)
mvn clean test -Dgroups="e2e-flows" -f e2e-tests/pom.xml

# Ejecutar solo tests de INTEGRACIÓN (User-Product, Order-Payment-Shipping)
mvn clean test -Dgroups="e2e-integration" -f e2e-tests/pom.xml

# Ejecutar solo tests de RESILIENCIA (Circuit Breaker)
mvn clean test -Dgroups="e2e-resilience" -f e2e-tests/pom.xml
```

### Ejecutar Clases Específicas

```bash
# Ejecutar solo UserFlowE2ETest
mvn clean test -Dtest=UserFlowE2ETest -f e2e-tests/pom.xml

# Ejecutar solo ProductFlowE2ETest
mvn clean test -Dtest=ProductFlowE2ETest -f e2e-tests/pom.xml

# Ejecutar solo OrderFlowE2ETest
mvn clean test -Dtest=OrderFlowE2ETest -f e2e-tests/pom.xml

# Ejecutar solo FavouriteFlowE2ETest
mvn clean test -Dtest=FavouriteFlowE2ETest -f e2e-tests/pom.xml

# Ejecutar solo UserProductIntegrationTest
mvn clean test -Dtest=UserProductIntegrationTest -f e2e-tests/pom.xml

# Ejecutar solo OrderPaymentShippingIntegrationTest
mvn clean test -Dtest=OrderPaymentShippingIntegrationTest -f e2e-tests/pom.xml

# Ejecutar solo CircuitBreakerE2ETest
mvn clean test -Dtest=CircuitBreakerE2ETest -f e2e-tests/pom.xml
```

### Ejecutar Métodos Específicos

```bash
# Ejecutar un solo test method
mvn clean test -Dtest=UserFlowE2ETest#testUserLogin -f e2e-tests/pom.xml

# Ejecutar múltiples métodos específicos
mvn clean test -Dtest=UserFlowE2ETest#testUserLogin,UserFlowE2ETest#testUserRegistration -f e2e-tests/pom.xml
```

### Opciones Avanzadas

```bash
# Ejecutar con salida detallada (verbose)
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml -X

# Ejecutar tests en paralelo (4 hilos)
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml -DparallelTestCount=4

# Ejecutar y saltar al primer error
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml -DfailFast

# Ejecutar sin detener si hay fallos
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml --continue

# Ejecutar solo tests que fallaron anteriormente
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml -Dtest=*FlakyTest

# Con configuración de memoria
mvn -Xmx2048m -Xms1024m clean test -Dgroups="e2e" -f e2e-tests/pom.xml
```

---

## 📊 Generar Reportes

### Reportes Allure (Recomendado)

```bash
# 1. Ejecutar tests E2E (genera resultados en target/allure-results)
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml

# 2. Generar reporte Allure
mvn allure:report -f e2e-tests/pom.xml

# 3. Ver reporte en navegador
allure open e2e-tests/target/site/allure-report

# O en una sola línea:
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml && mvn allure:report -f e2e-tests/pom.xml && allure open e2e-tests/target/site/allure-report
```

### Reportes Surefire (Estándar Maven)

```bash
# Ejecutar tests
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml

# Generar reporte
mvn surefire-report:report -f e2e-tests/pom.xml

# Abrir reporte
open e2e-tests/target/site/surefire-report.html  # macOS
start e2e-tests/target/site/surefire-report.html # Windows
xdg-open e2e-tests/target/site/surefire-report.html # Linux
```

### Ver logs de tests

```bash
# Logs completos en archivo
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml > test_output.log 2>&1

# Buscar fallos específicos
grep -i "failed\|error" e2e-tests/target/surefire-reports/TEST-*.xml

# Ver detalles de un test específico
cat e2e-tests/target/surefire-reports/TEST-com.selimhorri.app.e2e.flows.UserFlowE2ETest.txt
```

---

## 🛠️ Solución de Problemas

### Problema: "Connection refused"

**Causa:** Los servicios no están corriendo

**Solución:**
```bash
# 1. Verificar que docker-compose está corriendo
docker-compose ps

# 2. Si no están corriendo, iniciarlos
docker-compose up -d

# 3. Esperar a que estén listos (30-60 segundos)
sleep 60

# 4. Verificar salud
curl http://localhost:8080/actuator/health
```

### Problema: "Address already in use"

**Causa:** Puerto ya está siendo usado

**Solución:**
```bash
# Encontrar qué está usando el puerto (ej: 8080)
# Windows:
netstat -ano | findstr :8080

# macOS/Linux:
lsof -i :8080

# Matar el proceso:
# Windows:
taskkill /PID <PID> /F

# macOS/Linux:
kill -9 <PID>

# O cambiar puerto en application.yml
```

### Problema: "Tests timed out"

**Causa:** Servicios lentos o no responden

**Solución:**
```bash
# 1. Verificar logs de servicios
docker-compose logs

# 2. Aumentar timeout en BaseE2ETest.java (línea 20+)
protected static final long TIMEOUT_MS = 30000; // aumentar a 60000

# 3. Dar más tiempo al docker-compose
# En e2e-tests-pipeline.yml, línea ~35, aumentar sleep:
sleep 60  # cambiar a 120
```

### Problema: "Tests passed but no results"

**Causa:** Resultados no generados

**Solución:**
```bash
# Verificar estructura de directorios
ls -la e2e-tests/target/surefire-reports/

# Limpiar y reintentar
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml -DfailIfNoTests=false
```

### Problema: Allure no encuentra resultados

**Solución:**
```bash
# 1. Verificar que existe el directorio
ls e2e-tests/target/allure-results/

# 2. Limpiar reportes anteriores
rm -rf e2e-tests/target/allure-report

# 3. Regenerar reporte
mvn allure:report -f e2e-tests/pom.xml -Dallure.results.directory=e2e-tests/target/allure-results
```

---

## 🔄 Integración Continua (CI/CD)

### GitHub Actions

Los tests E2E se ejecutan automáticamente en:
- Push a `master`, `develop`, o `stage`
- Pull requests
- Trigger manual (workflow_dispatch)

Ver pipeline: `.github/workflows/e2e-tests-pipeline.yml`

Verificar estado:
```bash
# En GitHub
1. Ir a tu repo
2. Click en "Actions"
3. Ver "E2E Tests Pipeline"
```

### Ejecutar localmente como CI

```bash
# Simular lo que hace el pipeline
docker-compose down  # Limpiar
docker-compose up -d  # Iniciar servicios
sleep 60  # Esperar
mvn clean install -DskipTests=true  # Build
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml  # Tests E2E
mvn allure:report -f e2e-tests/pom.xml  # Reportes
docker-compose down  # Limpiar
```

---

## 📈 Monitorear Progreso

### Durante la ejecución

```bash
# En otra terminal, ver logs de un servicio
docker-compose logs -f user-service

# O todos los servicios
docker-compose logs -f

# Ver estado de containers
docker-compose ps

# Ver métricas
curl http://localhost:8080/actuator/metrics
```

### Después de la ejecución

```bash
# Resumen de resultados
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml -q

# Detalles de fallos
grep -i "failed" e2e-tests/target/surefire-reports/*.xml

# Contar tests ejecutados
grep -c "testcase" e2e-tests/target/surefire-reports/*.xml
```

---

## 💡 Tips y Trucos

### 1. Crear alias para comandos largos

```bash
# En ~/.bashrc o ~/.zshrc
alias e2e-tests='mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml'
alias e2e-flows='mvn clean test -Dgroups="e2e-flows" -f e2e-tests/pom.xml'
alias e2e-report='mvn allure:report -f e2e-tests/pom.xml && allure open e2e-tests/target/site/allure-report'

# Usar:
e2e-tests
e2e-flows
e2e-report
```

### 2. Script para ejecutar todo

```bash
#!/bin/bash
# save as run-e2e-tests.sh

set -e  # Exit on error

echo "🚀 Starting E2E Tests..."
echo "1️⃣ Starting services..."
docker-compose up -d
sleep 60

echo "2️⃣ Running tests..."
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml

echo "3️⃣ Generating report..."
mvn allure:report -f e2e-tests/pom.xml

echo "4️⃣ Opening report..."
allure open e2e-tests/target/site/allure-report

echo "✅ Done!"

# Usar:
chmod +x run-e2e-tests.sh
./run-e2e-tests.sh
```

### 3. Ejecutar tests en VS Code

Instalar extensión "Test Explorer UI" y "Maven Test Explorer"

### 4. Ejecutar tests en IDE

**IntelliJ IDEA:**
1. Right-click en clase de test
2. "Run" o "Run with Coverage"
3. Ver resultados en panel de tests

**Eclipse:**
1. Right-click en clase de test
2. "Run As" > "JUnit Test"

**VS Code:**
1. Instalar "Java Test Runner"
2. Click en "Run" o "Debug" sobre el test

---

## 📚 Documentación Adicional

- **Descripción completa:** [e2e-tests/README.md](e2e-tests/README.md)
- **Resumen de implementación:** [E2E_TESTS_SUMMARY.md](E2E_TESTS_SUMMARY.md)
- **Código de tests:** [e2e-tests/src/test/java](e2e-tests/src/test/java)

---

## ✨ Ejemplo Completo

Ejecutar todo de una vez (en Windows):

```batch
@echo off
REM Navegar al proyecto
cd /d C:\Users\USUARIO\Documents\8voSemestre\Soft5\Taller2\OPS2\ecommerce-microservice-backend-app

REM Limpiar y construir
mvn clean install -DskipTests=true

REM Iniciar servicios
docker-compose up -d
timeout /t 60 /nobreak

REM Ejecutar tests
mvn clean test -Dgroups="e2e" -f e2e-tests/pom.xml

REM Generar reporte
mvn allure:report -f e2e-tests/pom.xml

REM Abrir reporte
start e2e-tests\target\site\allure-report\index.html

REM Detener servicios
docker-compose down

echo Tests completed!
pause
```

---

**Versión:** 1.0
**Actualizado:** 2024-10-30
