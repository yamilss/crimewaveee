@echo off
echo ================================
echo   VERIFICANDO COMPILACION
echo ================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo [1/3] Limpiando proyecto...
call gradlew clean >nul 2>&1

echo [2/3] Compilando proyecto...
call gradlew assembleDebug --stacktrace

if %errorlevel% equ 0 (
    echo.
    echo ✅ COMPILACION EXITOSA!
    echo.
    echo APK generado en:
    echo   app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo [3/3] Ejecutando pruebas unitarias...
    call gradlew test --continue
    if %errorlevel% equ 0 (
        echo ✅ PRUEBAS UNITARIAS COMPLETADAS!
    ) else (
        echo ⚠️  Algunas pruebas fallaron, pero la compilacion es exitosa
    )
) else (
    echo.
    echo ❌ ERROR EN COMPILACION
    echo Ver detalles arriba para errores especificos.
)

echo.
pause
