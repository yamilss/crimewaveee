@echo off
echo =====================================
echo   COMPILANDO MICROSERVICIO SPRING BOOT
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🔍 Verificando configuración del microservicio...
if exist "microservice\application-aws.properties" (
    echo ✅ Configuración AWS encontrada
) else (
    echo ⚠️  Configuración AWS no encontrada, usando configuración por defecto
)

echo.
echo 🧹 Limpiando compilación anterior...
call gradlew :microservice:clean

if %errorlevel% neq 0 (
    echo ❌ Error en clean
    pause
    exit /b 1
)

echo.
echo 📦 Compilando JAR del microservicio...
call gradlew :microservice:bootJar

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡MICROSERVICIO COMPILADO EXITOSAMENTE!
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo 📁 JAR generado: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do (
            echo    📏 Tamaño: %%~zI bytes
        )
        echo.
        echo 🚀 SIGUIENTE PASO - DESPLIEGUE A AWS EC2:
        echo    1. Crear instancia EC2 t3.micro (Free Tier)
        echo    2. Configurar RDS PostgreSQL
        echo    3. Ejecutar: deploy-to-aws.bat TU-IP-EC2 ruta\key.pem
        echo.
        echo 📖 Guía completa: AWS_EC2_RDS_COMPLETE_GUIDE.md
    ) else (
        echo ❌ JAR no encontrado después de compilación exitosa
    )
) else (
    echo.
    echo ❌ ERROR EN COMPILACIÓN DEL MICROSERVICIO
    echo    Revisa los errores de Gradle mostrados arriba
)

echo.
pause
