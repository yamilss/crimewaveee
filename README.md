# CrimeWave - Tienda de Ropa Online

## 📱 Aplicación Móvil Android con Microservicio Spring Boot en AWS

### 🎯 **Descripción del Proyecto**

CrimeWave es una aplicación móvil completa para una tienda de ropa online que incluye:
- **Frontend**: App Android nativa con Jetpack Compose
- **Backend**: Microservicio Spring Boot desplegado en AWS EC2
- **Base de datos**: PostgreSQL en AWS RDS
- **Arquitectura**: Microservicios en la nube con escalabilidad

---

## 🏗️ **Arquitectura del Sistema**

```
📱 App Android (Kotlin + Jetpack Compose)
    ↓ HTTP/REST API
🌐 AWS EC2 (Spring Boot Microservicio)
    ↓ JDBC
🗄️ AWS RDS (PostgreSQL)
```

### **Componentes principales:**
- **App móvil**: Gestión de productos, categorías, carrito de compras
- **Microservicio**: API REST con CRUD completo de productos
- **Base de datos**: Almacenamiento persistente en la nube
- **AWS**: Infraestructura escalable y confiable

---

## 🚀 **Funcionalidades**

### **App Android:**
- ✅ Catálogo de productos de ropa
- ✅ Navegación por categorías (Poleras, Polerones, Cuadros)
- ✅ Productos destacados y nuevos
- ✅ Carrito de compras
- ✅ Gestión de stock en tiempo real
- ✅ Interfaz moderna con Jetpack Compose
- ✅ Conexión automática con AWS

### **Microservicio Spring Boot:**
- ✅ CRUD completo de productos
- ✅ Endpoints REST documentados
- ✅ Validación de datos
- ✅ Manejo de stock
- ✅ Health checks
- ✅ Configuración para AWS RDS
- ✅ CORS habilitado para app móvil

### **Base de Datos:**
- ✅ Esquema optimizado para productos de ropa
- ✅ Índices para búsquedas eficientes
- ✅ Respaldos automáticos en AWS
- ✅ Escalabilidad horizontal

---

## 🛠️ **Tecnologías Utilizadas**

### **Frontend (Android):**
- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arquitectura**: MVVM + Repository Pattern
- **Networking**: Retrofit + OkHttp
- **Dependency Injection**: Hilt/Dagger
- **Navigation**: Navigation Compose

### **Backend (Microservicio):**
- **Framework**: Spring Boot 2.7.14
- **Lenguaje**: Kotlin
- **Base de datos**: PostgreSQL
- **ORM**: Spring Data JPA
- **Validación**: Bean Validation
- **Documentación**: Swagger/OpenAPI

### **Infraestructura (AWS):**
- **Compute**: EC2 t3.micro (Free Tier)
- **Database**: RDS Aurora PostgreSQL (Free Tier)  
- **Network**: VPC, Security Groups
- **Monitoreo**: CloudWatch

---

## 📦 **Instalación y Despliegue**

### **Prerrequisitos:**
- Android Studio
- Java JDK 11+
- Cuenta AWS (Free Tier)
- Git

### **Clonar el repositorio:**
```bash
git clone [URL_DEL_REPOSITORIO]
cd crimewavee
```

### **Despliegue automático:**
```bash
# Desde Windows PowerShell
C:\Users\sekai\Downloads\finish-everything.bat
```

Este script automatiza:
1. ✅ Compilación del microservicio
2. ✅ Despliegue en AWS EC2
3. ✅ Configuración de base de datos
4. ✅ Generación del APK Android
5. ✅ Verificación de funcionamiento

---

## 🌐 **URLs de Producción**

### **Microservicio en AWS:**
- **Health Check**: http://3.15.178.116:8080/actuator/health
- **API Productos**: http://3.15.178.116:8080/api/products
- **Documentación**: http://3.15.178.116:8080/swagger-ui.html

### **Endpoints principales:**
```
GET    /api/products              # Listar todos los productos
GET    /api/products/{id}         # Obtener producto por ID
GET    /api/products/category/{cat} # Productos por categoría
GET    /api/products/featured     # Productos destacados
GET    /api/products/new         # Productos nuevos
POST   /api/products             # Crear producto
PUT    /api/products/{id}        # Actualizar producto
DELETE /api/products/{id}        # Eliminar producto
POST   /api/products/init-sample-data # Inicializar datos de prueba
```

---

## 📱 **Instalación de la App**

### **APK Generado:**
- **Ubicación**: `app/build/outputs/apk/debug/app-debug.apk`
- **Tamaño**: ~41MB
- **Configuración**: Conecta automáticamente a AWS

