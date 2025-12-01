@echo off
echo =============================================
echo     SCRIPT CORREGIDO - DEPLOY CRIMEWAVE
echo =============================================
echo.

echo 1. Navegando a la carpeta del proyecto...
cd /d "C:\Users\sekai\OneDrive\Documents\GitHub\crimewaveee"
echo Carpeta actual: %CD%

echo.
echo 2. Verificando archivos necesarios...
if exist "gradlew.bat" (
    echo ✅ gradlew.bat encontrado
) else (
    echo ❌ gradlew.bat NO encontrado
    pause
    exit /b 1
)

if exist "microservice\build.gradle.kts" (
    echo ✅ microservice\build.gradle.kts encontrado
) else (
    echo ❌ microservice\build.gradle.kts NO encontrado
    pause
    exit /b 1
)

echo.
echo 3. Construyendo JAR...
echo Ejecutando: gradlew.bat :microservice:bootJar
call gradlew.bat :microservice:bootJar

if %errorlevel% neq 0 (
    echo ❌ Error al construir el JAR (código: %errorlevel%)
    echo.
    echo Posibles soluciones:
    echo - Verificar que Java esté instalado
    echo - Ejecutar: gradlew.bat clean :microservice:bootJar
    echo - Verificar conexión a internet (para descargar dependencias)
    pause
    exit /b 1
)

echo.
echo 4. Verificando que el JAR se creó...
if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ✅ JAR creado exitosamente
    echo Ubicación: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
) else (
    echo ❌ JAR NO se creó
    pause
    exit /b 1
)

echo.
echo 5. Verificando archivo de configuración...
if exist "microservice\application-aws.properties" (
    echo ✅ Archivo de configuración encontrado
) else (
    echo ❌ application-aws.properties NO encontrado
    pause
    exit /b 1
)

echo.
echo =============================================
echo ✅ CONSTRUCCIÓN EXITOSA
echo =============================================
echo.
echo 📋 Archivos listos para despliegue:
echo    JAR: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
echo    Config: microservice\application-aws.properties
echo.
echo 📤 Siguiente paso - Desplegar en AWS EC2:
echo.
echo OPCIÓN 1 - Con Git Bash:
echo    bash deploy-to-aws.sh 3.21.53.102 "ruta\a\tu-archivo.pem"
echo.
echo OPCIÓN 2 - Manual con SCP:
echo    scp -i "tu-archivo.pem" microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar ubuntu@3.21.53.102:/home/ubuntu/
echo    scp -i "tu-archivo.pem" microservice\application-aws.properties ubuntu@3.21.53.102:/home/ubuntu/
echo.
echo OPCIÓN 3 - Conectar por SSH y ejecutar:
echo    ssh -i "tu-archivo.pem" ubuntu@3.21.53.102
echo    mkdir -p ~/logs
echo    nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties ^> ~/logs/crimewave.log 2^>^&1 ^&
echo.
echo 🧪 Para probar después del despliegue:
echo    curl http://3.21.53.102:8080/api/products
echo    POST http://3.21.53.102:8080/api/products/init-sample-data (en Postman)
echo.

pause
