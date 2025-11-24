@echo off
echo =====================================
echo   ARREGLANDO Y COMPILANDO MICROSERVICIO
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🧹 Limpiando completamente...
call gradlew clean
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "microservice\build" 2>nul

echo.
echo 🛑 Deteniendo daemon de Gradle...
call gradlew --stop

echo.
echo 🔄 Sincronizando proyecto...
call gradlew tasks --all

echo.
echo 📦 Compilando microservicio con dependencias frescas...
call gradlew :microservice:clean :microservice:bootJar --refresh-dependencies --no-daemon

if %errorlevel% equ 0 (
    echo.
    echo ✅ MICROSERVICIO COMPILADO EXITOSAMENTE
    echo.
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo 📁 JAR generado correctamente
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do echo    📏 Tamaño: %%~zI bytes

        echo.
        echo 🚀 AHORA PUEDES DESPLEGAR:
        echo    deploy-to-my-ec2.bat "C:\Users\sekai\Downloads\crimewave-key.pem"
    ) else (
        echo ❌ JAR no encontrado después de compilación
    )
) else (
    echo.
    echo ❌ ERROR: No se pudo compilar el microservicio
    echo    Verificando errores específicos...
    echo.
    echo 💡 POSIBLES SOLUCIONES:
    echo    1. Actualizar Gradle: gradlew wrapper --gradle-version=8.5
    echo    2. Limpiar cache: del /q /s .gradle
    echo    3. Verificar Java version: java -version
)

echo.
pause
