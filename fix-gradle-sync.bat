@echo off
echo =====================================
echo   SOLUCIONANDO ERRORES DE GRADLE
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🛑 Parando daemon de Gradle para limpiar cache...
call gradlew --stop

echo.
echo 🗑️ Eliminando cache corrupto...
if exist ".gradle" (
    echo    Eliminando .gradle...
    rmdir /s /q ".gradle" 2>nul
)

echo.
echo 🔄 Sincronizando proyecto (Gradle Sync)...
call gradlew tasks --all

if %errorlevel% neq 0 (
    echo ❌ Error en sincronización. Intentando reparar...

    echo 📝 Verificando configuración...
    echo    Revisando build.gradle.kts principal...
    if not exist "build.gradle.kts" (
        echo ❌ build.gradle.kts principal no existe
        pause
        exit /b 1
    )

    echo    Revisando microservice/build.gradle.kts...
    if not exist "microservice\build.gradle.kts" (
        echo ❌ microservice\build.gradle.kts no existe
        pause
        exit /b 1
    )

    echo    Revisando settings.gradle.kts...
    if not exist "settings.gradle.kts" (
        echo ❌ settings.gradle.kts no existe
        pause
        exit /b 1
    )

    echo.
    echo 🔧 Intentando reparación automática...
    call gradlew clean --refresh-dependencies

    if %errorlevel% neq 0 (
        echo ❌ No se pudo reparar automáticamente
        echo.
        echo 🔍 Información de debug:
        echo    Java Version:
        java -version
        echo.
        echo    Gradle Version:
        call gradlew --version
        pause
        exit /b 1
    )
)

echo.
echo ✅ Sincronización completada

echo.
echo 📦 Compilando microservicio...
call gradlew :microservice:bootJar

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡COMPILACIÓN EXITOSA!
    echo.
    echo 📁 JAR generado:
    if exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
        echo ✅ microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar
        for %%I in ("microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar") do (
            echo    Tamaño: %%~zI bytes
        )
    ) else (
        echo ❌ JAR no encontrado en ubicación esperada
    )

    echo.
    echo 🚀 SIGUIENTE PASO:
    echo    El microservicio está listo para desplegar a AWS
    echo    Usa: deploy-to-aws.bat TU-IP-EC2 ruta\al\key.pem

) else (
    echo.
    echo ❌ Error en compilación del microservicio
    echo    Revisa los mensajes de error arriba
)

echo.
pause
