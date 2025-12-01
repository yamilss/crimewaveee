@echo off
echo =============================================
echo   VERIFICACIÓN DE ENTORNO - CRIMEWAVE
echo =============================================
echo.

echo 1. Verificando Java...
java -version 2>nul
if %errorlevel% equ 0 (
    echo ✅ Java está instalado
    java -version
) else (
    echo ❌ Java NO está instalado o no está en PATH
    echo Instala Java 11 o superior desde: https://adoptopenjdk.net/
    echo.
    pause
    exit /b 1
)

echo.
echo 2. Verificando ubicación actual...
echo Carpeta actual: %CD%
echo.

echo 3. Verificando estructura del proyecto...
if exist "gradlew.bat" (
    echo ✅ gradlew.bat encontrado
) else (
    echo ❌ gradlew.bat NO encontrado
    echo Asegúrate de estar en la carpeta raíz del proyecto
)

if exist "microservice" (
    echo ✅ Carpeta microservice encontrada
) else (
    echo ❌ Carpeta microservice NO encontrada
)

if exist "microservice\build.gradle.kts" (
    echo ✅ build.gradle.kts del microservicio encontrado
) else (
    echo ❌ build.gradle.kts del microservicio NO encontrado
)

if exist "microservice\src" (
    echo ✅ Carpeta src del microservicio encontrada
) else (
    echo ❌ Carpeta src del microservicio NO encontrada
)

echo.
echo 4. Verificando permisos de Gradle...
gradlew.bat --version 2>nul
if %errorlevel% equ 0 (
    echo ✅ Gradle Wrapper funciona correctamente
) else (
    echo ❌ Problema con Gradle Wrapper
    echo Intentando limpiar...
    gradlew.bat clean
)

echo.
echo 5. Verificando conexión a Internet...
ping -n 1 google.com >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Conexión a Internet disponible
) else (
    echo ⚠️ Sin conexión a Internet (puede afectar descarga de dependencias)
)

echo.
echo =============================================
echo ✅ VERIFICACIÓN COMPLETADA
echo =============================================
echo.
echo Si todo está OK, ejecuta: build-jar.bat
echo.

pause
