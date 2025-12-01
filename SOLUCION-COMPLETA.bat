@echo off
REM =============================================
REM    SOLUCIÓN COMPLETA - CRIMEWAVE DEPLOY
REM =============================================

cls
echo.
echo ╔══════════════════════════════════════════════════════════╗
echo ║                 CRIMEWAVE DEPLOY FIX                     ║
echo ╚══════════════════════════════════════════════════════════╝
echo.

REM Cambiar a la carpeta correcta
cd /d "C:\Users\sekai\OneDrive\Documents\GitHub\crimewaveee"

echo 🔍 PASO 1: Verificación rápida...
if not exist "gradlew.bat" (
    echo ❌ ERROR: No estamos en la carpeta correcta
    echo Carpeta actual: %CD%
    echo Se esperaba: C:\Users\sekai\OneDrive\Documents\GitHub\crimewaveee
    pause
    exit /b 1
)

echo ✅ Carpeta correcta: %CD%
echo.

echo 🧹 PASO 2: Limpiando build anterior...
call gradlew.bat clean >nul 2>&1
echo ✅ Build anterior limpiado
echo.

echo 📦 PASO 3: Construyendo JAR del microservicio...
echo    Comando: gradlew.bat :microservice:bootJar
echo    Esto puede tomar unos minutos...
echo.

call gradlew.bat :microservice:bootJar

if %errorlevel% neq 0 (
    echo.
    echo ❌ ERROR en la construcción del JAR
    echo.
    echo 🔧 DIAGNÓSTICO:
    echo    1. Verifica que tengas Java 11+ instalado: java -version
    echo    2. Verifica conexión a internet para descargar dependencias
    echo    3. Intenta ejecutar: gradlew.bat :microservice:clean :microservice:bootJar
    echo.
    echo 💡 COMANDOS DE DIAGNÓSTICO:
    echo    - java -version
    echo    - gradlew.bat --version
    echo    - gradlew.bat :microservice:dependencies
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ PASO 4: Verificando archivos generados...

if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ✅ JAR creado: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar

    REM Obtener tamaño del archivo
    for %%A in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do echo    Tamaño: %%~zA bytes
) else (
    echo ❌ JAR NO fue creado
    pause
    exit /b 1
)

if exist "microservice\application-aws.properties" (
    echo ✅ Configuración: microservice\application-aws.properties
) else (
    echo ❌ Archivo de configuración NO encontrado
    pause
    exit /b 1
)

echo.
echo ╔══════════════════════════════════════════════════════════╗
echo ║                    🎉 BUILD EXITOSO 🎉                  ║
echo ╚══════════════════════════════════════════════════════════╝
echo.

echo 📋 ARCHIVOS LISTOS:
echo    📦 JAR: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
echo    ⚙️  Config: microservice\application-aws.properties
echo    🌐 Target: AWS EC2 (3.21.53.102:8080)
echo.

echo 🚀 PRÓXIMOS PASOS PARA DESPLIEGUE:
echo.
echo OPCIÓN A - Despliegue automático (recomendado):
echo    1. Abre Git Bash en esta carpeta
echo    2. Ejecuta: bash deploy-to-aws.sh 3.21.53.102 /ruta/a/tu-clave.pem
echo.
echo OPCIÓN B - Despliegue manual:
echo    1. Sube archivos: scp -i "clave.pem" microservice\build\libs\*.jar ubuntu@3.21.53.102:~/
echo    2. Sube config: scp -i "clave.pem" microservice\application-aws.properties ubuntu@3.21.53.102:~/
echo    3. SSH: ssh -i "clave.pem" ubuntu@3.21.53.102
echo    4. Ejecuta: nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties ^> ~/logs/app.log 2^>^&1 ^&
echo.

echo 🧪 DESPUÉS DEL DESPLIEGUE - PROBAR EN POSTMAN:
echo    1. POST http://3.21.53.102:8080/api/products/init-sample-data
echo    2. GET  http://3.21.53.102:8080/api/products
echo    3. GET  http://3.21.53.102:8080/api/products/featured
echo.

echo 🔧 SI EL SERVICIO NO RESPONDE:
echo    - SSH al servidor: ssh -i "clave.pem" ubuntu@3.21.53.102
echo    - Ver logs: tail -f ~/logs/app.log
echo    - Ver procesos: ps aux ^| grep java
echo    - Reiniciar: pkill java ^&^& java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties
echo.

pause
