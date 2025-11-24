#!/bin/bash
echo "Configurando servidor Ubuntu..."

# Actualizar sistema
sudo apt update -y

# Instalar Java 11 si no está instalado
if ! command -v java >/dev/null 2>&1; then
    echo "Instalando Java 11..."
    sudo apt install openjdk-11-jdk -y
else
    echo "Java ya instalado"
fi

echo "Java version:"
java -version

# Detener aplicación anterior si existe
echo "Deteniendo aplicación anterior..."
pkill -f 'microservice-0.0.1-SNAPSHOT.jar' || true
sleep 3

# Crear directorio de logs
mkdir -p ~/logs

# Ejecutar Spring Boot en background
echo "Iniciando Spring Boot con RDS Aurora..."
nohup java -jar ~/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=~/application-aws.properties > ~/logs/crimewave.log 2>&1 &

# Esperar que inicie la aplicación
echo "Esperando inicio (60 segundos)..."
sleep 60

# Verificar que el proceso esté ejecutándose
if pgrep -f 'microservice-0.0.1-SNAPSHOT.jar' >/dev/null; then
    echo "✅ Spring Boot ejecutándose correctamente"

    # Intentar health check múltiples veces
    echo "Probando health check..."
    for i in {1..20}; do
        if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
            echo "✅ Health check exitoso en intento $i"

            # Inicializar datos de productos
            echo "Inicializando productos de la tienda..."
            sleep 3
            if curl -s -X POST http://localhost:8080/api/products/init-sample-data; then
                echo "✅ Productos inicializados correctamente"
            else
                echo "⚠️ Error inicializando productos (puede ser normal si ya existen)"
            fi

            # Mostrar algunos productos para verificar
            echo "Verificando API de productos..."
            curl -s http://localhost:8080/api/products | head -c 300
            echo ""
            echo "✅ API funcionando correctamente"
            exit 0
        else
            echo "Esperando... ($i/20)"
            sleep 10
        fi
    done

    echo "❌ Health check falló después de múltiples intentos"
    echo "Mostrando log de errores:"
    tail -50 ~/logs/crimewave.log
    exit 1
else
    echo "❌ Spring Boot no se inició correctamente"
    echo "Log de errores:"
    tail -50 ~/logs/crimewave.log
    exit 1
fi
