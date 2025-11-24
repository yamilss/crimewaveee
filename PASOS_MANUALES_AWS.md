# 📝 PASOS MANUALES PARA DESPLEGAR EN AWS

## ✋ **ANTES DE EMPEZAR:**
Asegúrate de tener:
- [ ] EC2 creada y funcionando
- [ ] RDS creada y "Available"
- [ ] IP pública de EC2: `_____._____._____.____`
- [ ] Endpoint de RDS: `crimewave-db.xxxxx.region.rds.amazonaws.com`
- [ ] Archivo `crimewave-key.pem` descargado

---

## 🔧 **PASO 1: PREPARAR CONFIGURACIÓN LOCAL**

### 1.1 Editar application-aws.properties:
```bash
# Abrir archivo
notepad microservice\application-aws.properties

# Reemplazar TU-RDS-ENDPOINT con tu endpoint real
# Cambiar: TU-RDS-ENDPOINT
# Por: crimewave-db.xxxxx.us-east-1.rds.amazonaws.com
```

### 1.2 Compilar JAR:
```bash
cd microservice
.\gradlew clean bootJar
cd ..
```

**Verificar:** Debe generar `microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar`

---

## 🚀 **PASO 2: SUBIR ARCHIVOS A EC2**

### 2.1 Subir JAR:
```bash
# Reemplazar TU-IP-EC2 con tu IP real
scp -i "crimewave-key.pem" microservice\build\libs\microservice-0.0.1-SNAPSHOT.jar ubuntu@TU-IP-EC2:/home/ubuntu/
```

### 2.2 Subir configuración:
```bash
scp -i "crimewave-key.pem" microservice\application-aws.properties ubuntu@TU-IP-EC2:/home/ubuntu/
```

---

## 💻 **PASO 3: CONFIGURAR EC2**

### 3.1 Conectar por SSH:
```bash
ssh -i "crimewave-key.pem" ubuntu@TU-IP-EC2
```

### 3.2 Actualizar sistema:
```bash
sudo apt update
sudo apt upgrade -y
```

### 3.3 Instalar Java:
```bash
sudo apt install openjdk-11-jdk -y
java -version
```

---

## ▶️ **PASO 4: EJECUTAR APLICACIÓN**

### 4.1 Probar ejecución:
```bash
# Ejecutar en primer plano (para ver errores)
java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties
```

**Si hay errores:**
- Verifica el endpoint RDS en application-aws.properties
- Verifica que RDS esté "Available"
- Verifica Security Group permite puerto 5432

### 4.2 Ejecutar en background:
```bash
# Si funciona bien, ejecutar en background
pkill -f "microservice" || true
nohup java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties > app.log 2>&1 &
```

---

## 🧪 **PASO 5: PROBAR**

### 5.1 Desde EC2:
```bash
# Probar localmente en EC2
curl http://localhost:8080/api/products

# Inicializar datos
curl -X POST http://localhost:8080/api/products/init-sample-data

# Ver productos
curl http://localhost:8080/api/products
```

### 5.2 Desde tu máquina:
```bash
# Reemplazar TU-IP-EC2 con tu IP real
curl http://TU-IP-EC2:8080/api/products
```

**Si no funciona desde tu máquina:**
- Verifica Security Group permite puerto 8080
- Verifica que la aplicación esté corriendo: `ps aux | grep microservice`

---

## 📱 **PASO 6: CONECTAR APP ANDROID**

### 6.1 Actualizar ServerConfig.kt:
```kotlin
// En app/src/main/java/com/example/crimewavee/data/repository/ServerConfig.kt
private const val AWS_EC2 = "http://TU-IP-EC2:8080/" // Cambiar por tu IP real
```

### 6.2 Compilar app:
```bash
.\gradlew assembleDebug
```

### 6.3 Instalar y probar:
- Instalar APK en dispositivo
- Abrir app
- Verificar que carga productos desde AWS

---

## 🔍 **COMANDOS DE VERIFICACIÓN**

### Ver logs en tiempo real:
```bash
ssh -i "crimewave-key.pem" ubuntu@TU-IP-EC2
tail -f app.log
```

### Estado de la aplicación:
```bash
ps aux | grep microservice
```

### Reiniciar aplicación:
```bash
pkill -f "microservice"
nohup java -jar microservice-0.0.1-SNAPSHOT.jar --spring.config.location=application-aws.properties > app.log 2>&1 &
```

---

## ✅ **CHECKLIST FINAL**

- [ ] JAR compilado exitosamente
- [ ] Archivos subidos a EC2
- [ ] Java instalado en EC2
- [ ] Aplicación ejecutándose: `ps aux | grep microservice`
- [ ] Endpoint responde: `curl http://TU-IP-EC2:8080/api/products`
- [ ] App Android conecta correctamente
- [ ] Datos se guardan en RDS

---

## 🚨 **SI ALGO SALE MAL:**

### Error de conexión SSH:
```bash
chmod 400 crimewave-key.pem
```

### Error de conexión RDS:
1. Verificar endpoint en application-aws.properties
2. Verificar que RDS esté "Available"
3. Verificar Security Group puerto 5432

### Error de conexión app Android:
1. Verificar IP en ServerConfig.kt
2. Verificar Security Group puerto 8080
3. Probar desde navegador web

**¡Sigue estos pasos uno por uno y tu aplicación estará funcionando en AWS!** 🎉
