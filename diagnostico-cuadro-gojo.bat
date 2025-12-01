@echo off
REM Script para diagnosticar por qué el Cuadro Gojo sigue apareciendo

echo ╔══════════════════════════════════════════════════════════╗
echo ║           DIAGNÓSTICO - CUADRO GOJO PERSISTENTE         ║
echo ╚══════════════════════════════════════════════════════════╝
echo.

echo 🔍 VERIFICANDO ESTADO DEL SERVIDOR...
echo.

echo 1. Productos actuales en el servidor:
curl -s http://3.21.53.102:8080/api/products

echo.
echo.

echo 2. Buscando específicamente productos con "Gojo":
curl -s http://3.21.53.102:8080/api/products | findstr /i "gojo"

echo.
echo.

echo 3. Verificando productos destacados:
curl -s http://3.21.53.102:8080/api/products/featured

echo.
echo.

echo 🔧 POSIBLES CAUSAS DEL PROBLEMA:
echo.
echo ❓ CAUSA 1: Cache local no sincronizado
echo    - La app tiene datos en SharedPreferences que no se han actualizado
echo    - Solución: Limpiar cache y forzar sincronización
echo.
echo ❓ CAUSA 2: Producto aún existe en el servidor
echo    - El producto se eliminó localmente pero no del servidor
echo    - Solución: Eliminar del servidor via API
echo.
echo ❓ CAUSA 3: Problema de sincronización
echo    - La eliminación no se propagó correctamente
echo    - Solución: Sincronización manual
echo.
echo ❓ CAUSA 4: ID duplicado o conflicto
echo    - Existen múltiples productos con el mismo nombre/ID
echo    - Solución: Verificar IDs únicos
echo.

echo 💡 COMANDOS PARA SOLUCIONAR:
echo.
echo A) Eliminar producto específico del servidor:
echo    curl -X DELETE http://3.21.53.102:8080/api/products/4
echo.
echo B) Eliminar por nombre (si es necesario):
echo    - Buscar ID correcto y eliminar
echo.
echo C) Forzar sincronización completa:
echo    - Usar el botón de sincronización en la app
echo    - O reiniciar la app para que descargue datos frescos
echo.

pause
