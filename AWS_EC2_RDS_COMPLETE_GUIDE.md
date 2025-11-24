# 🚀 GUÍA COMPLETA: SPRING BOOT EN AWS EC2 + RDS GRATIS

## 🎯 **QUÉ VAMOS A HACER:**
1. Crear instancia **EC2 Free Tier** (servidor gratuito)
2. Crear base de datos **RDS PostgreSQL Free Tier**
3. Subir tu microservicio Spring Boot a EC2
4. Conectar la app Android al servidor AWS

---

## 📋 **PASO 1: CREAR CUENTA AWS FREE TIER**

### 1.1 Registrarse en AWS:
- Ve a [aws.amazon.com](https://aws.amazon.com)
- Crear cuenta gratuita (necesitas tarjeta de crédito, pero no se cobra)
- Free Tier: **12 meses gratis**

### 1.2 Límites Free Tier importantes:
- **EC2**: t3.micro o t2.micro, 750 horas/mes (= 24/7 por 31 días)
- **RDS**: db.t3.micro, 750 horas/mes, 20GB storage
- **Data Transfer**: 1GB salida gratis/mes

---

## 🖥️ **PASO 2: CREAR INSTANCIA EC2**

### 2.1 Ir a EC2 Dashboard:
1. En la consola AWS, buscar "EC2"
2. Click "Launch Instance"

### 2.2 Configurar la instancia:
```
Name: crimewave-server

Application and OS Images:
- Quick Start: Ubuntu Server 22.04 LTS (HVM), SSD
- Architecture: 64-bit (x86)

Instance type:
- t3.micro o t2.micro (Free tier eligible) ← MUY IMPORTANTE
- Si no ves t2.micro disponible, usar t3.micro (es más moderna)

Key pair:
- Create new key pair
- Key pair name: crimewave-key
- Key pair type: RSA
- Private key file format: .pem
- ¡DESCARGAR Y GUARDAR EL ARCHIVO .pem!

Network settings:
- Create security group
- Security group name: crimewave-sg
- Description: Security group for CrimeWave app
- 
- SSH (Port 22): My IP (solo tu IP)
- Add rule: HTTP (Port 80): Anywhere IPv4 (0.0.0.0/0)
- Add rule: Custom TCP (Port 8080): Anywhere IPv4 (0.0.0.0/0) ← Para Spring Boot
- Add rule: HTTPS (Port 443): Anywhere IPv4 (0.0.0.0/0)

Configure storage:
- 8 GiB gp3 (Free tier eligible)
- ✅ Delete on termination: Yes

Advanced details:
- Dejar todo por defecto
```

### 2.3 Lanzar la instancia:
- Review y "Launch Instance"
- Anotar la **IP pública** que se asigna

---

## 🗄️ **PASO 3: CREAR BASE DE DATOS RDS**

### 3.1 Ir a RDS:
1. En consola AWS, buscar "RDS"
2. Click "Create database"

### 3.2 Configurar RDS:
```
Engine options:
- Engine type: PostgreSQL
- Engine Version: PostgreSQL 15.x (latest)

Templates:
- Free tier ← MUY IMPORTANTE

Settings:
- DB instance identifier: crimewave-db
- Master username: postgres
- Master password: CrimeWave2024!
- Confirm password: CrimeWave2024!

DB instance class:
- Burstable classes: db.t3.micro (Free tier eligible)

Storage:
- Storage type: General Purpose SSD (gp2)
- Allocated storage: 20 (máximo gratuito)
- ✅ Enable storage autoscaling: NO

Connectivity:
- Compute resource: Don't connect to an EC2 compute resource
- Network type: IPv4
- Virtual private cloud (VPC): Default VPC
- DB subnet group: default
- Public access: Yes ← IMPORTANTE
- VPC security group: Choose existing
- Existing VPC security groups: default + crimewave-sg
- Availability Zone: No preference
- Database port: 5432

Database authentication:
- Password authentication

Monitoring:
- ✅ Turn on Performance Insights: NO
- Monitoring role: Default

Additional configuration:
- Initial database name: crimewave_products
- DB parameter group: default
- Backup retention period: 1 day ← IMPORTANTE: Máximo para Free Tier
- Backup window: No preference
- ✅ Copy tags to snapshots: NO
- ✅ Enable Enhanced monitoring: NO (genera costos extra)
- ✅ Enable Performance Insights: NO (genera costos extra)
- ✅ Enable auto minor version upgrade: YES
- Maintenance window: No preference
- ✅ Enable deletion protection: NO
```

### 3.3 Crear y anotar datos:
- Click "Create database"
- Esperar 5-10 minutos hasta "Available"
- **Anotar el Endpoint** (ej: crimewave-db.xxxxx.us-east-1.rds.amazonaws.com)

---

## 🔒 **PASO 4: CONFIGURAR SECURITY GROUPS**

### 4.1 Editar Security Group de RDS:
1. EC2 > Security Groups
2. Buscar "crimewave-sg"
3. Inbound rules > Edit

### 4.2 Agregar regla PostgreSQL:
```
Type: PostgreSQL
Protocol: TCP
Port Range: 5432
Source: Anywhere IPv4 (0.0.0.0/0)
Description: PostgreSQL for CrimeWave
```

---

## 📦 **PASO 5: PREPARAR EL MICROSERVICIO**

### 5.1 Actualizar configuración para AWS:
```properties
# application-aws.properties
server.port=8080

# AWS RDS PostgreSQL
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
- **EC2 t2.micro**: 750 horas/mes
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

¡Ya tienes tu Spring Boot corriendo en AWS totalmente gratis! 🎉
