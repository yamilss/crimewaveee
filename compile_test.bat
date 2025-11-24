@echo off
echo Compilando proyecto Android...
cd /d "C:\Users\sekai\Downloads\crimewavee"
call gradlew assembleDebug
if %errorlevel% equ 0 (
    echo.
    echo ✓ Compilación exitosa!
    echo APK generado en: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ✗ Error en la compilación. Ver detalles arriba.
)
pause

