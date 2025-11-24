# 🚀 PASOS PARA DESPLEGAR TU MICROSERVICIO EN AWS EC2

## 📋 **ESTADO ACTUAL:**
- ✅ **RDS PostgreSQL**: Ya configurado (crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com)
- ✅ **Microservicio**: Listo para compilar
- ✅ **App Android**: Funcionando correctamente
- 🔄 **Falta**: Crear EC2 y desplegar

---

## 🖥️ **PASO 1: CREAR INSTANCIA EC2**

### **1.1 Ir a AWS Console:**
- Entra a [aws.amazon.com](https://aws.amazon.com)
- Ve a **EC2 Dashboard**
- Click **"Launch Instance"**

### **1.2 Configurar Instancia:**
```
Name: crimewave-server
OS: Ubuntu Server 22.04 LTS (Free tier eligible)
Instance type: t3.micro (Free tier eligible) ⚠️ IMPORTANTE
Key pair: Crear nuevo "crimewave-key.pem" ⬇️ DESCARGAR

Security Group:
- SSH (22): My IP
- HTTP (80): 0.0.0.0/0
- Custom TCP (8080): 0.0.0.0/0 ⚠️ Para Spring Boot
- HTTPS (443): 0.0.0.0/0

Storage: 8 GB gp3 (Free tier)
```

### **1.3 Anotar IP:**
Una vez creada, anota tu **IP pública**: `___.___.___.___`

---

## 📦 **PASO 2: COMPILAR MICROSERVICIO**

### **2.1 Ejecutar compilación:**
```bash
compile-microservice-for-aws.bat
```

### **2.2 Verificar JAR:**
Debe generar: `microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar`

---

## 🚀 **PASO 3: DESPLEGAR A EC2**

### **3.1 Despliegue automático:**
```bash
deploy-to-aws.bat TU-IP-EC2 ruta\a\crimewave-key.pem
```

**Ejemplo:**
```bash
deploy-to-aws.bat 54.123.45.67 C:\Users\sekai\Downloads\crimewave-key.pem
```

### **3.2 ¿No tienes el script? Despliegue manual:**

**Subir archivos:**
```bash
scp -i "crimewave-key.pem" microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar ubuntu@TU-IP-EC2:/home/ubuntu/
scp -i "crimewave-key.pem" microservice\application-aws.properties ubuntu@TU-IP-EC2:/home/ubuntu/
```

**Conectar SSH:**
```bash
ssh -i "crimewave-key.pem" ubuntu@TU-IP-EC2
```

**En el servidor EC2:**
```bash
# Instalar Java
sudo apt update
sudo apt install openjdk-11-jdk -y

# Ejecutar aplicación
java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties
```

---

## 🧪 **PASO 4: PROBAR**

### **4.1 Verificar endpoints:**
```bash
# Health check
curl http://TU-IP-EC2:8080/actuator/health

# Ver productos
curl http://TU-IP-EC2:8080/api/products

# Inicializar datos
curl -X POST http://TU-IP-EC2:8080/api/products/init-sample-data
```

### **4.2 Probar desde navegador:**
- Ve a: `http://TU-IP-EC2:8080/api/products`
- Deberías ver la respuesta JSON

---

## 📱 **PASO 5: CONECTAR APP ANDROID**

### **5.1 Actualizar IP en la app:**
En `ServerConfig.kt`, cambiar:
```kotlin
private const val AWS_EC2 = "http://TU-IP-EC2:8080/"
```

### **5.2 Compilar y probar:**
```bash
.\gradlew :app:assembleDebug
```

---

## ✅ **RESULTADO FINAL:**
- 🖥️ **Spring Boot** corriendo en EC2
- 🗄️ **PostgreSQL** en RDS Aurora
- 📱 **App Android** conectada a AWS
- 🛍️ **Tienda de ropa** 100% en la nube

---

## 💰 **COSTOS (GRATIS 12 MESES):**
- **EC2 t3.micro**: 750 horas/mes gratis
- **RDS db.t3.micro**: 750 horas/mes gratis
- **Almacenamiento**: 30 GB gratis

---

## 🔄 **SIGUIENTES PASOS:**

1. **Crear EC2** (10 minutos)
2. **Compilar JAR** (2 minutos) 
3. **Desplegar** (5 minutos)
4. **Probar** (3 minutos)
5. **Conectar app** (2 minutos)

**¡Total: 22 minutos para tener todo funcionando en AWS!** 🎉
