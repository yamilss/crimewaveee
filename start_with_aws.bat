@echo off
echo =====================================
echo   CRIMEWAVE - TIENDA CON AWS RDS
echo =====================================
echo.

:menu
echo Selecciona el modo de base de datos:
echo.
echo [1] AWS RDS PostgreSQL (Producción)
echo [2] H2 Local (Desarrollo)
echo [3] Solo compilar app Android
echo [4] Ver estado del microservicio
echo [5] Salir
echo.
set /p choice="Ingresa tu opción (1-5): "

if "%choice%"=="1" goto aws_mode
if "%choice%"=="2" goto local_mode
if "%choice%"=="3" goto android_only
if "%choice%"=="4" goto check_status
if "%choice%"=="5" goto exit
goto menu

:aws_mode
echo.
echo [AWS RDS MODE] Iniciando con PostgreSQL en AWS...
echo ⚠️  ASEGÚRATE DE HABER CONFIGURADO AWS RDS PRIMERO
echo    Ver: AWS_RDS_SETUP.md para instrucciones completas
echo.
cd microservice
start "CrimeWave Microservice - AWS RDS" cmd /k "gradlew bootRun"
goto android_compile

:local_mode
echo.
echo [LOCAL MODE] Iniciando con H2 en memoria...
cd microservice
start "CrimeWave Microservice - Local H2" cmd /k "gradlew bootRun --args='--spring.profiles.active=dev'"
goto android_compile

:android_compile
echo.
echo [ANDROID] Compilando aplicación móvil...
cd ..
call gradlew assembleDebug
if %errorlevel% equ 0 (
    echo ✅ Aplicación compilada exitosamente!
    echo    APK: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo [ENDPOINTS] Microservicio disponible en:
    echo    🌐 Base URL: http://localhost:8080
    echo    📱 Ver productos: http://localhost:8080/api/products
    echo    🔧 Inicializar datos: http://localhost:8080/api/products/init-sample-data
    echo.
    echo ¿Quieres ejecutar las pruebas unitarias? (s/n)
    set /p run_tests=
    if /i "%run_tests%"=="s" (
        call gradlew test
    )
) else (
    echo ❌ Error en compilación
)
goto end

:android_only
echo.
echo [ANDROID ONLY] Compilando solo la aplicación móvil...
call gradlew assembleDebug
if %errorlevel% equ 0 (
    echo ✅ Aplicación compilada exitosamente!
    echo    APK: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo ⚠️  NOTA: Microservicio NO iniciado
    echo    La app usará datos locales (SharedPreferences)
) else (
    echo ❌ Error en compilación
)
goto end

:check_status
echo.
echo [STATUS CHECK] Verificando estado del microservicio...
curl -s -o nul -w "HTTP Status: %%{http_code}" http://localhost:8080/api/products
if %errorlevel% equ 0 (
    echo.
    echo ✅ Microservicio está corriendo correctamente
    echo.
    echo Endpoints disponibles:
    echo   GET  /api/products - Ver todos los productos
    echo   POST /api/products - Crear producto
    echo   GET  /api/products/featured - Productos destacados
    echo   GET  /api/products/in-stock - Productos con stock
    echo   POST /api/products/init-sample-data - Inicializar datos
) else (
    echo.
    echo ❌ Microservicio no está disponible en localhost:8080
    echo    Inicialo primero con la opción 1 o 2
)
echo.
pause
goto menu

:end
echo.
echo =====================================
echo       PROYECTO INICIADO
echo =====================================
pause
goto exit

:exit
echo.
echo 👋 ¡Hasta luego!