### **Instalar en dispositivo Android:**
1. Habilitar "Fuentes desconocidas" en Configuración
2. Transferir APK al dispositivo
3. Ejecutar APK desde explorador de archivos
4. La app conectará automáticamente con el microservicio en AWS

### **Instalar con ADB:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 **Pruebas**

### **Verificar microservicio:**
```bash
# Health check
curl http://3.15.178.116:8080/actuator/health

# Listar productos
curl http://3.15.178.116:8080/api/products

# Crear producto de prueba
curl -X POST http://3.15.178.116:8080/api/products/init-sample-data
```

### **Probar la app:**
1. Instalar APK en dispositivo Android
2. Abrir "CrimeWave" 
3. Verificar que carga productos desde AWS
4. Navegar por categorías
5. Agregar productos al carrito

---

## 📊 **Datos de Prueba**

La aplicación incluye productos de ejemplo:
- **Polera Satoru Gojo** - $22.000 (Anime Jujutsu Kaisen)
- **Polerón Toga Himiko** - $42.000 (Anime My Hero Academia)  
- **Cuadro Given** - $45.000 (Anime Given)
- **Cuadro Gojo** - $35.000 (Anime Jujutsu Kaisen)

---

## 🔧 **Configuración**

### **Variables de entorno (AWS):**
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://crimewave-db.cticyu8qgoa0.us-east-2.rds.amazonaws.com:5432/crimewave_products
spring.datasource.username=postgres
spring.datasource.password=CrimeWave2024!

# Servidor
server.port=8080

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### **Configuración app Android:**
```kotlin
// ServerConfig.kt
const val BASE_URL = "http://3.15.178.116:8080/"
```

---

## 📚 **Documentación Técnica**

### **Estructura del proyecto:**
```
crimewavee/
├── app/                    # Aplicación Android
│   ├── src/main/java/     # Código Kotlin
│   ├── build.gradle.kts   # Configuración Gradle
│   └── ...
├── microservice/          # Spring Boot Microservicio
│   ├── src/main/kotlin/   # Código del backend
│   ├── build.gradle.kts   # Configuración Gradle
│   └── ...
├── keystore/              # Certificados de firma
└── gradle/                # Configuración Gradle global
```

### **Patrones de diseño utilizados:**
- **MVVM**: En la app Android
- **Repository Pattern**: Para acceso a datos
- **Dependency Injection**: Para desacoplamiento
- **RESTful API**: Para comunicación cliente-servidor

---

## 🏆 **Cumplimiento de Requisitos Académicos**

### ✅ **Desarrollo de Aplicación Móvil:**
- App Android nativa con Kotlin
- Interfaz visual completa y funcional
- Navegación sin errores
- Formularios con validación

### ✅ **Microservicios:**
- Spring Boot correctamente configurado
- Base de datos activa (AWS RDS)
- Endpoints funcionales
- Operaciones CRUD completas

### ✅ **Integración:**
- App móvil integrada con microservicio
- Envío, recepción y actualización de datos en tiempo real
- Flujo CRUD desde la interfaz

### ✅ **API Externa:**
- Consumo vía Retrofit
- Integración en la interfaz
- Sin interferencia con datos locales

### ✅ **APK Firmado:**
- APK generado correctamente
- Configuración técnica incluida (build.gradle, keystore)
- Funcional en dispositivos Android

### ✅ **Herramientas Colaborativas:**
- Repositorio GitHub con commits técnicos
- Planificación visible
- Trabajo colaborativo evidenciado

---

## 📞 **Soporte**

Para problemas o consultas:
1. Verificar logs del microservicio: `ssh ubuntu@3.15.178.116 "tail -f logs/spring-final.log"`
2. Verificar conectividad: `curl http://3.15.178.116:8080/actuator/health`
3. Reinstalar APK si hay problemas de conexión

---

## 📄 **Licencia**

Este proyecto es desarrollado con fines académicos para la asignatura DSY1105 - Desarrollo de Aplicaciones Móviles.

---

## 🎉 **Estado del Proyecto**

**✅ COMPLETADO Y FUNCIONAL**

- ✅ App Android desplegada y funcional
- ✅ Microservicio ejecutándose en AWS EC2  
- ✅ Base de datos PostgreSQL en AWS RDS
- ✅ APK generado y listo para instalación
- ✅ Toda la arquitectura en la nube funcionando
- ✅ Listo para evaluación académica

**Última actualización**: 24 de Noviembre, 2025
