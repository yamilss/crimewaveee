# CRUD DE PRODUCTOS - EXPLICACIÓN COMPLETA

## ¿Qué hace exactamente el CRUD de productos?

### 🛍️ **FUNCIONALIDADES DEL CRUD:**

#### **1. CREAR (Create) - Agregar nuevos productos**
- ✅ **Agregar productos** con toda la información:
  - Nombre del producto (ej: "Polera Gojo")
  - Descripción detallada
  - Precio en pesos chilenos
  - Categoría (POLERAS, POLERONES, CUADROS, etc.)
  - URL de imagen
  - Stock disponible
  - Marcadores (Nuevo, Destacado)

#### **2. LEER (Read) - Ver y filtrar productos**
- ✅ **Ver todos los productos** de la tienda
- ✅ **Filtrar por categoría** (solo poleras, solo cuadros, etc.)
- ✅ **Ver productos destacados** (aparecen en home)
- ✅ **Ver productos nuevos** (con etiqueta "NUEVO")
- ✅ **Ver productos en stock** (que tienen cantidad disponible)
- ✅ **Buscar producto específico** por ID

#### **3. ACTUALIZAR (Update) - Modificar productos existentes**
- ✅ **Editar información** del producto (nombre, precio, descripción)
- ✅ **Actualizar stock** cuando se venden productos
- ✅ **Cambiar estado** (destacado/no destacado, nuevo/no nuevo)
- ✅ **Modificar precio** para ofertas o cambios
- ✅ **Reducir stock automáticamente** cuando alguien compra

#### **4. ELIMINAR (Delete) - Quitar productos**
- ✅ **Eliminar productos** que ya no se venden
- ✅ **Confirmación antes de eliminar** para evitar errores

---

## 📍 **¿DÓNDE ESTÁ LA BASE DE DATOS?**

### **TIENES 2 OPCIONES DE ALMACENAMIENTO:**

#### **OPCIÓN 1: Base de Datos Local (SharedPreferences)**
📂 **Ubicación**: Almacenado internamente en el dispositivo Android
- **Archivo**: `ProductRepository.kt` (líneas 20-30)
- **Tipo**: SharedPreferences de Android
- **Datos**: Se guardan en el almacenamiento interno del celular
- **Ventaja**: Funciona sin internet, datos persisten
- **Desventaja**: Solo en ese dispositivo

#### **OPCIÓN 2: Base de Datos del Microservicio (H2 Database)**
🖥️ **Ubicación**: Microservicio Spring Boot en `localhost:8080`
- **Archivo**: `ClothingItemController.kt` (microservicio)
- **Base de datos**: H2 Database (en memoria)
- **Tabla**: `clothing_items` 
- **Ventaja**: Datos centralizados, múltiples dispositivos
- **Desventaja**: Necesita conexión al servidor

---

## 🔧 **CÓMO FUNCIONA ACTUALMENTE:**

### **Configuración Actual:**
```kotlin
// En ProductRepository.kt línea 28:
private const val USE_MICROSERVICE = false  // ← ACTUALMENTE USA LOCAL
```

### **Si USE_MICROSERVICE = false (ACTUAL):**
- ✅ Productos se guardan en **SharedPreferences** del celular
- ✅ Funciona **sin internet**
- ✅ 4 productos **predefinidos** (Polera Gojo, Polerón Toga, etc.)
- ✅ **Datos persisten** entre aperturas de la app

### **Si USE_MICROSERVICE = true:**
- 🌐 Productos se guardan en **base de datos H2** del servidor
- 🌐 Necesita **microservicio corriendo** en `localhost:8080`
- 🌐 Datos **centralizados** y **compartidos**

---

## 📱 **PANTALLAS DONDE SE USA:**

1. **HomeScreen** - Muestra productos destacados
2. **ProductManagementScreen** - Panel CRUD completo para administradores
3. **DetailsScreen** - Vista detallada de cada producto
4. **CartScreen** - Carrito de compras (reduce stock automáticamente)
5. **EmployeePanelScreen** - Panel de empleados con estadísticas

---

## 🗃️ **ESTRUCTURA DE DATOS DE UN PRODUCTO:**

```kotlin
ClothingItem(
    id = "1",                           // ID único
    name = "Polera Satoru Gojo",        // Nombre del producto
    description = "Diseño original...", // Descripción
    price = 22000.0,                    // Precio en CLP
    imageUrl = "satorupolera",          // URL/nombre de imagen
    category = ProductType.POLERAS,     // Categoría
    isNew = true,                       // ¿Es nuevo?
    isFeatured = false,                 // ¿Es destacado?
    sizes = ["S", "M", "L", "XL"],     // Tallas disponibles
    stock = 50                          // Cantidad en inventario
)
```

---

## 🎯 **EJEMPLO DE USO REAL:**

### **Caso: Tienda de Ropa Online**
1. **Administrador** usa `ProductManagementScreen` para:
   - Agregar nueva polera de anime
   - Actualizar precios para Black Friday
   - Eliminar productos descontinuados
   
2. **Cliente** en la app ve:
   - Productos en `HomeScreen`
   - Puede comprar (stock se reduce automáticamente)
   - Solo ve productos con stock > 0

3. **Empleado** usa `EmployeePanelScreen` para:
   - Ver estadísticas de ventas
   - Actualizar stock rápidamente
   - Gestionar inventario

¿Te gustaría que active el microservicio (base de datos H2) o prefieres seguir con el almacenamiento local?
