# ✅ CHECKLIST: DESPLEGAR A AWS EC2

## 🎯 **TU SIGUIENTE PASO:**

### **OPCIÓN 1: Ya tienes EC2 creada**
```bash
deploy-complete-to-aws.bat TU-IP-EC2 ruta\a\crimewave-key.pem
```

### **OPCIÓN 2: No tienes EC2 aún**
Sigue los pasos de abajo ⬇️

---

## 🖥️ **CREAR EC2 (10 minutos):**

### **1. Ve a AWS Console**
- [aws.amazon.com](https://aws.amazon.com) → EC2 → Launch Instance

### **2. Configuración rápida:**
- **Name**: `crimewave-server`
- **OS**: Ubuntu Server 22.04 LTS  
- **Type**: `t3.micro` (Free tier)
- **Key**: Create new `crimewave-key.pem` ⬇️ DESCARGAR
- **Security**: Allow SSH(22), HTTP(80), TCP(8080)
- **Storage**: 8GB (Free tier)

### **3. Anotar IP pública:**
✅ **Tu IP**: `3.15.178.116` (ya configurada)

---

## 🚀 **DESPLEGAR AHORA (5 minutos):**

### **Comando para tu EC2:**
```bash
deploy-to-my-ec2.bat ruta\a\crimewave-key.pem
```

**Ejemplo:**
```bash
deploy-to-my-ec2.bat C:\Users\sekai\Downloads\crimewave-key.pem
```

---

## 📱 **CONECTAR APP (2 minutos):**

### **1. Editar ServerConfig.kt:**
```kotlin
private const val AWS_EC2 = "http://TU-IP-EC2:8080/"
```

### **2. Compilar app:**
```bash
.\gradlew :app:assembleDebug
```

---

## ✅ **VERIFICAR QUE FUNCIONA:**

### **En navegador:**
- Ve a: `http://3.15.178.116:8080/api/products`
- Deberías ver productos JSON

### **En app Android:**
- Abre la app
- Ve al catálogo de productos
- Deberían cargar desde AWS

---

## 🎉 **RESULTADO FINAL:**
- 🖥️ **Spring Boot** en EC2 (Free Tier)
- 🗄️ **PostgreSQL** en RDS Aurora 
- 📱 **App Android** conectada a AWS
- 🛍️ **Tienda de ropa** 100% en la nube

---

## 💡 **¿TIENES DUDAS?**
- 📖 Guía completa: `AWS_EC2_RDS_COMPLETE_GUIDE.md`
- 🔧 Troubleshooting: `RDS_FREE_TIER_FIX.md`
- 📋 Pasos detallados: `AWS_DEPLOYMENT_STEPS.md`
