#!/bin/bash

# Script para verificar y reiniciar el microservicio en AWS EC2
# Uso: ./check-and-restart.sh TU-IP-EC2 ruta/a/crimewave-key.pem

set -e

if [ "$#" -ne 2 ]; then
    echo "Uso: $0 <IP-EC2> <ruta-al-key.pem>"
    echo "Ejemplo: $0 3.21.53.102 ~/Downloads/crimewave-key.pem"
    exit 1
fi

EC2_IP=$1
KEY_FILE=$2
EC2_USER="ubuntu"

echo "🔍 Verificando estado del servicio en $EC2_IP..."

# Conectar y verificar estado
ssh -i "$KEY_FILE" "$EC2_USER@$EC2_IP" << 'EOF'
echo "📊 Estado actual del sistema:"
echo ""

# Verificar si el proceso está ejecutándose
echo "🔍 Procesos Java activos:"
pgrep -f "microservice-0.0.1-SNAPSHOT.jar" || echo "❌ No hay procesos Java ejecutándose"

# Verificar archivos
echo ""
echo "📁 Archivos en home:"
ls -la ~/*.jar ~/*.properties 2>/dev/null || echo "❌ No se encontraron archivos JAR o properties"

# Verificar logs
echo ""
echo "📋 Últimas líneas del log:"
if [ -f ~/logs/crimewave.log ]; then
    tail -20 ~/logs/crimewave.log
else
    echo "❌ No se encontró archivo de log"
fi

# Verificar puerto 8080
echo ""
echo "🌐 Puerto 8080:"
netstat -tlnp | grep :8080 || echo "❌ Puerto 8080 no está en uso"

# Reiniciar servicio
echo ""
echo "🔄 Reiniciando servicio..."

# Matar procesos existentes
pkill -f "microservice-0.0.1-SNAPSHOT.jar" || echo "No había procesos previos"
sleep 2

# Crear directorio de logs si no existe
mkdir -p ~/logs

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "📥 Java no encontrado, instalando..."
    sudo apt update
    sudo apt install openjdk-11-jdk -y
fi

# Iniciar aplicación
if [ -f ~/microservice-0.0.1-SNAPSHOT.jar ] && [ -f ~/application-aws.properties ]; then
    echo "▶️ Iniciando aplicación..."
    nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

    # Esperar y verificar
    echo "⏳ Esperando inicio..."
    sleep 10

    if pgrep -f "microservice-0.0.1-SNAPSHOT.jar" > /dev/null; then
        echo "✅ Aplicación iniciada correctamente"
        echo "📋 Últimas líneas del nuevo log:"
        tail -10 ~/logs/crimewave.log
    else
        echo "❌ Error al iniciar la aplicación"
        echo "📋 Log de errores:"
        tail -20 ~/logs/crimewave.log
        exit 1
    fi
else
    echo "❌ Archivos necesarios no encontrados. Ejecuta primero deploy-to-aws.sh"
    exit 1
fi

EOF

echo ""
echo "🧪 Probando endpoints:"
echo "curl http://$EC2_IP:8080/api/products"
curl -s "http://$EC2_IP:8080/api/products" || echo "❌ Error al conectar"

echo ""
echo "🎉 ¡Verificación completada!"
