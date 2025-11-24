# 🚀 CONFIGURACIÓN AWS RDS FREE TIER PARA CRIMEWAVE

## 📋 **PASOS PARA CREAR LA BASE DE DATOS GRATIS EN AWS**

### **Paso 1: Crear Cuenta AWS Free Tier**
1. Ve a [aws.amazon.com](https://aws.amazon.com)
2. Crea una cuenta gratuita (12 meses gratis)
3. Inicia sesión en la consola AWS

### **Paso 2: Crear Instancia RDS PostgreSQL**

#### **2.1 Ir a RDS:**
- En la consola AWS, busca "RDS"
- Click en "Create database"

#### **2.2 Configuración de la Base de Datos:**
```
Engine Type: PostgreSQL
Engine Version: PostgreSQL 15.x (o la más reciente disponible)

Templates: 🎯 FREE TIER (MUY IMPORTANTE)

Settings:
- DB instance identifier: crimewave-db
- Master username: postgres
- Master password: CrimeWave2024!
- Confirm password: CrimeWave2024!

DB Instance Class: 
- Burstable classes: db.t3.micro (elegible para capa gratuita)

Storage:
- Storage type: General Purpose SSD (gp2)
- Allocated storage: 20 GB (máximo gratuito)
- ✅ Enable storage autoscaling: NO (para evitar costos)

Connectivity:
- Compute resource: Don't connect to an EC2 compute resource
- Network type: IPv4
- Virtual private cloud (VPC): Default VPC
- DB subnet group: default
- Public access: 🎯 YES (IMPORTANTE para acceso desde tu app)
- VPC security group: Create new
  - Security group name: crimewave-sg
- Availability Zone: No preference
- Database port: 5432

Database Authentication:
- Database authentication options: Password authentication

Monitoring:
- ✅ Turn on Performance Insights: NO (para evitar costos)

Additional Configuration:
- Initial database name: crimewave_products
- Backup retention period: 7 days (mínimo)
- ✅ Enable automated backups: YES
- Backup window: No preference
- ✅ Copy tags to snapshots: NO
- ✅ Enable Enhanced monitoring: NO
- Log exports: None (para ahorrar espacio)
- ✅ Enable auto minor version upgrade: YES
- Maintenance window: No preference
- ✅ Enable deletion protection: NO (para poder eliminar fácilmente)
```

#### **2.3 Crear la Base de Datos:**
- Click "Create database"
- Esperar 5-10 minutos hasta que esté "Available"

### **Paso 3: Configurar Seguridad (Security Group)**

#### **3.1 Editar Security Group:**
1. Ve a EC2 > Security Groups
2. Busca el grupo "crimewave-sg"
3. Click "Edit inbound rules"

#### **3.2 Agregar Regla:**
```
Type: PostgreSQL
Protocol: TCP
Port: 5432
Source: 0.0.0.0/0 (Anywhere IPv4)
Description: PostgreSQL access for CrimeWave app
```

### **Paso 4: Obtener Información de Conexión**

#### **4.1 En RDS Databases:**
- Click en tu instancia "crimewave-db"
- En "Connectivity & security" encontrarás:

```
Endpoint: crimewave-db.xxxxxxxxxxxx.us-east-1.rds.amazonaws.com
Port: 5432
```

### **Paso 5: Actualizar Configuración del Microservicio**

#### **5.1 Editar application.properties:**
Reemplaza en `microservice/src/main/resources/application.properties`:

```properties
# Reemplaza con TU endpoint real de AWS RDS
spring.datasource.url=jdbc:postgresql://crimewave-db.xxxxxxxxxxxx.us-east-1.rds.amazonaws.com:5432/crimewave_products
spring.datasource.username=postgres
spring.datasource.password=CrimeWave2024!
```

### **Paso 6: Probar Conexión**

#### **6.1 Iniciar el Microservicio:**
```bash
cd microservice
./gradlew bootRun
```

#### **6.2 Verificar que conecta:**
- El log debería mostrar: "Started CrimewaveProductsApplication"
- Sin errores de conexión a PostgreSQL

#### **6.3 Inicializar Datos de Prueba:**
```bash
# Llamar endpoint para crear datos iniciales
curl -X POST http://localhost:8080/api/products/init-sample-data
```

### **Paso 7: Probar desde la App Android**

#### **7.1 Compilar la App:**
```bash
cd ..
./gradlew assembleDebug
```

#### **7.2 En la app:**
- Ve a "Gestión de Productos" (si eres admin)
- Deberías ver los productos cargados desde AWS RDS
- Prueba crear, editar y eliminar productos

---

## 💰 **COSTOS AWS FREE TIER**

### **✅ GRATIS por 12 meses:**
- **RDS**: db.t3.micro por 750 horas/mes
- **Storage**: 20 GB de almacenamiento SSD
- **Backups**: 20 GB de backup storage
- **Data Transfer**: 1 GB salida por mes

### **⚠️ LÍMITES IMPORTANTES:**
- Solo 1 instancia db.t3.micro
- Máximo 20 GB de almacenamiento
- No exceder 750 horas/mes (= 31 días completos)

### **💡 RECOMENDACIONES:**
- Siempre para la instancia cuando no la uses
- Configura alertas de billing
- Elimina la instancia antes de que termine el free tier

---

## 🔧 **COMANDOS ÚTILES**

### **Conectar a la BD desde terminal (opcional):**
```bash
psql -h crimewave-db.xxxxxxxxxxxx.us-east-1.rds.amazonaws.com -U postgres -d crimewave_products
```

### **Probar endpoints del microservicio:**
```bash
# Ver todos los productos
curl http://localhost:8080/api/products

# Crear producto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "id": "5",
    "name": "Nueva Polera",
    "description": "Descripción del producto",
    "price": 25000,
    "imageUrl": "imagen.jpg",
    "category": "POLERAS",
    "stock": 100
  }'

# Ver productos destacados
curl http://localhost:8080/api/products/featured

# Ver productos por categoría
curl http://localhost:8080/api/products/category/POLERAS
```

---

## 🚨 **TROUBLESHOOTING**

### **Error de conexión:**
1. Verifica que el Security Group permite conexiones en puerto 5432
2. Confirma que "Public access" está en YES
3. Verifica el endpoint y credenciales

### **Error "relation does not exist":**
- Las tablas se crean automáticamente por JPA
- Verifica que `spring.jpa.hibernate.ddl-auto=update` esté configurado

### **Error de timeout:**
- Aumenta el `connection-timeout` en application.properties
- Verifica la conectividad de red

¡Ya tienes tu base de datos PostgreSQL gratuita en AWS lista para usar! 🎉
