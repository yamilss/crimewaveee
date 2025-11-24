@echo off
echo =====================================
echo     CRIMEWAVE - PROYECTO COMPLETO
echo =====================================
echo.

echo [1/5] Verificando prerequisites...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java no encontrado. Instalar JDK 11+
    pause
    exit /b 1
)
echo ✓ Java encontrado

echo.
echo [2/5] Compilando microservicio Spring Boot...
cd microservice
call gradlew build
if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion del microservicio
    pause
    exit /b 1
)
echo ✓ Microservicio compilado exitosamente

echo.
echo [3/5] Iniciando microservicio en puerto 8080...
start "CrimeWave Microservice" cmd /k "gradlew bootRun"
echo ✓ Microservicio iniciado (verificar en http://localhost:8080/api/crime-reports)
timeout /t 5 >nul

echo.
echo [4/5] Compilando aplicacion Android...
cd ..
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion de la app Android
    pause
    exit /b 1
)
echo ✓ Aplicacion Android compilada

echo.
echo [5/5] Ejecutando pruebas unitarias...
call gradlew test
echo ✓ Pruebas unitarias completadas

echo.
echo =====================================
echo        PROYECTO INICIADO
echo =====================================
echo.
echo Microservicio: http://localhost:8080
echo API Endpoints: http://localhost:8080/api/crime-reports
echo H2 Console: http://localhost:8080/h2-console
echo.
echo APK Debug: app\build\outputs\apk\debug\app-debug.apk
echo.
echo Para generar APK firmado: gradlew assembleRelease
echo.
pause
