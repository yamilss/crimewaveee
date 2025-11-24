# 🚀 GUÍA COMPLETA: DESPLEGAR SPRING BOOT EN AWS EC2 + RDS

## 📦 **PASO 5: PREPARAR EL MICROSERVICIO**

### 5.1 Actualizar configuración para AWS:

Necesitas crear el archivo `application-aws.properties` en tu carpeta `microservice`:

```properties
# application-aws.properties
server.port=8080

# AWS RDS PostgreSQL
# IMPORTANTE: Reemplaza TU-RDS-ENDPOINT con tu endpoint real de RDS
spring.datasource.url=jdbc:postgresql://TU-RDS-ENDPOINT:5432/crimewave_products
spring.datasource.username=postgres
spring.datasource.password=CrimeWave2024!
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Pool de conexiones optimizado para AWS
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000

# CORS para permitir conexiones desde app móvil
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

# Logging
logging.level.com.example.crimewavee=INFO
logging.level.org.springframework.web=WARN
```

### 5.2 Crear JAR ejecutable:
```bash
cd microservice
./gradlew bootJar
```

Esto genera: `build/libs/microservice-0.0.1-SNAPSHOT.jar`

---

## 🚀 **PASO 6: SUBIR A EC2**

### 6.1 Conectar por SSH:
```bash
# En Windows (usar Git Bash o WSL)
ssh -i "ruta/a/crimewave-key.pem" ubuntu@TU-IP-EC2

# En la instancia EC2, actualizar el sistema:
sudo apt update
sudo apt upgrade -y
```

### 6.2 Instalar Java en EC2:
```bash
# Instalar OpenJDK 11
sudo apt install openjdk-11-jdk -y

# Verificar instalación
java -version
```

### 6.3 Subir el JAR a EC2:
```bash
# Desde tu máquina local (nueva terminal)
scp -i "crimewave-key.pem" microservice/build/libs/microservice-0.0.1-SNAPSHOT.jar ubuntu@TU-IP-EC2:/home/ubuntu/
```

### 6.4 Crear archivo de configuración AWS:
```bash
# Conectado por SSH a EC2:
nano application-aws.properties

# Pegar la configuración del paso 5.1 con TU endpoint RDS real
```

---

## ▶️ **PASO 7: EJECUTAR EN EC2**

### 7.1 Ejecutar el microservicio:
```bash
# En EC2 por SSH:
java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties

# O en background:
nohup java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties > app.log 2>&1 &
```

### 7.2 Verificar que funciona:
```bash
# Desde EC2:
curl http://localhost:8080/api/products

# Desde tu máquina:
curl http://TU-IP-EC2:8080/api/products
```

### 7.3 Inicializar datos:
```bash
curl -X POST http://TU-IP-EC2:8080/api/products/init-sample-data
```

---

## 📱 **PASO 8: CONECTAR APP ANDROID**

### 8.1 Actualizar ProductRepository:
En `ProductRepository.kt`, cambiar:
```kotlin
private val baseUrl = "http://TU-IP-EC2:8080/" // Reemplazar con tu IP de EC2
```

### 8.2 Compilar y probar:
```bash
./gradlew assembleDebug
```

---

## 🔄 **PASO 9: CREAR SERVICIO SYSTEMD (OPCIONAL)**

### 9.1 Para que se inicie automáticamente:
```bash
# En EC2:
sudo nano /etc/systemd/system/crimewave.service
```

```ini
[Unit]
Description=CrimeWave Spring Boot Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/microservice-0.0.1-SNAPSHOT.jar --spring.config.location=/home/ubuntu/application-aws.properties
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# Activar el servicio:
sudo systemctl daemon-reload
sudo systemctl enable crimewave
sudo systemctl start crimewave
sudo systemctl status crimewave
```

---

## 💰 **COSTOS Y LÍMITES FREE TIER**

### ✅ **GRATIS por 12 meses:**
- **EC2 t3.micro**: 750 horas/mes
- **RDS db.t3.micro**: 750 horas/mes  
- **EBS**: 30 GB de almacenamiento
- **Data Transfer**: 1 GB salida/mes

### ⚠️ **CUIDADO CON:**
- No crear múltiples instancias
- Monitorear uso de datos
- Parar instancias cuando no las uses

---

## 🔧 **COMANDOS ÚTILES**

### Gestionar EC2:
```bash
# Conectar
ssh -i "crimewave-key.pem" ubuntu@TU-IP-EC2

# Ver logs
tail -f app.log

# Parar aplicación
pkill -f "microservice-0.0.1-SNAPSHOT.jar"

# Estado del sistema
sudo systemctl status crimewave
```

### Endpoints finales:
```bash
# Ver productos
http://TU-IP-EC2:8080/api/products

# Crear producto
http://TU-IP-EC2:8080/api/products

# Health check
http://TU-IP-EC2:8080/actuator/health
```

---

## 🚨 **TROUBLESHOOTING**

### Error de conexión:
1. ✅ Security Group permite puerto 8080
2. ✅ RDS permite puerto 5432
3. ✅ Credentials correctas en application-aws.properties

### Error de memoria:
```bash
# Ejecutar con menos memoria
java -Xmx256m -jar microservice-0.0.1-SNAPSHOT.jar
```

### Ver logs de error:
```bash
journalctl -u crimewave -f
```

---

## 🎯 **RESUMEN DE LO QUE TIENES QUE HACER:**

1. **Crear application-aws.properties** con tu endpoint RDS real
2. **Compilar JAR**: `./gradlew bootJar`
3. **Subir JAR a EC2** con scp
4. **Conectar por SSH** a EC2
5. **Instalar Java** en EC2
6. **Ejecutar aplicación** en EC2
7. **Probar endpoints** 
8. **Conectar app Android** cambiando IP

¡Ya tienes tu Spring Boot corriendo en AWS totalmente gratis! 🎉
