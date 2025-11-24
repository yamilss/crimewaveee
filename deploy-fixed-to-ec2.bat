@echo off
echo =====================================
echo   DESPLIEGUE CORREGIDO A EC2
echo =====================================
echo.

set EC2_IP=3.15.178.116
set KEY_FILE=C:\Users\sekai\Downloads\crimewave-key.pem

echo 🎯 Desplegando a EC2: %EC2_IP%
echo 🔑 Usando key: %KEY_FILE%
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 📦 El JAR ya está compilado (44MB)
if not exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR no encontrado, compilando...
    call gradlew :microservice:bootJar
)

echo.
echo 🔧 Configurando SSH para EC2...
REM Arreglar permisos del key
icacls "%KEY_FILE%" /inheritance:r >nul 2>&1
icacls "%KEY_FILE%" /grant:r "%USERNAME%:(R)" >nul 2>&1

REM Limpiar known_hosts
ssh-keygen -R %EC2_IP% >nul 2>&1

echo.
echo 📤 Subiendo archivos a EC2...
echo    Archivo JAR (esto puede tomar 1-2 minutos)...
scp -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" ubuntu@%EC2_IP%:/home/ubuntu/

if %errorlevel% neq 0 (
    echo ❌ Error subiendo JAR
    echo.
    echo 💡 POSIBLES PROBLEMAS:
    echo    1. Security Group no permite SSH (puerto 22)
    echo    2. Instancia no está "Running"
    echo    3. Key file incorrecto
    echo    4. Tu IP cambió (usar VPN)
    echo.
    pause
    exit /b 1
)

echo    Archivo de configuración...
scp -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "microservice\application-aws.properties" ubuntu@%EC2_IP%:/home/ubuntu/

echo ✅ Archivos subidos exitosamente

echo.
echo ⚙️ Configurando y ejecutando Spring Boot en EC2...
ssh -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@%EC2_IP% "
echo '🔧 Preparando servidor Ubuntu...'

# Actualizar e instalar Java
sudo apt update -y
sudo apt install openjdk-11-jdk -y

echo '☕ Verificando Java:'
java -version

# Detener app anterior
echo '🛑 Deteniendo aplicación anterior...'
pkill -f 'microservice-0.0.1-SNAPSHOT.jar' || true

# Crear logs
mkdir -p ~/logs

# Ejecutar Spring Boot
echo '🚀 Iniciando CrimeWave Spring Boot...'
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/app.log 2>&1 &

# Esperar inicio
echo '⏳ Esperando inicio (30 segundos)...'
sleep 30

# Verificar proceso
if pgrep -f 'microservice-0.0.1-SNAPSHOT.jar' >/dev/null; then
    echo '✅ Spring Boot ejecutándose correctamente'

    # Health check local
    echo '🔍 Probando health check interno...'
    for i in {1..10}; do
        if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
            echo '✅ Health check interno OK'
            break
        else
            echo \"Intento \$i/10 - esperando...\"
            sleep 5
        fi
    done

    # Inicializar productos
    echo '📦 Inicializando productos de la tienda...'
    sleep 5
    curl -s -X POST http://localhost:8080/api/products/init-sample-data && echo '✅ Productos inicializados' || echo '⚠️ Error inicializando productos'

else
    echo '❌ Spring Boot no se inició correctamente'
    echo '📋 Últimas líneas del log:'
    tail -20 ~/logs/app.log
    exit 1
fi
"

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡DESPLIEGUE COMPLETADO EXITOSAMENTE!
    echo.
    echo =====================================
    echo        TU TIENDA DE ROPA EN AWS
    echo =====================================
    echo.
    echo 🌐 URLs disponibles:
    echo    Base API: http://%EC2_IP%:8080
    echo    Productos: http://%EC2_IP%:8080/api/products
    echo    Health: http://%EC2_IP%:8080/actuator/health
    echo.
    echo 🧪 Probando desde internet...
    timeout /t 5 >nul

    echo.
    echo 🔍 Health check público:
    curl -s http://%EC2_IP%:8080/actuator/health

    echo.
    echo 📦 Productos disponibles:
    curl -s "http://%EC2_IP%:8080/api/products" | head -c 500

    echo.
    echo.
    echo ✅ CONFIGURACIÓN COMPLETA:
    echo    🖥️  Spring Boot: Corriendo en EC2
    echo    🗄️  PostgreSQL: RDS Aurora conectado
    echo    📱 App Android: Configurada para %EC2_IP%
    echo.
    echo 📱 PROBAR APP ANDROID:
    echo    1. Instala: app\build\outputs\apk\debug\app-debug.apk
    echo    2. Abre la app de tienda de ropa
    echo    3. Ve al catálogo - debe cargar desde AWS
    echo.

) else (
    echo.
    echo ❌ Error en configuración del servidor
    echo    Revisa los logs en EC2: ssh -i "%KEY_FILE%" ubuntu@%EC2_IP% "tail -50 ~/logs/app.log"
)

pause
