#!/bin/bash

# Script para desplegar el microservicio en AWS EC2
# Uso: ./deploy-to-aws.sh TU-IP-EC2 ruta/a/crimewave-key.pem

set -e

if [ "$#" -ne 2 ]; then
    echo "Uso: $0 <IP-EC2> <ruta-al-key.pem>"
    echo "Ejemplo: $0 54.123.45.67 ~/Downloads/crimewave-key.pem"
    exit 1
fi

EC2_IP=$1
KEY_FILE=$2
EC2_USER="ubuntu"

echo "🚀 Desplegando CrimeWave a AWS EC2..."
echo "IP: $EC2_IP"
echo "Key: $KEY_FILE"

# Verificar que el archivo key existe
if [ ! -f "$KEY_FILE" ]; then
    echo "❌ Error: No se encuentra el archivo key: $KEY_FILE"
    exit 1
fi

# Construir el JAR
echo "📦 Construyendo JAR..."
cd microservice
./gradlew bootJar
cd ..

JAR_FILE="microservice/build/libs/microservice-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Error: No se pudo generar el JAR"
    exit 1
fi

# Subir archivos a EC2
echo "📤 Subiendo archivos a EC2..."
scp -i "$KEY_FILE" "$JAR_FILE" "$EC2_USER@$EC2_IP:/home/ubuntu/"
scp -i "$KEY_FILE" "microservice/application-aws.properties" "$EC2_USER@$EC2_IP:/home/ubuntu/"

# Conectar y configurar EC2
echo "⚙️ Configurando EC2..."
ssh -i "$KEY_FILE" "$EC2_USER@$EC2_IP" << 'EOF'
# Matar proceso anterior si existe
pkill -f "microservice-0.0.1-SNAPSHOT.jar" || true

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "📥 Instalando Java..."
    sudo apt update
    sudo apt install openjdk-11-jdk -y
fi

# Crear directorio de logs
mkdir -p ~/logs

# Ejecutar en background
echo "▶️ Iniciando aplicación..."
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

# Esperar y verificar
sleep 5
if pgrep -f "microservice-0.0.1-SNAPSHOT.jar" > /dev/null; then
    echo "✅ Aplicación iniciada correctamente"
else
    echo "❌ Error al iniciar la aplicación"
    exit 1
fi
EOF

echo "🎉 ¡Despliegue completado!"
echo ""
echo "📋 Información del despliegue:"
echo "   🌐 URL: http://$EC2_IP:8080"
echo "   📱 Endpoints: http://$EC2_IP:8080/api/products"
echo "   🔍 Health: http://$EC2_IP:8080/actuator/health"
echo ""
echo "🧪 Comandos para probar:"
echo "   curl http://$EC2_IP:8080/api/products"
echo "   curl -X POST http://$EC2_IP:8080/api/products/init-sample-data"
echo ""
echo "🔧 Para ver logs:"
echo "   ssh -i \"$KEY_FILE\" $EC2_USER@$EC2_IP 'tail -f ~/logs/crimewave.log'"
