@echo off
echo =====================================
echo   DESPLIEGUE COMPLETO A AWS EC2 + RDS
echo =====================================
echo.

if "%~2"=="" (
    echo ❌ Uso incorrecto
    echo.
    echo 📋 Uso: %0 ^<IP-EC2^> ^<RUTA-KEY^>
    echo    Ejemplo: %0 54.123.45.67 C:\Users\sekai\Downloads\crimewave-key.pem
    echo.
    echo 🔍 ¿No tienes EC2 aún?
    echo    1. Ve a AWS Console → EC2 → Launch Instance
    echo    2. Usa Ubuntu 22.04 LTS + t3.micro (Free Tier)
    echo    3. Descarga crimewave-key.pem
    echo    4. Configura Security Group: SSH(22), HTTP(80), TCP(8080)
    echo    5. Anota la IP pública y ejecútame de nuevo
    echo.
    pause
    exit /b 1
)

set EC2_IP=%1
set KEY_FILE=%2

echo 🎯 CONFIGURACIÓN:
echo    EC2 IP: %EC2_IP%
echo    Key File: %KEY_FILE%
echo    RDS: crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 📦 PASO 1: Compilando microservicio...
call gradlew :microservice:clean :microservice:bootJar

if %errorlevel% neq 0 (
    echo ❌ Error compilando JAR
    pause
    exit /b 1
)

if not exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR no generado
    pause
    exit /b 1
)

echo ✅ JAR compilado exitosamente

echo.
echo 🔍 PASO 2: Verificando conectividad SSH...
ssh -i "%KEY_FILE%" -o ConnectTimeout=10 -o BatchMode=yes ubuntu@%EC2_IP% exit
if %errorlevel% neq 0 (
    echo ❌ No se puede conectar a EC2
    echo    Verifica:
    echo    - IP correcta: %EC2_IP%
    echo    - Key file correcto: %KEY_FILE%
    echo    - Security Group permite SSH puerto 22
    pause
    exit /b 1
)

echo ✅ Conexión SSH exitosa

echo.
echo 📤 PASO 3: Subiendo archivos a EC2...
scp -i "%KEY_FILE%" "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" ubuntu@%EC2_IP%:/home/ubuntu/
scp -i "%KEY_FILE%" "microservice\application-aws.properties" ubuntu@%EC2_IP%:/home/ubuntu/

if %errorlevel% neq 0 (
    echo ❌ Error subiendo archivos
    pause
    exit /b 1
)

echo ✅ Archivos subidos exitosamente

echo.
echo ⚙️ PASO 4: Configurando y ejecutando en EC2...
ssh -i "%KEY_FILE%" ubuntu@%EC2_IP% "
echo '🔧 Configurando servidor EC2...'

# Actualizar sistema
sudo apt update -y

# Instalar Java si no está
if ! command -v java >/dev/null 2>&1; then
    echo '📥 Instalando Java 11...'
    sudo apt install openjdk-11-jdk -y
fi

echo '☕ Java version:'
java -version

# Detener proceso anterior si existe
echo '🛑 Deteniendo aplicación anterior...'
pkill -f 'microservice-0.0.1-SNAPSHOT.jar' || true

# Crear directorio logs
mkdir -p ~/logs

# Ejecutar aplicación
echo '🚀 Iniciando CrimeWave Spring Boot...'
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

# Esperar que inicie
sleep 15

# Verificar que está corriendo
if pgrep -f 'microservice-0.0.1-SNAPSHOT.jar' >/dev/null; then
    echo '✅ Aplicación iniciada correctamente'

    # Probar health check
    echo '🔍 Probando health check...'
    sleep 5
    if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
        echo '✅ Health check exitoso'
    else
        echo '⚠️ Health check falló, pero la app puede estar iniciando aún'
    fi

    # Inicializar datos de productos
    echo '📦 Inicializando datos de productos...'
    sleep 3
    curl -s -X POST http://localhost:8080/api/products/init-sample-data || echo '⚠️ Inicialización puede haber fallado'

else
    echo '❌ Error: La aplicación no se inició'
    echo '📋 Últimas líneas del log:'
    tail -20 ~/logs/crimewave.log
    exit 1
fi
"

if %errorlevel% neq 0 (
    echo ❌ Error configurando EC2
    pause
    exit /b 1
)

echo.
echo 🧪 PASO 5: Probando endpoints...
timeout /t 5 >nul

echo.
echo 🔍 Health check:
curl -s http://%EC2_IP%:8080/actuator/health

echo.
echo 📦 Productos disponibles:
curl -s http://%EC2_IP%:8080/api/products

echo.
echo 🎉 ¡DESPLIEGUE COMPLETADO!
echo.
echo =====================================
echo       INFORMACIÓN DEL SERVIDOR
echo =====================================
echo.
echo 🌐 URL Base: http://%EC2_IP%:8080
echo 📱 API Productos: http://%EC2_IP%:8080/api/products
echo 🔍 Health Check: http://%EC2_IP%:8080/actuator/health
echo 🗄️ RDS Database: crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com
echo.
echo =====================================
echo          PRÓXIMO PASO
echo =====================================
echo.
echo 📱 ACTUALIZAR APP ANDROID:
echo    1. Abrir: ServerConfig.kt
echo    2. Cambiar: AWS_EC2 = "http://%EC2_IP%:8080/"
echo    3. Compilar: gradlew :app:assembleDebug
echo    4. Probar la app conectada a AWS
echo.

pause
