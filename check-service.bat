@echo off
REM Script para verificar y reiniciar el microservicio en AWS EC2
REM Uso: check-service.bat

echo 🔍 Verificando estado del servicio en 3.21.53.102...
echo.

REM Usar curl para probar la conexión directamente
echo 🧪 Probando conexión a la API...
curl -s --connect-timeout 10 http://3.21.53.102:8080/api/products
if %errorlevel% equ 0 (
    echo ✅ Servicio está funcionando correctamente
    echo.
    echo 📋 Endpoints disponibles:
    echo    GET  http://3.21.53.102:8080/api/products
    echo    POST http://3.21.53.102:8080/api/products/init-sample-data
    echo    GET  http://3.21.53.102:8080/api/products/featured
    echo    GET  http://3.21.53.102:8080/api/products/category/POLERAS
) else (
    echo ❌ No se puede conectar al servicio
    echo.
    echo 🔧 Posibles soluciones:
    echo 1. El servicio no está ejecutándose en el servidor
    echo 2. Firewall o Security Groups bloqueando el puerto 8080
    echo 3. El servidor EC2 está apagado
    echo.
    echo 💡 Para reiniciar manualmente:
    echo    - Conéctate por SSH al servidor EC2
    echo    - Ejecuta: java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties
)

echo.
echo 📱 Para usar en Postman:
echo    URL Base: http://3.21.53.102:8080/api/products
echo    Método: GET para obtener todos los productos
echo    Método: POST /init-sample-data para cargar datos de prueba

pause
