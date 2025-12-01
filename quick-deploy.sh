#!/bin/bash
# Script simplificado de despliegue para CrimeWave
# Uso: bash quick-deploy.sh /ruta/a/tu-clave.pem

set -e

if [ "$#" -ne 1 ]; then
    echo "❌ Uso: bash quick-deploy.sh /ruta/a/tu-clave.pem"
    echo "📋 Ejemplo: bash quick-deploy.sh ~/Downloads/mi-clave.pem"
    exit 1
fi

KEY_FILE=$1
EC2_IP="3.21.53.102"
EC2_USER="ubuntu"

echo "🚀 Desplegando CrimeWave a AWS EC2..."
echo "🌐 IP: $EC2_IP"
echo "🔐 Key: $KEY_FILE"

# Verificar que el archivo key existe
if [ ! -f "$KEY_FILE" ]; then
    echo "❌ Error: No se encuentra el archivo key: $KEY_FILE"
    exit 1
fi

# Verificar que el JAR existe
JAR_FILE="microservice/build/libs/microservice-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Error: JAR no encontrado. Ejecuta primero SOLUCION-COMPLETA.bat"
    exit 1
fi

echo "📤 Subiendo archivos a EC2..."
scp -i "$KEY_FILE" "$JAR_FILE" "$EC2_USER@$EC2_IP:/home/ubuntu/"
scp -i "$KEY_FILE" "microservice/application-aws.properties" "$EC2_USER@$EC2_IP:/home/ubuntu/"

echo "⚙️ Configurando y ejecutando en EC2..."
ssh -i "$KEY_FILE" "$EC2_USER@$EC2_IP" << 'EOF'
# Matar proceso anterior si existe
pkill -f "microservice-0.0.1-SNAPSHOT.jar" || true
sleep 2

# Crear directorio de logs
mkdir -p /home/ubuntu/logs

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "📥 Instalando Java..."
    sudo apt update
    sudo apt install openjdk-11-jdk -y
fi

# Ejecutar aplicación
echo "▶️ Iniciando CrimeWave API..."
nohup java -jar /home/ubuntu/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=/home/ubuntu/application-aws.properties > /home/ubuntu/logs/crimewave.log 2>&1 &

# Esperar y verificar
sleep 8
if pgrep -f "microservice-0.0.1-SNAPSHOT.jar" > /dev/null; then
    echo "✅ ¡CrimeWave API iniciada correctamente!"
    echo "📋 Últimas líneas del log:"
    tail -5 /home/ubuntu/logs/crimewave.log
else
    echo "❌ Error al iniciar. Revisando logs..."
    tail -10 /home/ubuntu/logs/crimewave.log
    exit 1
fi
EOF

echo ""
echo "🎉 ¡Despliegue completado exitosamente!"
echo ""
echo "🧪 PROBAR AHORA EN POSTMAN:"
echo "   1. POST http://3.21.53.102:8080/api/products/init-sample-data"
echo "   2. GET  http://3.21.53.102:8080/api/products"
echo ""
echo "🔍 Verificar estado:"
echo "   curl http://3.21.53.102:8080/api/products"
