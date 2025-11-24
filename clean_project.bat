@echo off
echo Limpiando proyecto Android...

echo Eliminando directorio build...
if exist "app\build" (
    rmdir /s /q "app\build"
    echo Directorio app\build eliminado
)

if exist "build" (
    rmdir /s /q "build"
    echo Directorio build eliminado
)

echo Eliminando cache de Gradle...
if exist ".gradle" (
    rmdir /s /q ".gradle"
    echo Cache de Gradle eliminado
)

echo Eliminando archivos temporales de IDE...
if exist ".idea\caches" (
    rmdir /s /q ".idea\caches"
    echo Cache de IDE eliminado
)

echo Buscando archivos duplicados...
for /r %%i in (*ProductRepositoryNew*.kt) do (
    echo Encontrado archivo duplicado: %%i
    del "%%i"
    echo Archivo eliminado
)

echo Limpieza completada.
echo Ahora ejecuta: gradlew clean build
pause
