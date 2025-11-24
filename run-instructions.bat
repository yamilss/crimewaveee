@echo off
echo =====================================
echo   COMANDOS CORRECTOS PARA EJECUTAR
echo =====================================
echo.

echo ❌ ERROR: Estás en la carpeta equivocada
echo    Actualmente en: C:\Users\sekai
echo    Necesitas estar en: C:\Users\sekai\Downloads\crimewavee
echo.

echo ✅ SOLUCIÓN - Ejecuta estos comandos:
echo.
echo    cd "C:\Users\sekai\Downloads\crimewavee"
echo    .\check-ec2-status.bat
echo.
echo    O directamente:
echo    .\finish-deployment.bat
echo.

echo 📋 COMANDOS DISPONIBLES:
echo    .\check-ec2-status.bat     - Ver estado actual
echo    .\finish-deployment.bat    - Completar despliegue
echo    .\compile-app-with-aws.bat - Compilar app Android
echo.

echo 🎯 O ejecuta manualmente:
echo    ssh -i "C:\Users\sekai\Downloads\crimewave-key.pem" ubuntu@3.15.178.116
echo.

pause
