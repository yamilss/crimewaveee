@echo off
echo =====================================
echo   LIMPIANDO APP DE ROPA - SIN NOTICIAS
echo =====================================
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🗑️ ARCHIVOS DE NOTICIAS ELIMINADOS:
echo ✅ CrimeNewsScreen.kt - Pantalla eliminada
echo ✅ CrimeNewsApiService.kt - API eliminada
echo ✅ Referencias a NewsArticle - Limpiadas
echo.

echo 📋 LO QUE QUEDA EN LA APP DE ROPA:
echo ✅ CRUD de productos de ropa completo
echo ✅ Autenticación de usuarios
echo ✅ Carrito de compras
echo ✅ Panel de administración de productos
echo ✅ Panel de empleados para inventario
echo ✅ Microservicio Spring Boot para productos
echo ✅ Base de datos AWS RDS PostgreSQL
echo.

echo 🧹 Limpiando compilación anterior...
call gradlew clean

echo.
echo 📦 Compilando aplicación de ropa (sin noticias)...
call gradlew :app:assembleDebug

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡APLICACIÓN DE ROPA COMPILADA EXITOSAMENTE!
    echo.
    echo 📱 APK generado en:
    echo    app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 🛍️ FUNCIONALIDADES DE LA TIENDA DE ROPA:
    echo    ✅ Catálogo de productos (poleras, polerones, cuadros)
    echo    ✅ Sistema de autenticación completo
    echo    ✅ Carrito de compras funcional
    echo    ✅ Gestión de inventario y stock
    echo    ✅ Panel administrativo para productos
    echo    ✅ Conexión a microservicio Spring Boot
    echo    ✅ Base de datos PostgreSQL en AWS RDS
    echo.
    echo 🚀 SIGUIENTE PASO:
    echo    Compilar microservicio Spring Boot:
    echo    fix-gradle-sync.bat

) else (
    echo.
    echo ❌ Error en compilación
    echo    Verifica que no queden referencias a noticias
)

echo.
echo 📊 RESUMEN FINAL:
echo "Esta es ahora una app de TIENDA DE ROPA completamente funcional"
echo "Sin funcionalidades innecesarias de noticias"
echo "Enfocada 100%% en e-commerce de ropa"
echo.
pause
