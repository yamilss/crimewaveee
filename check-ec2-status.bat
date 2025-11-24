@echo off
echo =====================================
echo   VERIFICANDO ESTADO ACTUAL EN EC2
echo =====================================
echo.

set EC2_IP=3.15.178.116
set KEY_FILE=C:\Users\sekai\Downloads\crimewave-key.pem

echo 🔍 Conectando a EC2 para verificar estado...
echo.

ssh -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@%EC2_IP% "
echo '=== ESTADO ACTUAL DEL SERVIDOR ==='
echo

echo 'Archivos subidos:'
ls -la ~/ | grep -E '(jar|properties)'

echo
echo 'Java instalado:'
java -version 2>&1 | head -3

echo
echo 'Procesos Spring Boot ejecutándose:'
ps aux | grep java || echo 'No hay procesos Java'

echo
echo 'Logs recientes (últimas 20 líneas):'
if [ -f ~/logs/crimewave.log ]; then
    tail -20 ~/logs/crimewave.log
else
    echo 'Log no existe aún'
fi

echo
echo 'Probando puerto 8080:'
curl -s http://localhost:8080/actuator/health 2>/dev/null || echo 'Puerto 8080 no responde'

echo
echo 'Memoria y CPU:'
free -h
uptime
"

echo.
echo =====================================
echo.
echo 💡 OPCIONES DISPONIBLES:
echo    1. .\finish-deployment.bat  (Completar configuración)
echo    2. Conectar manualmente: ssh -i "%KEY_FILE%" ubuntu@%EC2_IP%
echo    3. Probar en navegador: http://%EC2_IP%:8080/api/products
echo.

pause
