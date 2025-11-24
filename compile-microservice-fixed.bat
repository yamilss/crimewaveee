@echo off
echo =====================================
echo   COMPILANDO MICROSERVICIO - INTENTO 2
echo =====================================
echo.

echo 📍 Cambiando a carpeta del proyecto...
cd /d "C:\Users\sekai\Downloads\crimewavee"

echo.
echo 🧹 Limpiando compilación anterior...
call gradlew clean

if %errorlevel% neq 0 (
    echo ❌ Error en gradle clean
    pause
    exit /b 1
)

echo.
echo 📦 Compilando microservicio Spring Boot...
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
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do echo    Tamaño: %%~zI bytes
    ) else (
        echo ⚠️  Archivo JAR no encontrado
    )
    echo.
    echo 🚀 SIGUIENTE PASO:
    echo    El JAR está listo para subir a AWS EC2
    echo    Necesitas tu IP de EC2 y el archivo crimewave-key.pem
) else (
    echo.
    echo ❌ ERROR EN LA COMPILACIÓN
    echo    Revisa los errores de Gradle mostrados arriba
)

echo.
pause
