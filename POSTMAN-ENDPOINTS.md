# Endpoints para Postman - CrimeWave API

**URL Base**: `http://3.21.53.102:8080`

## 1. Inicializar Datos de Prueba (EJECUTAR PRIMERO)
- **Método**: `POST`
- **URL**: `http://3.21.53.102:8080/api/products/init-sample-data`
- **Headers**: `Content-Type: application/json`
- **Respuesta esperada**: `{"message": "Datos de prueba inicializados exitosamente"}`

## 2. Obtener Todos los Productos
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products`

## 3. Obtener Producto por ID
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products/1`

## 4. Obtener Productos por Categoría
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products/category/POLERAS`
- **Categorías disponibles**: `POLERAS`, `POLERONES`, `CUADROS`

## 5. Obtener Productos Destacados
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products/featured`

## 6. Obtener Productos Nuevos
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products/new`

## 7. Obtener Productos en Stock
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products/in-stock`

## 8. Buscar Productos por Nombre
- **Método**: `GET`
- **URL**: `http://3.21.53.102:8080/api/products/search?name=Gojo`

## 9. Crear Nuevo Producto
- **Método**: `POST`
- **URL**: `http://3.21.53.102:8080/api/products`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
    "id": "5",
    "name": "Producto de Prueba",
    "description": "Descripción del producto",
    "price": 25000.0,
    "imageUrl": "imagen-test",
    "category": "POLERAS",
    "isNew": true,
    "isFeatured": false,
    "stock": 10
}
```

## 10. Actualizar Producto
- **Método**: `PUT`
- **URL**: `http://3.21.53.102:8080/api/products/1`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
    "name": "Polera Satoru Gojo - Actualizada",
    "price": 24000.0,
    "stock": 45
}
```

## 11. Actualizar Solo Stock
- **Método**: `PUT`
- **URL**: `http://3.21.53.102:8080/api/products/1/stock`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
    "stock": 25
}
```

## 12. Reducir Stock (Simular Compra)
- **Método**: `POST`
- **URL**: `http://3.21.53.102:8080/api/products/1/reduce-stock/2`

## 13. Eliminar Producto
- **Método**: `DELETE`
- **URL**: `http://3.21.53.102:8080/api/products/5`

---

## Colección de Postman

Puedes importar esta configuración en Postman creando una nueva colección y agregando estos endpoints.

## Variables de Entorno Recomendadas

Crear en Postman:
- `base_url`: `http://3.21.53.102:8080`
- Usar: `{{base_url}}/api/products`

## Datos de Prueba Precargados

Después de ejecutar `/init-sample-data`, tendrás:

1. **Polera Satoru Gojo** (ID: 1)
   - Precio: $22,000
   - Categoría: POLERAS
   - Stock: 50

2. **Polerón Toga Himiko** (ID: 2)
   - Precio: $42,000  
   - Categoría: POLERONES
   - Stock: 30
   - Destacado: Sí

3. **Cuadro Given** (ID: 3)
   - Precio: $45,000
   - Categoría: CUADROS
   - Stock: 15

4. **Cuadro Peneo** (ID: 4)
   - Precio: $35,000
   - Categoría: CUADROS
   - Stock: 20
   - Destacado: Sí
