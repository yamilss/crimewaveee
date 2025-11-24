@echo off
echo =====================================
echo   COMPILANDO APP CON IP DE AWS
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 📱 IP configurada en ServerConfig.kt: 3.15.178.116:8080
echo 🗄️ RDS configurado: crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com
echo.

echo 🧹 Limpiando compilación anterior...
call gradlew clean

echo.
echo 📦 Compilando app Android con configuración AWS...
call gradlew :app:assembleDebug

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡APP ANDROID COMPILADA CON AWS!
    echo.
    echo 📱 APK generado: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo ✅ CONFIGURACIÓN AWS:
    echo    📡 Servidor: http://3.15.178.116:8080
    echo    🗄️ Base de datos: RDS Aurora PostgreSQL
    echo    🔄 Autodetección: Primero AWS, luego local
    echo.
    echo 🚀 PRÓXIMOS PASOS:
    echo    1. Desplegar microservicio: deploy-to-my-ec2.bat key.pem
    echo    2. Instalar APK en dispositivo Android
    echo    3. Probar que carga productos desde AWS
    echo.
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo 📁 APK listo para instalar
        for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do echo    📏 Tamaño: %%~zI bytes
    )

) else (
    echo.
    echo ❌ Error compilando app Android
    echo    Revisa los errores mostrados arriba
)

echo.
pause
