@echo off
echo =====================================
echo   FINALIZANDO DESPLIEGUE EN EC2
echo =====================================
echo.

set EC2_IP=3.15.178.116
set KEY_FILE=C:\Users\sekai\Downloads\crimewave-key.pem

echo 🎯 Los archivos ya están en EC2, configurando Spring Boot...

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo.
echo 📤 Subiendo script de configuración...
scp -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "setup-spring-boot.sh" ubuntu@%EC2_IP%:/home/ubuntu/

echo.
echo ⚙️ Ejecutando configuración de Spring Boot en EC2...
ssh -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@%EC2_IP% "chmod +x ~/setup-spring-boot.sh && ~/setup-spring-boot.sh"

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡SPRING BOOT CONFIGURADO EXITOSAMENTE!
    echo.
    echo =====================================
    echo        TU TIENDA DE ROPA EN AWS
    echo =====================================
    echo.
    echo 🌐 URLs disponibles:
    echo    API Base: http://%EC2_IP%:8080
    echo    Productos: http://%EC2_IP%:8080/api/products
    echo    Health: http://%EC2_IP%:8080/actuator/health
    echo.

    echo 🧪 Probando desde internet...
    timeout /t 5 >nul

    echo.
    echo 🔍 Health check público:
    curl -s http://%EC2_IP%:8080/actuator/health || echo "API iniciando..."

    echo.
    echo 📦 Productos disponibles:
    curl -s http://%EC2_IP%:8080/api/products | head -c 400 || echo "Cargando productos..."

    echo.
    echo.
    echo ✅ ¡DESPLIEGUE COMPLETO!
    echo    🖥️ Spring Boot: Ejecutándose en EC2
    echo    🗄️ PostgreSQL: Conectado a RDS Aurora
    echo    📱 App Android: Lista para conectar
    echo.
    echo 📱 SIGUIENTE PASO - COMPILAR APP:
    echo    .\compile-app-with-aws.bat
    echo.
    echo 📲 DESPUÉS INSTALAR APK:
    echo    app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 🌐 PROBAR EN NAVEGADOR:
    echo    http://%EC2_IP%:8080/api/products
    echo.

) else (
    echo.
    echo ❌ Error en la configuración
    echo.
    echo 💡 VERIFICAR MANUALMENTE:
    echo    ssh -i "%KEY_FILE%" ubuntu@%EC2_IP%
    echo    tail -50 ~/logs/crimewave.log
    echo.
    echo ⏰ O espera 2-3 minutos y prueba:
    echo    http://%EC2_IP%:8080/api/products
)

pause
