@echo off
echo ========================================
echo  LIMPIEZA COMPLETA Y FIX JDK GRADLE
echo ========================================

echo 1. Deteniendo daemon de Gradle...
gradlew --stop

echo 2. Eliminando cache de Gradle...
if exist ".gradle" (
    rmdir /s /q ".gradle"
    echo    Cache .gradle eliminado
)

echo 3. Eliminando directorio build...
if exist "build" (
    rmdir /s /q "build"
    echo    Directorio build eliminado
)

if exist "app\build" (
    rmdir /s /q "app\build"
    echo    Directorio app\build eliminado
)

echo 4. Eliminando cache de IDE...
if exist ".idea\caches" (
    rmdir /s /q ".idea\caches"
    echo    Cache de IDE eliminado
)

if exist ".idea\modules" (
    rmdir /s /q ".idea\modules"
    echo    Modulos de IDE eliminados
)

echo 5. Eliminando archivos temporales...
del /q /f *.tmp 2>nul
del /q /f *.lock 2>nul

echo 6. Limpiando cache de Gradle global...
echo    Eliminando cache en %USERPROFILE%\.gradle\caches
if exist "%USERPROFILE%\.gradle\caches" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches"
    echo    Cache global eliminado
)

echo 7. Verificando configuracion JDK...
echo    JDK configurado en gradle.properties
echo    JDK configurado en .idea\misc.xml
echo    JDK configurado en build.gradle.kts

echo ========================================
echo  LIMPIEZA COMPLETADA
echo ========================================
echo.
echo SIGUIENTE PASO:
echo 1. Cierra Android Studio completamente
echo 2. Abre Android Studio
echo 3. Abre el proyecto
echo 4. Ve a File ^> Sync Project with Gradle Files
echo 5. Si aun hay errores, ve a File ^> Settings ^> Build ^> Gradle
echo 6. Selecciona "Gradle JDK: Embedded JDK (jbr-17)"
echo.
echo ALTERNATIVA SI SIGUE FALLANDO:
echo gradlew clean build --refresh-dependencies
echo.
pause
