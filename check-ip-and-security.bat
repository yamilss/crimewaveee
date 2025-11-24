@echo off
echo =====================================
echo   VERIFICADOR DE IP Y SECURITY GROUP
echo =====================================
echo.

echo 🔍 Tu IP pública actual:
curl -s https://ipinfo.io/ip
echo.

echo.
echo 📋 CONFIGURACIÓN ACTUAL EN AWS:
echo    Security Group SSH permite: 201.189.214.126/32
echo    Tu EC2 IP: 3.15.178.116
echo    Estado requerido: EC2 debe estar "Running"
echo.

echo 💡 SI TU IP CAMBIÓ:
echo    1. Ve a AWS Console → EC2 → Security Groups
echo    2. Busca tu Security Group
echo    3. Edita regla SSH (puerto 22)
echo    4. Cambia Source a "My IP" o tu nueva IP
echo.

echo 🔧 O usa temporalmente esta IP amplia (MENOS SEGURO):
echo    Source: 0.0.0.0/0 para SSH
echo    (Recuerda cambiarlo después por seguridad)
echo.

echo ⚠️ VERIFICACIONES IMPORTANTES:
echo    ✓ ¿Tu instancia EC2 está "Running"?
echo    ✓ ¿El key pair es el correcto (crimewave-key.pem)?
echo    ✓ ¿Tu IP coincide con la del Security Group?
echo    ✓ ¿No usas VPN que cambie tu IP?
echo.

echo 🚀 DESPUÉS DE ARREGLAR EL SECURITY GROUP:
echo    fix-ssh-connection.bat
echo    deploy-fixed-to-ec2.bat
echo.

pause
