@echo off
echo =====================================
echo   SOLUCIÓN RÁPIDA - COMPILACIÓN
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🛑 Parando daemon de Gradle...
call gradlew --stop

echo.
echo 🧹 Limpiando...
call gradlew clean

echo.
echo 📦 Compilando microservicio (modo verbose)...
call gradlew :microservice:bootJar --info

echo.
if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ✅ ¡JAR CREADO EXITOSAMENTE!
    echo 📁 Ubicación: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
) else (
    echo ❌ JAR no encontrado
    echo 🔍 Contenido de microservice\build\libs\:
    if exist "microservice\build\libs" (
        dir "microservice\build\libs"
    ) else (
        echo    Directorio no existe
    )
)

pause
