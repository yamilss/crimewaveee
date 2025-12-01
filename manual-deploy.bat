@echo off
REM =============================================
REM      DESPLIEGUE MANUAL - CRIMEWAVE
REM =============================================

echo.
echo ╔══════════════════════════════════════════════════════════╗
echo ║              DESPLIEGUE MANUAL - PASO A PASO             ║
echo ╚══════════════════════════════════════════════════════════╝
echo.

echo ✅ JAR construido exitosamente (44MB)
echo 📂 Ubicación: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
echo.

echo 🔑 NECESITAS TU ARCHIVO .PEM DE AWS
echo    Este es el archivo de clave privada que descargaste al crear la instancia EC2
echo    Ejemplo: mi-clave.pem, crimewave-key.pem, etc.
echo.

echo 📤 PASO 1 - SUBIR ARCHIVOS AL SERVIDOR:
echo.
echo    Opción A - Con WinSCP (Interfaz gráfica):
echo    1. Descargar WinSCP: https://winscp.net/
echo    2. Conectar: ubuntu@3.21.53.102 usando tu archivo .pem
echo    3. Subir: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
echo    4. Subir: microservice\application-aws.properties
echo.

echo    Opción B - Con comando SCP (desde PowerShell):
echo    1. scp -i "C:\ruta\a\tu-clave.pem" "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" ubuntu@3.21.53.102:~/
echo    2. scp -i "C:\ruta\a\tu-clave.pem" "microservice\application-aws.properties" ubuntu@3.21.53.102:~/
echo.

echo 🔗 PASO 2 - CONECTAR POR SSH:
echo    ssh -i "C:\ruta\a\tu-clave.pem" ubuntu@3.21.53.102
echo.

echo 🚀 PASO 3 - EJECUTAR EN EL SERVIDOR (una vez conectado por SSH):
echo.
echo    # Matar procesos anteriores
echo    pkill -f microservice-0.0.1-SNAPSHOT.jar
echo.
echo    # Crear directorio de logs
echo    mkdir -p ~/logs
echo.
echo    # Iniciar la aplicación
echo    nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties ^> ~/logs/crimewave.log 2^>^&1 ^&
echo.
echo    # Verificar que se inició
echo    sleep 5
echo    ps aux ^| grep java
echo    tail -f ~/logs/crimewave.log
echo.

echo 🧪 PASO 4 - PROBAR EN POSTMAN:
echo    1. POST http://3.21.53.102:8080/api/products/init-sample-data
echo    2. GET  http://3.21.53.102:8080/api/products
echo.

echo 💡 ALTERNATIVA - USA AWS CONSOLE:
echo    1. Ve a la consola de AWS EC2
echo    2. Selecciona tu instancia
echo    3. Usa "EC2 Instance Connect" para conectarte desde el navegador
echo    4. Ejecuta los comandos del PASO 3
echo.

echo 🔍 DIAGNÓSTICO (si no funciona):
echo    - Ver logs: tail -f ~/logs/crimewave.log
echo    - Ver procesos: ps aux ^| grep java
echo    - Verificar puerto: netstat -tlnp ^| grep :8080
echo    - Reiniciar servicio: pkill java ^&^& nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties ^> ~/logs/crimewave.log 2^>^&1 ^&
echo.

pause
