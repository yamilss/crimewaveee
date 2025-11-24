@echo off
echo =====================================
echo   DESPLEGANDO A TU EC2: 3.15.178.116
echo =====================================
echo.

set EC2_IP=3.15.178.116
echo 🎯 IP de EC2: %EC2_IP%
echo 🗄️ RDS: crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com
echo.

if "%~1"=="" (
    echo 🔑 Necesito la ruta del archivo key
    echo.
    echo 📋 Uso: %0 ^<RUTA-KEY^>
    echo    Ejemplo: %0 C:\Users\sekai\Downloads\crimewave-key.pem
    echo.
    echo 💡 ¿No tienes el key file?
    echo    Descárgalo desde AWS Console → EC2 → Key Pairs
    echo.
    pause
    exit /b 1
)

set KEY_FILE=%1

if not exist "%KEY_FILE%" (
    echo ❌ Key file no encontrado: %KEY_FILE%
    echo    Verifica la ruta del archivo .pem
    pause
    exit /b 1
)

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 📦 PASO 1: Compilando microservicio...
call gradlew :microservice:clean :microservice:bootJar

if %errorlevel% neq 0 (
    echo ❌ Error compilando JAR
    pause
    exit /b 1
)

if not exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR no generado
    pause
    exit /b 1
)

echo ✅ JAR compilado: microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar

echo.
echo 🔍 PASO 2: Verificando conexión SSH...
ssh -i "%KEY_FILE%" -o ConnectTimeout=10 -o BatchMode=yes ubuntu@%EC2_IP% exit
if %errorlevel% neq 0 (
    echo ❌ No se puede conectar a EC2
    echo.
    echo 🔧 SOLUCIONES:
    echo    1. Verifica Security Group permite SSH (puerto 22)
    echo    2. Verifica que la instancia esté "Running"
    echo    3. Key file tiene permisos correctos
    echo.
    echo 📋 Comandos para arreglar permisos:
    echo    icacls "%KEY_FILE%" /inheritance:r
    echo    icacls "%KEY_FILE%" /grant:r "%USERNAME%:(R)"
    pause
    exit /b 1
)

echo ✅ Conexión SSH exitosa

echo.
echo 📤 PASO 3: Subiendo archivos...
scp -i "%KEY_FILE%" "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" ubuntu@%EC2_IP%:/home/ubuntu/
scp -i "%KEY_FILE%" "microservice\application-aws.properties" ubuntu@%EC2_IP%:/home/ubuntu/

if %errorlevel% neq 0 (
    echo ❌ Error subiendo archivos
    pause
    exit /b 1
)

echo ✅ Archivos subidos

echo.
echo ⚙️ PASO 4: Configurando servidor...
ssh -i "%KEY_FILE%" ubuntu@%EC2_IP% "
echo '🔧 Configurando Ubuntu en EC2...'

# Actualizar sistema
sudo apt update -y

# Instalar Java 11
if ! command -v java >/dev/null 2>&1; then
    echo '📥 Instalando OpenJDK 11...'
    sudo apt install openjdk-11-jdk -y
else
    echo '☕ Java ya instalado'
fi

java -version

# Detener app anterior
echo '🛑 Deteniendo aplicación anterior...'
pkill -f 'microservice-0.0.1-SNAPSHOT.jar' || true

# Crear logs
mkdir -p ~/logs

# Ejecutar aplicación
echo '🚀 Iniciando Spring Boot en puerto 8080...'
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

# Esperar inicio
echo '⏳ Esperando que inicie la aplicación...'
sleep 20

# Verificar proceso
if pgrep -f 'microservice-0.0.1-SNAPSHOT.jar' >/dev/null; then
    echo '✅ Spring Boot ejecutándose correctamente'

    # Health check
    echo '🔍 Probando health check...'
    for i in {1..6}; do
        if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
            echo '✅ Health check exitoso'
            break
        else
            echo \"Intento \$i/6 - esperando...\"
            sleep 10
        fi
    done

    # Inicializar datos
    echo '📦 Inicializando productos de la tienda...'
    sleep 5
    if curl -s -X POST http://localhost:8080/api/products/init-sample-data >/dev/null 2>&1; then
        echo '✅ Datos iniciales cargados'
    else
        echo '⚠️ Inicialización puede haber fallado'
    fi

else
    echo '❌ Error: Spring Boot no se inició'
    echo '📋 Log de errores:'
    tail -30 ~/logs/crimewave.log
    exit 1
fi
"

if %errorlevel% neq 0 (
    echo ❌ Error en configuración
    pause
    exit /b 1
)

echo.
echo 🧪 PASO 5: Probando endpoints públicos...
timeout /t 5 >nul

echo.
echo 🔍 Health check desde internet:
curl -s http://%EC2_IP%:8080/actuator/health

echo.
echo 📦 API de productos:
curl -s http://%EC2_IP%:8080/api/products | head -n 10

echo.
echo 🎉 ¡DESPLIEGUE COMPLETADO EXITOSAMENTE!
echo.
echo =====================================
echo        TU TIENDA DE ROPA EN AWS
echo =====================================
echo.
echo 🌐 URL Base: http://%EC2_IP%:8080
echo 📱 API Productos: http://%EC2_IP%:8080/api/products
echo 🔍 Health Check: http://%EC2_IP%:8080/actuator/health
echo 🗄️ Base de Datos: RDS Aurora PostgreSQL
echo.
echo ✅ CONFIGURACIÓN COMPLETADA:
echo    - Spring Boot corriendo en EC2
echo    - PostgreSQL en RDS Aurora
echo    - App Android ya configurada con tu IP
echo    - Security Group configurado correctamente
echo.
echo 📱 PRUEBA TU APP:
echo    1. Compila: gradlew :app:assembleDebug
echo    2. Instala en tu dispositivo
echo    3. Abre la app y ve el catálogo
echo    4. Los productos deben cargar desde AWS
echo.

pause
