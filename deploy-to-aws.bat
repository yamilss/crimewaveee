@echo off
setlocal enabledelayedexpansion

REM Script para desplegar CrimeWave a AWS EC2 desde Windows
REM Requiere: Git Bash o WSL instalado

if "%~2"=="" (
    echo Uso: %0 ^<IP-EC2^> ^<ruta-al-key.pem^>
    echo Ejemplo: %0 54.123.45.67 C:\Users\usuario\Downloads\crimewave-key.pem
    exit /b 1
)

set EC2_IP=%1
set KEY_FILE=%2
set EC2_USER=ubuntu

echo =====================================
echo   DESPLEGANDO CRIMEWAVE A AWS EC2
echo =====================================
echo IP EC2: %EC2_IP%
echo Key File: %KEY_FILE%
echo.

REM Verificar que existe el archivo key
if not exist "%KEY_FILE%" (
    echo ❌ Error: No se encuentra el archivo key: %KEY_FILE%
    exit /b 1
)

REM Construir el JAR
echo 📦 Construyendo JAR de Spring Boot...
echo    PASO 1: Compilando microservicio...
cd microservice
call gradlew clean bootJar
if %errorlevel% neq 0 (
    echo ❌ Error al construir el JAR
    echo    Verifica que Gradle esté funcionando correctamente
    pause
    exit /b 1
)
cd ..

echo ✅ JAR construido: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar

set JAR_FILE=microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
if not exist "%JAR_FILE%" (
    echo ❌ Error: No se pudo generar el JAR
    exit /b 1
)

echo ✅ JAR generado exitosamente

REM Verificar conectividad SSH
echo 🔍 Verificando conexión SSH...
ssh -i "%KEY_FILE%" -o ConnectTimeout=10 -o BatchMode=yes %EC2_USER%@%EC2_IP% exit
if %errorlevel% neq 0 (
    echo ❌ Error: No se puede conectar a EC2. Verifica:
    echo   - La IP de EC2 está correcta
    echo   - El archivo .pem tiene permisos correctos
    echo   - El Security Group permite SSH (puerto 22)
    exit /b 1
)

echo ✅ Conexión SSH exitosa

REM Subir archivos
echo 📤 Subiendo JAR y configuración a EC2...
scp -i "%KEY_FILE%" "%JAR_FILE%" %EC2_USER%@%EC2_IP%:/home/ubuntu/
if %errorlevel% neq 0 (
    echo ❌ Error al subir JAR
    exit /b 1
)

scp -i "%KEY_FILE%" "microservice\application-aws.properties" %EC2_USER%@%EC2_IP%:/home/ubuntu/
if %errorlevel% neq 0 (
    echo ❌ Error al subir configuración
    exit /b 1
)

echo ✅ Archivos subidos exitosamente

REM Configurar y ejecutar en EC2
echo ⚙️ Configurando y ejecutando en EC2...
ssh -i "%KEY_FILE%" %EC2_USER%@%EC2_IP% "
echo 'Configurando servidor...'

# Matar proceso anterior
pkill -f 'microservice-0.0.1-SNAPSHOT.jar' || true

# Verificar Java
if ! command -v java >/dev/null 2>&1; then
    echo 'Instalando Java...'
    sudo apt update -y
    sudo apt install openjdk-11-jdk -y
fi

# Mostrar versión de Java
java -version

# Crear directorio de logs
mkdir -p ~/logs

# Ejecutar aplicación
echo 'Iniciando CrimeWave Spring Boot...'
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

# Esperar que inicie
sleep 10

# Verificar que está corriendo
if pgrep -f 'microservice-0.0.1-SNAPSHOT.jar' >/dev/null; then
    echo 'Aplicación iniciada correctamente'

    # Probar endpoint
    sleep 5
    curl -s http://localhost:8080/actuator/health || echo 'Advertencia: Health check falló'
else
    echo 'Error: La aplicación no se inició correctamente'
    echo 'Últimas líneas del log:'
    tail -20 ~/logs/crimewave.log
    exit 1
fi
"

if %errorlevel% neq 0 (
    echo ❌ Error durante la configuración en EC2
    exit /b 1
)

echo.
echo 🎉 ¡DESPLIEGUE COMPLETADO EXITOSAMENTE!
echo.
echo =====================================
echo        INFORMACIÓN DEL SERVIDOR
echo =====================================
echo.
echo 🌐 URL Base: http://%EC2_IP%:8080
echo 📱 API Productos: http://%EC2_IP%:8080/api/products
echo 🔍 Health Check: http://%EC2_IP%:8080/actuator/health
echo.
echo =====================================
echo           COMANDOS ÚTILES
echo =====================================
echo.
echo 🧪 Probar API:
echo    curl http://%EC2_IP%:8080/api/products
echo.
echo 🚀 Inicializar datos:
echo    curl -X POST http://%EC2_IP%:8080/api/products/init-sample-data
echo.
echo 📋 Ver logs en tiempo real:
echo    ssh -i "%KEY_FILE%" %EC2_USER%@%EC2_IP% "tail -f ~/logs/crimewave.log"
echo.
echo 🔄 Reiniciar aplicación:
echo    ssh -i "%KEY_FILE%" %EC2_USER%@%EC2_IP% "pkill -f microservice && nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &"
echo.

REM Probar endpoints automáticamente
echo 🔍 Probando endpoints...
timeout /t 3 >nul
curl -s http://%EC2_IP%:8080/actuator/health
if %errorlevel% equ 0 (
    echo ✅ Servidor respondiendo correctamente
) else (
    echo ⚠️  Advertencia: No se pudo conectar al servidor
    echo    Espera 1-2 minutos más y prueba manualmente
)

echo.
echo 📝 Recuerda actualizar tu app Android:
echo    En ProductRepository.kt cambiar baseUrl a: "http://%EC2_IP%:8080/"
echo.

pause
