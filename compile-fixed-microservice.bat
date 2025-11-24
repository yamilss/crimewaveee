@echo off
echo =====================================
echo   MICROSERVICIO CORREGIDO - JAVAX
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo ✅ CORRECCIONES APLICADAS:
echo    - Imports javax (no Jakarta)
echo    - Spring Boot 2.x compatible Java 11
echo    - Dependencias actualizadas
echo.

echo ☕ Tu Java version:
java -version

echo.
echo 🧹 Limpiando cache completamente...
call gradlew clean --no-daemon
call gradlew --stop

echo.
echo 📦 Compilando microservicio corregido...
call gradlew :microservice:bootJar --refresh-dependencies --no-daemon

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡MICROSERVICIO COMPILADO EXITOSAMENTE!
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo ✅ JAR generado: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do (
            echo    📏 Tamaño: %%~zI bytes
            echo    📅 Creado: %%~tI
        )
        echo.
        echo 🚀 AHORA DESPLEGAR A EC2:
        echo    deploy-to-my-ec2.bat "C:\Users\sekai\Downloads\crimewave-key.pem"
        echo.
        echo 🌐 Después verifica en:
        echo    http://3.15.178.116:8080/api/products

    ) else (
        echo ❌ JAR no encontrado después de compilación exitosa
        echo    Verificando directorio build...
        if exist "microservice\build\libs" (
            dir "microservice\build\libs"
        )
    )

) else (
    echo.
    echo ❌ ERROR EN COMPILACIÓN
    echo.
    echo 🔧 POSIBLES CAUSAS:
    echo    1. Java version incompatible (necesita Java 11+)
    echo    2. Conflictos de cache de Gradle
    echo    3. Imports aún incorrectos
    echo.
    echo 💡 PRUEBA MANUAL:
    echo    1. gradlew --version
    echo    2. gradlew :microservice:dependencies
    echo    3. Verificar errores específicos
)

echo.
pause
