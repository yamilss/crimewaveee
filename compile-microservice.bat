@echo off
echo =====================================
echo   COMPILANDO MICROSERVICIO SPRING BOOT
echo =====================================
echo.

echo 📍 Verificando ubicación...
cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🔍 Verificando archivos necesarios...
if not exist "gradlew.bat" (
    echo ❌ Error: No se encuentra gradlew.bat en la carpeta raíz
    echo    Asegúrate de estar en: C:\Users\sekai\Downloads\crimewavee
    pause
    exit /b 1
)

if not exist "microservice\src" (
    echo ❌ Error: No se encuentra la carpeta microservice\src
    pause
    exit /b 1
)

echo ✅ Archivos encontrados correctamente

echo.
echo 🧹 Limpiando compilación anterior...
call gradlew clean

echo.
echo 📦 Compilando JAR de Spring Boot...
call gradlew :microservice:bootJar

if %errorlevel% equ 0 (
    echo.
    echo ✅ COMPILACIÓN EXITOSA!
    echo.
    echo 📋 JAR generado en:
    echo    microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo ✅ Archivo JAR confirmado
        dir "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar"
    ) else (
        echo ⚠️  Archivo JAR no encontrado en la ubicación esperada
    )
    echo.
    echo 🚀 SIGUIENTE PASO:
    echo    Ahora puedes subir el JAR a tu instancia EC2
    echo    Usa: deploy-to-aws.bat TU-IP-EC2 ruta\al\crimewave-key.pem
) else (
    echo.
    echo ❌ ERROR EN LA COMPILACIÓN
    echo    Revisa los errores mostrados arriba
)

echo.
pause
