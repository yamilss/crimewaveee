# ✅ CHECKLIST: DESPLEGAR CRIMEWAVE EN AWS

## 📋 **ANTES DE EMPEZAR**
- [ ] Cuenta AWS creada y verificada
- [ ] Tarjeta de crédito agregada (no se cobra en Free Tier)
- [ ] Git Bash o WSL instalado en Windows
- [ ] Proyecto compilando correctamente en local

---

## 🖥️ **PASO 1: CREAR EC2 (10 minutos)**
- [ ] Ir a EC2 Dashboard en AWS
- [ ] Launch Instance
- [ ] **Nombre**: `crimewave-server`
- [ ] **OS**: Ubuntu Server 22.04 LTS
- [ ] **Tipo**: `t3.micro` o `t2.micro` (Free tier eligible) ⚠️ IMPORTANTE
  - Si no aparece t2.micro, usar **t3.micro** (es más moderna y también gratuita)
- [ ] **Key Pair**: Crear nuevo `crimewave-key.pem` y descargar
- [ ] **Security Group**: 
  - [ ] SSH (22): Tu IP
  - [ ] HTTP (80): Anywhere
  - [ ] Custom TCP (8080): Anywhere ⚠️ IMPORTANTE
- [ ] **Storage**: 8 GB (Free tier)
- [ ] Launch Instance
- [ ] **Anotar IP pública**: `_____._____._____.____`

---

## 🗄️ **PASO 2: CREAR RDS (15 minutos)**

### ⚠️ **CONFIGURACIÓN CRÍTICA PARA FREE TIER:**
**Si ves errores como "backup retention period exceeds maximum", usa estos valores:**
- **Backup retention period**: `1 día` (máximo gratuito)
- **Enhanced monitoring**: `Deshabilitado` 
- **Performance Insights**: `Deshabilitado`
- **Multi-AZ**: `NO` (solo Single-AZ es gratuito)

### **Pasos para RDS:**
- [ ] Ir a RDS Dashboard
- [ ] Create Database
- [ ] **Engine**: PostgreSQL
- [ ] **Templates**: `Free tier` ⚠️ IMPORTANTE
- [ ] **DB identifier**: `crimewave-db`
- [ ] **Username**: `postgres`
- [ ] **Password**: `CrimeWave2024!`
- [ ] **Instance class**: `db.t3.micro` (Free tier eligible) ⚠️ IMPORTANTE
- [ ] **Storage**: 20 GB (máximo gratuito)
- [ ] **Public access**: `Yes` ⚠️ IMPORTANTE
- [ ] **VPC security group**: Usar el mismo de EC2 o crear nuevo
- [ ] **Database name**: `crimewave_products`
- [ ] **⚠️ CONFIGURACIÓN ADICIONAL (Expandir):**
  - [ ] **Backup retention period**: `1 día` (máximo para Free Tier)
  - [ ] **Enable automated backups**: `Yes` 
  - [ ] **Backup window**: `No preference`
  - [ ] **Enable Enhanced monitoring**: `NO` ⚠️ (genera costos)
  - [ ] **Enable Performance Insights**: `NO` ⚠️ (genera costos)
- [ ] Create Database
- [ ] Esperar hasta "Available" (5-10 minutos)
- [ ] **Anotar endpoint**: `crimewave-db.xxxxx.region.rds.amazonaws.com`

---

## 🔒 **PASO 3: CONFIGURAR SECURITY GROUPS (5 minutos)**
- [ ] EC2 > Security Groups
- [ ] Buscar security group de EC2
- [ ] Edit Inbound Rules
- [ ] Agregar regla:
  - [ ] **Type**: PostgreSQL
  - [ ] **Port**: 5432
  - [ ] **Source**: Anywhere (0.0.0.0/0)
- [ ] Save rules

---

## ⚙️ **PASO 4: CONFIGURAR APLICACIÓN (5 minutos)**
- [ ] Abrir `microservice/application-aws.properties`
- [ ] Reemplazar endpoint RDS:
  ```
  spring.datasource.url=jdbc:postgresql://TU-ENDPOINT-RDS:5432/crimewave_products
  ```
- [ ] Actualizar `ServerConfig.kt` con IP de EC2:
  ```kotlin
  private const val AWS_EC2 = "http://TU-IP-EC2:8080/"
  ```

---

