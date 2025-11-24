@echo off
echo =====================================
echo   COMPILANDO MICROSERVICIO - LIMPIEZA COMPLETA
echo =====================================
echo.

echo 📍 Cambiando a carpeta del proyecto...
cd /d "C:\Users\sekai\Downloads\crimewavee"

echo.
echo 🧹 Limpiando cache de Gradle completamente...
if exist ".gradle" (
    echo    Eliminando cache .gradle...
    rmdir /s /q ".gradle"
)

if exist "microservice\build" (
    echo    Eliminando build del microservicio...
    rmdir /s /q "microservice\build"
)

echo.
echo 🔄 Deteniendo daemon de Gradle...
call gradlew --stop

echo.
echo 🧹 Limpiando proyecto...
call gradlew clean

if %errorlevel% neq 0 (
    echo ❌ Error en gradle clean
    pause
    exit /b 1
)

echo.
echo 📦 Compilando microservicio Spring Boot...
call gradlew :microservice:bootJar --refresh-dependencies

if %errorlevel% equ 0 (
    echo.
    echo ✅ COMPILACIÓN EXITOSA!
    echo.
    echo 📋 JAR generado en:
    echo    microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo ✅ Archivo JAR confirmado
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do (
            echo    Tamaño: %%~zI bytes
            echo    Creado: %%~tI
        )
    ) else (
        echo ⚠️  Archivo JAR no encontrado
        echo    Verificando contenido de build/libs:
        if exist "microservice\build\libs" (
            dir "microservice\build\libs"
        ) else (
            echo    Directorio build/libs no existe
        )
    )
    echo.
    echo 🚀 SIGUIENTE PASO:
    echo    El JAR está listo para subir a AWS EC2
    echo    Usa: deploy-to-aws.bat TU-IP-EC2 ruta\al\crimewave-key.pem
) else (
    echo.
    echo ❌ ERROR EN LA COMPILACIÓN
    echo    Revisa los errores de Gradle mostrados arriba
    echo.
    echo 🔍 INFORMACIÓN DE DEBUG:
    echo    Verificando configuración del proyecto...
    if exist "microservice\build.gradle.kts" (
        echo    ✓ microservice\build.gradle.kts existe
    ) else (
        echo    ✗ microservice\build.gradle.kts NO existe
    )

    if exist "settings.gradle.kts" (
        echo    ✓ settings.gradle.kts existe
    ) else (
        echo    ✗ settings.gradle.kts NO existe
    )
)

echo.
pause
