@echo off
echo =====================================
echo   ARREGLANDO CONEXION SSH A EC2
echo =====================================
echo.

set EC2_IP=3.15.178.116
set KEY_FILE=C:\Users\sekai\Downloads\crimewave-key.pem

echo 🔍 Verificando archivo key...
if not exist "%KEY_FILE%" (
    echo ❌ Key file no encontrado: %KEY_FILE%
    echo    Verifica que esté en la carpeta Downloads
    pause
    exit /b 1
)

echo ✅ Key file encontrado: %KEY_FILE%

echo.
echo 🔧 Arreglando permisos del archivo .pem...
icacls "%KEY_FILE%" /inheritance:r
icacls "%KEY_FILE%" /grant:r "%USERNAME%:(R)"

echo.
echo 🧹 Limpiando known_hosts para evitar conflictos...
if exist "%USERPROFILE%\.ssh\known_hosts" (
    ssh-keygen -R %EC2_IP% 2>nul
    echo    Host %EC2_IP% removido de known_hosts
)

echo.
echo 🔍 Probando conexión SSH (aceptará automáticamente el host key)...
ssh -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ConnectTimeout=15 ubuntu@%EC2_IP% "echo 'Conexión SSH exitosa'; uptime"

if %errorlevel% equ 0 (
    echo.
    echo ✅ ¡CONEXIÓN SSH FUNCIONANDO!
    echo.
    echo 🚀 Ahora ejecuta el despliegue:
    echo    deploy-fixed-to-ec2.bat
    echo.
) else (
    echo.
    echo ❌ Aún hay problemas con SSH
    echo.
    echo 🔧 VERIFICACIONES ADICIONALES:
    echo.
    echo 1. ¿Tu instancia EC2 está "Running"?
    echo    Ve a AWS Console → EC2 → Instances
    echo.
    echo 2. ¿Security Group permite SSH desde tu IP?
    echo    Debe tener: SSH (22) desde tu IP pública
    echo.
    echo 3. ¿Usas VPN o proxy?
    echo    Puede bloquear la conexión SSH
    echo.
    echo 4. ¿El key pair es correcto?
    echo    Debe ser el mismo usado al crear la instancia
    echo.
    echo 💡 PRUEBA MANUAL:
    echo    ssh -i "%KEY_FILE%" ubuntu@%EC2_IP%
)

pause