## 🚀 **PASO 5: DESPLEGAR (10 minutos)**

### Opción A: Automático (Recomendado)
- [ ] Abrir terminal en la carpeta del proyecto
- [ ] Ejecutar:
  ```bash
  # Windows
  deploy-to-aws.bat TU-IP-EC2 ruta\al\crimewave-key.pem
  
  # Linux/Mac
  ./deploy-to-aws.sh TU-IP-EC2 ruta/al/crimewave-key.pem
  ```

### Opción B: Manual
- [ ] Construir JAR: `cd microservice && ./gradlew bootJar`
- [ ] Subir archivos:
  ```bash
  scp -i crimewave-key.pem microservice/build/libs/microservice-0.0.1-SNAPSHOT.jar ubuntu@TU-IP-EC2:/home/ubuntu/
  scp -i crimewave-key.pem microservice/application-aws.properties ubuntu@TU-IP-EC2:/home/ubuntu/
  ```
- [ ] Conectar SSH:
  ```bash
  ssh -i crimewave-key.pem ubuntu@TU-IP-EC2
  ```
- [ ] Instalar Java: `sudo apt update && sudo apt install openjdk-11-jdk -y`
- [ ] Ejecutar app:
  ```bash
  nohup java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties > app.log 2>&1 &
  ```

---

## 🧪 **PASO 6: PROBAR (5 minutos)**
- [ ] Verificar salud: `curl http://TU-IP-EC2:8080/actuator/health`
- [ ] Ver productos: `curl http://TU-IP-EC2:8080/api/products`
- [ ] Inicializar datos: `curl -X POST http://TU-IP-EC2:8080/api/products/init-sample-data`
- [ ] Verificar datos: `curl http://TU-IP-EC2:8080/api/products`

---

## 📱 **PASO 7: PROBAR APP ANDROID (5 minutos)**
- [ ] Compilar app: `./gradlew assembleDebug`
- [ ] Instalar en dispositivo
- [ ] Abrir app y verificar que carga productos
- [ ] Probar crear/editar productos desde el panel de administración
- [ ] Verificar que los cambios se reflejan en la base de datos AWS

---

## ✅ **VERIFICACIÓN FINAL**
- [ ] **EC2**: Instancia corriendo sin errores
- [ ] **RDS**: Base de datos "Available"
- [ ] **Security Groups**: Puertos 22, 80, 8080, 5432 abiertos
- [ ] **App Spring Boot**: Responde en `http://TU-IP-EC2:8080/api/products`
- [ ] **App Android**: Se conecta y muestra datos
- [ ] **Logs**: Sin errores críticos en `tail -f ~/logs/crimewave.log`

---

## 🚨 **SOLUCIÓN DE PROBLEMAS**

### Error de conexión SSH:
```bash
# Cambiar permisos del archivo key
chmod 400 crimewave-key.pem
```

### Error de conexión a RDS:
- [ ] Verificar que Public Access = Yes
- [ ] Verificar Security Group permite puerto 5432
- [ ] Verificar endpoint y credenciales en application-aws.properties

### Error 500 en Spring Boot:
```bash
# Ver logs completos
ssh -i crimewave-key.pem ubuntu@TU-IP-EC2
tail -50 ~/logs/crimewave.log
```

### App Android no conecta:
- [ ] Verificar IP en ServerConfig.kt
- [ ] Verificar que puerto 8080 está abierto
- [ ] Probar desde navegador: `http://TU-IP-EC2:8080/api/products`

---

## 💰 **MONITOREAR COSTOS**
- [ ] Configurar alertas de billing en AWS
- [ ] Revisar AWS Free Tier usage dashboard
- [ ] **No exceder**: 750 horas/mes EC2 (t2.micro o t3.micro) + 750 horas/mes RDS

---

## 🎉 **¡LISTO!**

Tu aplicación CrimeWave ahora está funcionando en AWS con:
- ✅ **EC2**: Servidor Spring Boot en la nube
- ✅ **RDS**: Base de datos PostgreSQL administrada
- ✅ **App Android**: Conectada a la infraestructura AWS
- ✅ **CRUD**: Funcionando completamente en producción

**URLs Finales:**
- 🌐 API: `http://TU-IP-EC2:8080/api/products`
- 🔍 Health: `http://TU-IP-EC2:8080/actuator/health`
- 📱 App Android conectada a AWS
