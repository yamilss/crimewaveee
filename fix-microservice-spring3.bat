@echo off
echo =====================================
echo   SOLUCIONANDO MICROSERVICIO SPRING BOOT
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🔍 Verificando Java version...
java -version
echo.

echo 💡 NOTA: Spring Boot 3 requiere Java 17 o superior
echo    Si tienes Java 11, el microservicio fallará
echo.

echo 🧹 Limpieza completa del proyecto...
call gradlew clean
call gradlew --stop

echo.
echo 📦 Intentando compilar con configuración actualizada...
call gradlew :microservice:bootJar --refresh-dependencies

if %errorlevel% equ 0 (
    echo.
    echo ✅ ¡MICROSERVICIO COMPILADO EXITOSAMENTE!
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo 📁 JAR generado: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do echo    📏 Tamaño: %%~zI bytes
        echo.
        echo 🚀 LISTO PARA DESPLEGAR:
        echo    deploy-to-my-ec2.bat "C:\Users\sekai\Downloads\crimewave-key.pem"
    ) else (
        echo ❌ JAR no encontrado
    )
) else (
    echo.
    echo ❌ COMPILACIÓN FALLÓ
    echo.
    echo 🔧 SOLUCIÓN - Usar Java 17:
    echo.
    echo    1. Descargar Java 17 desde:
    echo       https://adoptium.net/temurin/releases/
    echo.
    echo    2. O usar Spring Boot 2.x compatible con Java 11:
    echo       Ejecuta: fix-microservice-java11.bat
    echo.
    echo 💡 ALTERNATIVA RÁPIDA:
    echo    Voy a crear una versión compatible con Java 11...

    pause
    echo.
    echo 🔄 Creando versión compatible con Java 11...
    call fix-microservice-java11.bat
)

echo.
pause
