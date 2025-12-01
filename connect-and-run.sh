#!/bin/bash
# Script para conectar y ejecutar CrimeWave directamente
# Uso: bash connect-and-run.sh

KEY_FILE="~/Downloads/crimewave-key.pem"
EC2_IP="3.21.53.102"
EC2_USER="ubuntu"

echo "🔧 Conectando y solucionando el problema..."
echo "🌐 IP: $EC2_IP"

ssh -i "$KEY_FILE" "$EC2_USER@$EC2_IP" << 'EOF'
echo "🔍 Verificando archivos en el servidor..."
ls -la ~/microservice-0.0.1-SNAPSHOT.jar
ls -la ~/application-aws.properties

echo ""
echo "🛑 Deteniendo procesos Java anteriores..."
pkill -f microservice-0.0.1-SNAPSHOT.jar || echo "No había procesos Java ejecutándose"

echo ""
echo "📁 Creando directorio de logs..."
mkdir -p /home/ubuntu/logs

echo ""
echo "🚀 Iniciando CrimeWave con rutas absolutas..."
nohup java -jar /home/ubuntu/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=/home/ubuntu/application-aws.properties > /home/ubuntu/logs/crimewave.log 2>&1 &

echo ""
echo "⏳ Esperando que inicie..."
sleep 10

echo ""
echo "🔍 Verificando procesos..."
if pgrep -f "microservice-0.0.1-SNAPSHOT.jar" > /dev/null; then
    echo "✅ ¡Proceso Java encontrado!"
    pgrep -f "microservice-0.0.1-SNAPSHOT.jar"
else
    echo "❌ No se encontró proceso Java"
fi

echo ""
echo "📋 Últimas 15 líneas del log:"
tail -15 /home/ubuntu/logs/crimewave.log

echo ""
echo "🌐 Verificando puerto 8080..."
netstat -tlnp | grep :8080 || echo "❌ Puerto 8080 no está en uso"

echo ""
echo "🧪 Probando endpoint local..."
sleep 5
curl -s localhost:8080/api/products || echo "❌ No responde localmente"

EOF

echo ""
echo "🧪 Probando desde tu máquina..."
curl -s http://3.21.53.102:8080/api/products || echo "❌ No responde externamente"

echo ""
echo "📋 Si funciona, usa estos endpoints en Postman:"
echo "   1. POST http://3.21.53.102:8080/api/products/init-sample-data"
echo "   2. GET  http://3.21.53.102:8080/api/products"
