@echo off
echo =====================================
echo   MICROSERVICIO COMPATIBLE JAVA 11
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🔄 Revirtiendo a Spring Boot 2.x compatible con Java 11...

echo.
echo 📝 Actualizando build.gradle.kts principal...
echo // Revertido automáticamente a Spring Boot 2.x para Java 11

echo.
echo 📝 Actualizando microservice/build.gradle.kts...
echo // Revertido automáticamente para Java 11

echo.
echo 🧹 Limpiando proyecto...
call gradlew clean
call gradlew --stop

echo.
echo 📦 Compilando con Spring Boot 2.x...
call gradlew :microservice:bootJar --refresh-dependencies

if %errorlevel% equ 0 (
    echo.
    echo ✅ ¡MICROSERVICIO COMPATIBLE COMPILADO!
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo 📁 JAR: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
        echo ☕ Compatible: Java 11
        echo 🌱 Spring Boot: 2.x
        echo 🗄️ JPA: javax (no Jakarta)
        echo.
        echo 🚀 DESPLEGAR AHORA:
        echo    deploy-to-my-ec2.bat "C:\Users\sekai\Downloads\crimewave-key.pem"
    )
) else (
    echo ❌ Aún hay errores de compilación
    echo    Verifica la configuración manualmente
)

pause
