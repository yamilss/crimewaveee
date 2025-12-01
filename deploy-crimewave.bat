@echo off
REM Script de despliegue rápido para CrimeWave
REM Uso: deploy-crimewave.bat

echo 🚀 Iniciando despliegue de CrimeWave...
echo.

REM Construir el JAR
echo 📦 Construyendo JAR...
call gradlew.bat :microservice:bootJar
if %errorlevel% neq 0 (
    echo ❌ Error al construir el JAR
    pause
    exit /b 1
)

REM Verificar que el JAR se creó
if not exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ❌ No se pudo generar el JAR
    pause
    exit /b 1
)

echo ✅ JAR construido exitosamente
echo.

echo 📋 Archivos listos para despliegue:
echo    - microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
echo    - microservice\application-aws.properties
echo.

echo 📤 Para completar el despliegue necesitas:
echo.
echo 1. Tener tu archivo .pem de AWS
echo 2. Ejecutar uno de estos comandos:
echo.
echo    Con Git Bash:
echo    ./deploy-to-aws.sh 3.21.53.102 /ruta/a/tu-archivo.pem
echo.
echo    O subir manualmente por SCP:
echo    scp -i "tu-archivo.pem" "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" ubuntu@3.21.53.102:/home/ubuntu/
echo    scp -i "tu-archivo.pem" "microservice\application-aws.properties" ubuntu@3.21.53.102:/home/ubuntu/
echo.
echo 3. Conectar por SSH y ejecutar:
echo    ssh -i "tu-archivo.pem" ubuntu@3.21.53.102
echo    nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties ^> ~/logs/crimewave.log 2^>^&1 ^&
echo.

pause
