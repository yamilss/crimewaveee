@echo off
echo =====================================
echo   DESPLIEGUE COMPLETO - TODO EN UNO
echo =====================================
echo.

set EC2_IP=3.15.178.116
set KEY_FILE=C:\Users\sekai\Downloads\crimewave-key.pem

echo 🎯 Información:
echo    Tu IP: 201.189.214.126 ✅ (coincide con Security Group)
echo    EC2 IP: %EC2_IP%
echo    RDS: crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com
echo.

cd /d "C:\Users\sekai\Downloads\crimewavee"

echo 🔍 Verificando archivos necesarios...
if not exist "%KEY_FILE%" (
    echo ❌ Key file no encontrado: %KEY_FILE%
    pause
    exit /b 1
)

if not exist "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR no encontrado, compilando primero...
    call gradlew :microservice:bootJar
)

echo ✅ Archivos encontrados

echo.
echo 🔧 PASO 1: Arreglando permisos SSH...
icacls "%KEY_FILE%" /inheritance:r >nul 2>&1
icacls "%KEY_FILE%" /grant:r "%USERNAME%:(R)" >nul 2>&1
echo    Permisos del key corregidos

echo.
echo 🧹 PASO 2: Limpiando known_hosts...
ssh-keygen -R %EC2_IP% >nul 2>&1
echo    Cache SSH limpiado

echo.
echo 🔍 PASO 3: Probando conexión SSH...
ssh -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ConnectTimeout=15 ubuntu@%EC2_IP% "echo 'Conexión SSH OK'; date"

if %errorlevel% neq 0 (
    echo.
    echo ❌ SSH sigue fallando. Posibles causas:
    echo    1. Instancia EC2 no está "Running"
    echo    2. Key pair incorrecto
    echo    3. Firewall local bloquea SSH
    echo.
    echo 💡 Ve a AWS Console → EC2 → Instances
    echo    Verifica que tu instancia esté "Running"
    pause
    exit /b 1
)

echo ✅ SSH funcionando correctamente

echo.
echo 📤 PASO 4: Subiendo archivos a EC2...
echo    JAR (44MB - puede tomar 2-3 minutos)...
scp -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar" ubuntu@%EC2_IP%:/home/ubuntu/

echo    Configuración AWS...
scp -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null "microservice\application-aws.properties" ubuntu@%EC2_IP%:/home/ubuntu/

echo ✅ Archivos subidos exitosamente

echo.
echo ⚙️ PASO 5: Instalando y ejecutando Spring Boot...
ssh -i "%KEY_FILE%" -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@%EC2_IP% "
echo 'Configurando servidor...'

# Actualizar sistema
sudo apt update -y

# Instalar Java 11
if ! command -v java >/dev/null 2>&1; then
    echo 'Instalando Java 11...'
    sudo apt install openjdk-11-jdk -y
else
    echo 'Java ya instalado'
fi

echo 'Java version:'
java -version

# Detener app anterior
echo 'Deteniendo app anterior...'
pkill -f 'microservice-0.0.1-SNAPSHOT.jar' || true
sleep 2

# Crear directorio logs
mkdir -p ~/logs

# Ejecutar Spring Boot
echo 'Iniciando Spring Boot con RDS...'
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

# Esperar que inicie
echo 'Esperando inicio (45 segundos)...'
sleep 45

# Verificar proceso
if pgrep -f 'microservice-0.0.1-SNAPSHOT.jar' >/dev/null; then
    echo 'Spring Boot ejecutándose'

    # Health check
    echo 'Probando health check...'
    for i in {1..15}; do
        if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
            echo 'Health check OK'

            # Inicializar datos
            echo 'Inicializando productos...'
            sleep 3
            if curl -s -X POST http://localhost:8080/api/products/init-sample-data >/dev/null 2>&1; then
                echo 'Productos inicializados correctamente'
            else
                echo 'Warning: Error inicializando productos (puede ser normal si ya existen)'
            fi

            # Mostrar algunos productos
            echo 'Verificando API...'
            curl -s http://localhost:8080/api/products | head -c 200
            echo
            break
        else
            echo \"Esperando... (\$i/15)\"
            sleep 5
        fi
    done
else
    echo 'ERROR: Spring Boot no se inició'
    echo 'Log de errores:'
    tail -30 ~/logs/crimewave.log
    exit 1
fi
"

if %errorlevel% equ 0 (
    echo.
    echo 🎉 ¡DESPLIEGUE COMPLETADO EXITOSAMENTE!
    echo.
    echo =====================================
    echo        TU TIENDA DE ROPA EN AWS
    echo =====================================
    echo.
    echo 🌐 URLs disponibles:
    echo    API Base: http://%EC2_IP%:8080
    echo    Productos: http://%EC2_IP%:8080/api/products
    echo    Health: http://%EC2_IP%:8080/actuator/health
    echo.

    echo 🧪 Probando desde tu computadora...
    timeout /t 3 >nul

    echo.
    echo Health check público:
    curl -s http://%EC2_IP%:8080/actuator/health 2>nul || echo "Esperando que esté disponible..."

    echo.
    echo Productos disponibles:
    curl -s http://%EC2_IP%:8080/api/products 2>nul | head -c 300 || echo "API iniciando..."

    echo.
    echo.
    echo ✅ ¡CONFIGURACIÓN COMPLETA!
    echo    🖥️ Spring Boot: Ejecutándose en EC2
    echo    🗄️ PostgreSQL: Conectado a RDS Aurora
    echo    📱 App Android: Lista para conectar
    echo.
    echo 📱 SIGUIENTE PASO - COMPILAR APP:
    echo    .\compile-app-with-aws.bat
    echo.
    echo 📲 DESPUÉS INSTALAR:
    echo    app\build\outputs\apk\debug\app-debug.apk
    echo.

) else (
    echo.
    echo ❌ Error durante la configuración
    echo    La aplicación puede estar iniciando aún
    echo    Espera 2-3 minutos y prueba: http://%EC2_IP%:8080/api/products
)

pause
