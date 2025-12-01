package com.example.crimewavee.ui.theme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.ClothingCategory
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.data.repository.ProductRepository
import android.util.Log
// Imports de crímenes removidos - solo mantenemos funcionalidad de productos

class ClothingViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository(application.applicationContext)
    // CrimeNewsRepository removido

    private val _products = mutableStateOf<List<ClothingItem>>(emptyList())
    val products: State<List<ClothingItem>> = _products

    private val _categories = mutableStateOf<List<ClothingCategory>>(emptyList())
    val categories: State<List<ClothingCategory>> = _categories

    private val _featuredProducts = mutableStateOf<List<ClothingItem>>(emptyList())
    val featuredProducts: State<List<ClothingItem>> = _featuredProducts

    private val _selectedProduct = mutableStateOf<ClothingItem?>(null)
    val selectedProduct: State<ClothingItem?> = _selectedProduct

    private val _currentVideoIndex = mutableStateOf(2)
    val currentVideoIndex: State<Int> = _currentVideoIndex

    private val _cartItems = mutableStateOf<List<ClothingItem>>(emptyList())
    val cartItems: State<List<ClothingItem>> = _cartItems

    // Estados de noticias removidos - solo mantenemos funcionalidad de productos

    init {
        loadProductsFromRepository()
        loadCategories()
        loadFeaturedProducts()
        // Carga de noticias removida
    }

    // Funciones de noticias removidas - solo mantenemos CRUD de productos

    private fun loadProductsFromRepository() {
        viewModelScope.launch {
            try {
                // Usar auto-sincronización para obtener los productos más actualizados
                val products = productRepository.getProductsWithAutoSync()
                _products.value = products
                Log.d("CrimeViewModel", "✅ Productos cargados: ${products.size} items")
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error cargando productos: ${e.message}")
                _products.value = emptyList()
            }
        }
    }

    // Métodos CRUD para productos conectados al microservicio con feedback directo
    fun createProductWithFeedback(clothingItem: ClothingItem, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🆕 INICIANDO CREACIÓN DE PRODUCTO: ${clothingItem.name}")
                Log.d("CrimeViewModel", "📊 Datos - ID: ${clothingItem.id}, Precio: ${clothingItem.price}, Stock: ${clothingItem.stock}")
                Log.d("CrimeViewModel", "🌐 Enviando a API: http://3.21.53.102:8080/api/products")

                // CREAR DIRECTAMENTE EN LA API - SIN FALLBACK LOCAL
                val result = productRepository.createProductInMicroserviceStrict(clothingItem)

                result.fold(
                    onSuccess = { createdProduct ->
                        Log.d("CrimeViewModel", "✅ ¡PRODUCTO REALMENTE CREADO EN SERVIDOR!")
                        Log.d("CrimeViewModel", "📦 Producto: ${createdProduct.name} (ID: ${createdProduct.id})")
                        Log.d("CrimeViewModel", "💾 Guardado en PostgreSQL - Visible en Postman")

                        // Recargar productos desde servidor para mostrar el nuevo
                        loadProductsFromRepository()
                        Log.d("CrimeViewModel", "🔄 Lista actualizada desde servidor")

                        callback(true, "Producto creado exitosamente")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ ¡FALLÓ LA CREACIÓN EN SERVIDOR!")
                        Log.e("CrimeViewModel", "🚫 Error: ${error.message}")
                        Log.e("CrimeViewModel", "⚠️ Producto NO se guardó en base de datos")
                        Log.e("CrimeViewModel", "🔗 Verificar: http://3.21.53.102:8080/api/products")

                        callback(false, error.message ?: "Error desconocido")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ EXCEPCIÓN FATAL EN CREACIÓN: ${e.message}")
                Log.e("CrimeViewModel", "⚠️ Producto NO se creó en servidor")
                e.printStackTrace()

                callback(false, e.message ?: "Excepción fatal")
            }
        }
    }

    fun createProduct(clothingItem: ClothingItem) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🆕 INICIANDO CREACIÓN DE PRODUCTO: ${clothingItem.name}")
                Log.d("CrimeViewModel", "📊 Datos - ID: ${clothingItem.id}, Precio: ${clothingItem.price}, Stock: ${clothingItem.stock}")
                Log.d("CrimeViewModel", "🌐 Enviando a API: http://3.21.53.102:8080/api/products")

                // CREAR DIRECTAMENTE EN LA API - SIN FALLBACK LOCAL
                val result = productRepository.createProductInMicroserviceStrict(clothingItem)

                result.fold(
                    onSuccess = { createdProduct ->
                        Log.d("CrimeViewModel", "✅ ¡PRODUCTO REALMENTE CREADO EN SERVIDOR!")
                        Log.d("CrimeViewModel", "📦 Producto: ${createdProduct.name} (ID: ${createdProduct.id})")
                        Log.d("CrimeViewModel", "💾 Guardado en PostgreSQL - Visible en Postman")

                        // Recargar productos desde servidor para mostrar el nuevo
                        loadProductsFromRepository()
                        Log.d("CrimeViewModel", "🔄 Lista actualizada desde servidor")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ ¡FALLÓ LA CREACIÓN EN SERVIDOR!")
                        Log.e("CrimeViewModel", "🚫 Error: ${error.message}")
                        Log.e("CrimeViewModel", "⚠️ Producto NO se guardó en base de datos")
                        Log.e("CrimeViewModel", "🔗 Verificar: http://3.21.53.102:8080/api/products")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ EXCEPCIÓN FATAL EN CREACIÓN: ${e.message}")
                Log.e("CrimeViewModel", "⚠️ Producto NO se creó en servidor")
                e.printStackTrace()
            }
        }
    }

    fun updateProductInService(clothingItem: ClothingItem) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "✏️ Actualizando producto: ${clothingItem.name}")

                // Primero actualizar localmente para feedback inmediato
                val success = productRepository.updateProduct(clothingItem)
                if (success) {
                    // Actualizar UI inmediatamente
                    loadProductsFromRepository()
                    Log.d("CrimeViewModel", "📱 Producto actualizado localmente: ${clothingItem.name}")
                }

                // Luego sincronizar con servidor
                val result = productRepository.updateProductAndSync(clothingItem)
                result.fold(
                    onSuccess = {
                        Log.d("CrimeViewModel", "✅ Producto actualizado y sincronizado: ${clothingItem.name}")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ Error sincronizando actualización: ${error.message}")
                        // La actualización local ya se hizo, así que aún funciona
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Excepción actualizando producto: ${e.message}")
            }
        }
    }

    fun deleteProductWithFeedback(productId: String, productName: String) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🗑️ Eliminando producto: $productName (ID: $productId)")

                // Primero eliminar localmente para feedback inmediato
                val success = productRepository.deleteProduct(productId)
                if (success) {
                    // Actualizar UI inmediatamente
                    loadProductsFromRepository()
                    Log.d("CrimeViewModel", "📱 Producto eliminado localmente: $productName")
                }

                // Luego sincronizar con servidor
                val result = productRepository.deleteProductAndSync(productId)
                result.fold(
                    onSuccess = {
                        Log.i("CrimeViewModel", "✅ PRODUCTO ELIMINADO Y SINCRONIZADO: $productName")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ Error sincronizando eliminación: ${error.message}")
                        // La eliminación local ya se hizo, así que aún funciona
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Excepción eliminando producto: ${e.message}")
            }
        }
    }

    // === FUNCIONES DE SINCRONIZACIÓN PARA EL ADMIN ===

    /**
     * Sincronización manual para el panel de administración
     */
    fun syncWithServerManually() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔄 Iniciando sincronización manual...")
                val result = productRepository.syncWithServer()
                result.fold(
                    onSuccess = {
                        loadProductsFromRepository() // Recargar productos sincronizados
                        Log.d("CrimeViewModel", "✅ Sincronización manual completada")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ Error en sincronización manual: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Excepción en sincronización manual: ${e.message}")
            }
        }
    }

    /**
     * Verifica si necesita sincronización
     */
    fun needsSync(): Boolean {
        return productRepository.needsSync()
    }



    fun updateStock(productId: String, newStock: Int) {
        viewModelScope.launch {
            try {
                val result = productRepository.updateStockInMicroservice(productId, newStock)
                result.fold(
                    onSuccess = {
                        loadProductsFromRepository() // Recargar lista
                    },
                    onFailure = {
                        // Manejar error
                    }
                )
            } catch (e: Exception) {
                // Manejar excepción
            }
        }
    }

    fun purchaseProduct(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                val result = productRepository.reduceStockInMicroservice(productId, quantity)
                result.fold(
                    onSuccess = { updatedProduct ->
                        if (updatedProduct != null) {
                            loadProductsFromRepository() // Recargar lista
                        } else {
                            // No hay suficiente stock
                        }
                    },
                    onFailure = {
                        // Manejar error
                    }
                )
            } catch (e: Exception) {
                // Manejar excepción
            }
        }
    }

    private fun loadCategories() {
        _categories.value = listOf(
            ClothingCategory(
                id = "cat1",
                name = "CATEGORÍA 1",
                description = "Diseños Especiales",
                imageUrl = "cat1"
            ),
            ClothingCategory(
                id = "cat2",
                name = "CATEGORÍA 2",
                description = "Poleras Premium",
                imageUrl = "cat2"
            ),
            ClothingCategory(
                id = "cat3",
                name = "CATEGORÍA 3",
                description = "Colección Limitada",
                imageUrl = "cat3"
            ),
            ClothingCategory(
                id = "cat4",
                name = "CATEGORÍA 4",
                description = "Cuadros Anime",
                imageUrl = "cat4"
            ),
            ClothingCategory(
                id = "cat5",
                name = "CATEGORÍA 5",
                description = "Anime Collection",
                imageUrl = "cat5"
            ),
            ClothingCategory(
                id = "cat8",
                name = "CATEGORÍA 8",
                description = "Jujutsu Kaisen",
                imageUrl = "cat8"
            )
        )
    }

    private fun loadFeaturedProducts() {
        _featuredProducts.value = _products.value.filter { it.isFeatured }
    }

    fun getProductById(id: String): ClothingItem? {
        return try {
            productRepository.getProductById(id)
        } catch (e: Exception) {
            null
        }
    }

    fun selectProduct(product: ClothingItem) {
        _selectedProduct.value = product
    }

    fun getProductsByCategory(category: ProductType): List<ClothingItem> {
        return productRepository.getProductsByCategory(category)
    }

    fun addToCart(product: ClothingItem) {
        if (product.id.isBlank()) {
            throw IllegalArgumentException("Producto inválido")
        }

        if (product.stock <= 0) {
            throw IllegalArgumentException("No hay stock disponible para este producto")
        }

        if (product.price < 0) {
            throw IllegalArgumentException("Precio inválido")
        }

        _cartItems.value = _cartItems.value + product
    }

    fun removeFromCart(productId: String) {
        if (productId.isBlank()) {
            throw IllegalArgumentException("ID de producto inválido")
        }

        _cartItems.value = _cartItems.value.filter { it.id != productId }
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.price }
    }

    fun navigateVideo(direction: String) {
        val videoCount = 5
        if (direction == "left") {
            _currentVideoIndex.value = (_currentVideoIndex.value - 1 + videoCount) % videoCount
        } else {
            _currentVideoIndex.value = (_currentVideoIndex.value + 1) % videoCount
        }
    }

    fun getNewProducts(): List<ClothingItem> {
        return _products.value.filter { it.isNew }
    }

    fun searchProducts(query: String): List<ClothingItem> {
        return _products.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    fun addProduct(product: ClothingItem) {
        // Sistema de productos es solo visual - no se agregan realmente
        return
    }

    fun removeProduct(productId: String) {
        // Sistema de productos es solo visual - no se eliminan realmente
        return
    }

    fun updateProductLocal(updatedProduct: ClothingItem) {
        // Sistema de productos es solo visual - no se actualizan realmente
        return
    }

    fun generateNextProductId(): String {
        val maxId = _products.value.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
        return (maxId + 1).toString()
    }

    fun getDefaultImageForCategory(category: ProductType): String {
        return when (category) {
            ProductType.POLERAS -> "satorupolera"
            ProductType.POLERONES -> "togahoodie"
            ProductType.CUADROS -> "givencuadro"
        }
    }

    private fun validateProduct(product: ClothingItem) {
        if (product.id.isBlank()) {
            throw IllegalArgumentException("El ID del producto no puede estar vacío")
        }

        if (product.stock < 0) {
            throw IllegalArgumentException("El stock no puede ser negativo. Stock actual: ${product.stock}")
        }

        if (product.price < 0.0) {
            throw IllegalArgumentException("El precio no puede ser negativo. Precio actual: $${String.format("%.0f", product.price)}")
        }

        if (product.price < 15000.0) {
            throw IllegalArgumentException("El precio mínimo debe ser $15,000 CLP. Precio actual: $${String.format("%.0f", product.price)}")
        }

        if (product.name.isBlank()) {
            throw IllegalArgumentException("El nombre del producto no puede estar vacío")
        }

        if (product.name.length > 100) {
            throw IllegalArgumentException("El nombre del producto no puede exceder 100 caracteres")
        }

        if (product.description.isBlank()) {
            throw IllegalArgumentException("La descripción del producto no puede estar vacía")
        }

        if (product.description.length > 500) {
            throw IllegalArgumentException("La descripción del producto no puede exceder 500 caracteres")
        }

        if (product.category == ProductType.CUADROS) {
            if (product.sizes.isEmpty()) {
                throw IllegalArgumentException("Los cuadros deben tener al menos una medida especificada")
            }
            val validMeasures = listOf("30x39", "40x50", "50x70", "70x81")
            product.sizes.forEach { size ->
                if (!validMeasures.contains(size)) {
                    throw IllegalArgumentException("Medida inválida para cuadros: $size. Medidas válidas: ${validMeasures.joinToString(", ")}")
                }
            }
        } else {
            val validSizes = listOf("XS", "S", "M", "L", "XL", "XXL")
            product.sizes.forEach { size ->
                if (!validSizes.contains(size)) {
                    throw IllegalArgumentException("Talla inválida: $size. Tallas válidas: ${validSizes.joinToString(", ")}")
                }
            }
        }

        if (product.imageUrl.isNotBlank() && product.imageUrl != "default_product") {
            val invalidChars = listOf("<", ">", "\"", "'", "&")
            invalidChars.forEach { char ->
                if (product.imageUrl.contains(char)) {
                    throw IllegalArgumentException("La URL de la imagen contiene caracteres inválidos")
                }
            }
        }
    }

    /**
     * FUNCIONES DE DIAGNÓSTICO Y SOLUCIÓN PARA EL CUADRO GOJO
     */

    /**
     * Diagnóstica inconsistencias de datos y reporta el estado
     */
    fun diagnosticDataConsistency() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔍 Iniciando diagnóstico de consistencia...")

                // Obtener productos locales
                val localProducts = _products.value
                Log.i("CrimeViewModel", "📊 Productos locales: ${localProducts.size}")

                // Obtener productos del servidor
                val serverResult = productRepository.getAllProductsFromMicroservice()
                serverResult.fold(
                    onSuccess = { serverProducts ->
                        Log.i("CrimeViewModel", "📊 Productos en servidor: ${serverProducts.size}")
                        Log.i("CrimeViewModel", "📊 DIAGNÓSTICO COMPLETADO")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ Error conectando al servidor: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Excepción en diagnóstico: ${e.message}")
            }
        }
    }

    /**
     * Elimina específicamente el Cuadro Gojo problemático
     */
    fun deleteCuadroGojoForced() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🎯 Eliminando Cuadro Gojo forzadamente...")

                // Buscar producto "Cuadro Gojo" en la lista actual
                val cuadroGojo = _products.value.find {
                    it.name.contains("Gojo", ignoreCase = true) && it.category == ProductType.CUADROS
                }

                if (cuadroGojo != null) {
                    deleteProductWithFeedback(cuadroGojo.id, cuadroGojo.name)
                    Log.d("CrimeViewModel", "✅ Cuadro Gojo encontrado y eliminado: ${cuadroGojo.name}")
                } else {
                    Log.w("CrimeViewModel", "⚠️ No se encontró Cuadro Gojo para eliminar")
                }
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Excepción eliminando Cuadro Gojo: ${e.message}")
            }
        }
    }

    /**
     * Fuerza una resincronización completa limpiando todo el cache local
     */
    fun forceCompleteResync() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔄 Forzando resincronización completa...")

                // Usar la función de sincronización existente
                val result = productRepository.syncWithServer()
                result.fold(
                    onSuccess = {
                        Log.d("CrimeViewModel", "✅ Resincronización completa exitosa")
                        loadProductsFromRepository() // Recargar productos frescos
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ Error en resincronización completa: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Excepción en resincronización completa: ${e.message}")
            }
        }
    }

    /**
     * Función combinada para solucionar el problema del Cuadro Gojo
     */
    fun solveCuadroGojoProblem() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔧 SOLUCIONANDO PROBLEMA DEL CUADRO GOJO...")

                // 1. Diagnóstico inicial
                diagnosticDataConsistency()

                // 2. Eliminación forzada del Cuadro Gojo
                deleteCuadroGojoForced()

                // 3. Resincronización completa
                forceCompleteResync()

                Log.d("CrimeViewModel", "✅ PROBLEMA DEL CUADRO GOJO SOLUCIONADO")
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error solucionando problema Cuadro Gojo: ${e.message}")
            }
        }
    }

    /**
     * Fuerza la recarga de productos desde el servidor
     */
    fun refreshProducts() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔄 Recargando productos...")
                val products = productRepository.getProductsWithAutoSync()
                _products.value = products
                Log.d("CrimeViewModel", "✅ Productos recargados: ${products.size} items")
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error recargando productos: ${e.message}")
            }
        }
    }

    /**
     * Verifica conectividad con el servidor con callback para feedback directo
     */
    fun testServerConnectionWithFeedback(callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🧪 === PROBANDO CONEXIÓN AL SERVIDOR ===")
                Log.d("CrimeViewModel", "🔗 URL de prueba: http://3.21.53.102:8080/api/products")

                val result = productRepository.getAllProductsFromMicroservice()
                result.fold(
                    onSuccess = { products ->
                        Log.d("CrimeViewModel", "✅ ¡SERVIDOR FUNCIONANDO CORRECTAMENTE!")
                        Log.d("CrimeViewModel", "📊 ${products.size} productos disponibles en la API")
                        callback(true, "${products.size} productos encontrados")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ SERVIDOR NO DISPONIBLE")
                        Log.e("CrimeViewModel", "🚫 Error: ${error.message}")
                        callback(false, "Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ EXCEPCIÓN PROBANDO CONEXIÓN")
                Log.e("CrimeViewModel", "🚫 Excepción: ${e.message}")
                callback(false, "Excepción: ${e.message}")
            }
        }
    }

    /**
     * Verifica conectividad con el servidor
     */
    fun testServerConnection() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🧪 === PROBANDO CONEXIÓN AL SERVIDOR ===")
                Log.d("CrimeViewModel", "🔗 URL de prueba: http://3.21.53.102:8080/api/products")

                val result = productRepository.getAllProductsFromMicroservice()
                result.fold(
                    onSuccess = { products ->
                        Log.d("CrimeViewModel", "✅ ¡SERVIDOR FUNCIONANDO CORRECTAMENTE!")
                        Log.d("CrimeViewModel", "📊 ${products.size} productos disponibles en la API")
                        Log.d("CrimeViewModel", "🌐 Conexión exitosa con http://3.21.53.102:8080/api/products")

                        // Mostrar lista de productos para debugging
                        products.forEachIndexed { index, product ->
                            Log.d("CrimeViewModel", "📦 Producto ${index + 1}: ${product.name} (ID: ${product.id})")
                        }
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ SERVIDOR NO DISPONIBLE")
                        Log.e("CrimeViewModel", "🚫 Error: ${error.message}")
                        Log.e("CrimeViewModel", "🔗 Revisar manualmente: http://3.21.53.102:8080/api/products")
                        Log.e("CrimeViewModel", "💡 Posibles causas:")
                        Log.e("CrimeViewModel", "   - Servidor EC2 apagado")
                        Log.e("CrimeViewModel", "   - Problemas de red")
                        Log.e("CrimeViewModel", "   - IP incorrecta")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ EXCEPCIÓN PROBANDO CONEXIÓN")
                Log.e("CrimeViewModel", "🚫 Excepción: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Verifica conectividad con el servidor y prueba las funciones de API
     */
    fun testApiIntegration() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🧪 INICIANDO PRUEBAS DE INTEGRACIÓN API")

                // Probar conectividad básica obteniendo productos
                Log.d("CrimeViewModel", "🌐 Probando conectividad con el servidor...")

                // Probar obtener productos
                val products = productRepository.getAllProductsFromMicroservice()
                products.fold(
                    onSuccess = { productList ->
                        Log.d("CrimeViewModel", "✅ API funcionando - ${productList.size} productos obtenidos")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ Error obteniendo productos: ${error.message}")
                    }
                )

            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error en pruebas de API: ${e.message}")
            }
        }
    }

    /**
     * Función de prueba para crear un producto de ejemplo
     */
    fun testCreateProduct() {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🧪 PRUEBA: Creando producto de ejemplo")

                val testProduct = ClothingItem(
                    id = "test_${System.currentTimeMillis()}",
                    name = "Producto de Prueba API",
                    description = "Producto creado para probar la integración con la API",
                    price = 25000.0,
                    imageUrl = "https://via.placeholder.com/300x300.jpg",
                    category = ProductType.POLERAS,
                    isNew = true,
                    isFeatured = false,
                    sizes = listOf("S", "M", "L"),
                    stock = 5
                )

                createProduct(testProduct)
                Log.d("CrimeViewModel", "✅ Producto de prueba creado exitosamente")

            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error creando producto de prueba: ${e.message}")
            }
        }
    }

    /**
     * Ejecuta un diagnóstico completo del sistema de creación de productos
     */
    fun runAdvancedDiagnostic(callback: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔍 === DIAGNÓSTICO COMPLETO INICIADO ===")

                // PASO 1: Verificar conectividad básica
                Log.d("CrimeViewModel", "🔍 PASO 1: Probando conectividad básica...")
                val connectivityResult = productRepository.getAllProductsFromMicroservice()

                connectivityResult.fold(
                    onSuccess = { products ->
                        Log.d("CrimeViewModel", "✅ PASO 1: Conectividad OK - ${products.size} productos")

                        // PASO 2: Probar creación con datos mínimos
                        Log.d("CrimeViewModel", "🔍 PASO 2: Probando creación de producto...")

                        val diagnosticProduct = ClothingItem(
                            id = "diagnostic_${System.currentTimeMillis()}",
                            name = "DIAGNÓSTICO TEST",
                            description = "Producto creado para diagnóstico del sistema",
                            price = 15000.0,
                            imageUrl = "test-image",
                            category = ProductType.POLERAS,
                            isNew = true,
                            isFeatured = false,
                            sizes = listOf("S"),
                            stock = 1
                        )

                        val createResult = productRepository.createProductInMicroserviceStrict(diagnosticProduct)
                        createResult.fold(
                            onSuccess = { createdProduct ->
                                Log.d("CrimeViewModel", "✅ PASO 2: Producto creado exitosamente")
                                callback("✅ DIAGNÓSTICO EXITOSO: Servidor funciona correctamente. Producto creado con ID: ${createdProduct.id}")

                                // Opcional: Limpiar el producto de prueba
                                try {
                                    productRepository.deleteProductInMicroservice(createdProduct.id)
                                    Log.d("CrimeViewModel", "🗑️ Producto de diagnóstico eliminado")
                                } catch (e: Exception) {
                                    Log.w("CrimeViewModel", "⚠️ No se pudo eliminar producto de diagnóstico: ${e.message}")
                                }
                            },
                            onFailure = { error ->
                                Log.e("CrimeViewModel", "❌ PASO 2: Falló la creación")
                                Log.e("CrimeViewModel", "🔍 ERROR COMPLETO: ${error.message}")
                                Log.e("CrimeViewModel", "📱 TIPO DE ERROR: ${error::class.simpleName}")

                                // Crear mensaje detallado y legible
                                val errorDetails = when {
                                    error.message?.contains("400") == true -> "Error HTTP 400: Datos inválidos enviados al servidor"
                                    error.message?.contains("404") == true -> "Error HTTP 404: Endpoint no encontrado"
                                    error.message?.contains("500") == true -> "Error HTTP 500: Error interno del servidor"
                                    error.message?.contains("Connection") == true -> "Error de conexión: No se pudo conectar"
                                    error.message?.contains("timeout") == true -> "Error de timeout: Servidor muy lento"
                                    else -> "Error desconocido: ${error.message ?: "Sin detalles"}"
                                }

                                callback("❌ FALLA AL CREAR: $errorDetails")
                            }
                        )
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ PASO 1: Falló la conectividad")
                        callback("❌ PROBLEMA DE CONECTIVIDAD: No se puede conectar al servidor. Error: ${error.message}")
                    }
                )

            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error en diagnóstico: ${e.message}")
                callback("❌ ERROR EN DIAGNÓSTICO: ${e.message}")
            }
        }
    }

    /**
     * Ejecuta diagnóstico con diálogo detallado para errores largos
     */
    fun runAdvancedDiagnosticWithDialog(
        onSuccess: (String) -> Unit,
        onError: (String, String) -> Unit // título, mensaje detallado
    ) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🔍 === DIAGNÓSTICO COMPLETO CON DIÁLOGO ===")

                // PASO 1: Verificar conectividad básica
                Log.d("CrimeViewModel", "🔍 PASO 1: Probando conectividad básica...")
                val connectivityResult = productRepository.getAllProductsFromMicroservice()

                connectivityResult.fold(
                    onSuccess = { products ->
                        Log.d("CrimeViewModel", "✅ PASO 1: Conectividad OK - ${products.size} productos")

                        // PASO 2: Probar creación con datos mínimos
                        Log.d("CrimeViewModel", "🔍 PASO 2: Probando creación de producto...")

                        val diagnosticProduct = ClothingItem(
                            id = "diagnostic_${System.currentTimeMillis()}",
                            name = "DIAGNÓSTICO TEST",
                            description = "Producto creado para diagnóstico del sistema",
                            price = 15000.0,
                            imageUrl = "test-image",
                            category = ProductType.POLERAS,
                            isNew = true,
                            isFeatured = false,
                            sizes = listOf("S"),
                            stock = 1
                        )

                        val createResult = productRepository.createProductInMicroserviceStrict(diagnosticProduct)
                        createResult.fold(
                            onSuccess = { createdProduct ->
                                Log.d("CrimeViewModel", "✅ PASO 2: Producto creado exitosamente")
                                onSuccess("✅ DIAGNÓSTICO EXITOSO\n\nServidor funciona correctamente.\nProducto creado con ID: ${createdProduct.id}")

                                // Limpiar el producto de prueba
                                try {
                                    productRepository.deleteProductInMicroservice(createdProduct.id)
                                    Log.d("CrimeViewModel", "🗑️ Producto de diagnóstico eliminado")
                                } catch (e: Exception) {
                                    Log.w("CrimeViewModel", "⚠️ No se pudo eliminar producto de diagnóstico: ${e.message}")
                                }
                            },
                            onFailure = { error ->
                                Log.e("CrimeViewModel", "❌ PASO 2: Falló la creación")
                                Log.e("CrimeViewModel", "🔍 ERROR COMPLETO: ${error.message}")
                                Log.e("CrimeViewModel", "📱 STACK TRACE:")
                                error.printStackTrace()

                                // Crear mensaje detallado
                                val titulo = "❌ ERROR AL CREAR PRODUCTO"
                                val mensaje = buildString {
                                    append("🔍 DIAGNÓSTICO DETALLADO:\n\n")
                                    append("✅ Paso 1: Conexión al servidor OK\n")
                                    append("❌ Paso 2: Falla al crear producto\n\n")
                                    append("📋 DETALLES DEL ERROR:\n")
                                    append("Tipo: ${error::class.simpleName}\n")
                                    append("Mensaje: ${error.message ?: "Sin mensaje específico"}\n\n")

                                    when {
                                        error.message?.contains("400") == true -> {
                                            append("🚫 ERROR HTTP 400 - BAD REQUEST\n")
                                            append("Causa probable: Datos del producto inválidos\n")
                                            append("Solución: Verificar formato de datos enviados")
                                        }
                                        error.message?.contains("404") == true -> {
                                            append("🚫 ERROR HTTP 404 - NOT FOUND\n")
                                            append("Causa probable: Endpoint incorrecto\n")
                                            append("Solución: Verificar URL del API")
                                        }
                                        error.message?.contains("500") == true -> {
                                            append("🚫 ERROR HTTP 500 - INTERNAL SERVER ERROR\n")
                                            append("Causa probable: Error interno del servidor\n")
                                            append("Solución: Contactar administrador del servidor")
                                        }
                                        error.message?.contains("Connection") == true -> {
                                            append("🚫 ERROR DE CONEXIÓN\n")
                                            append("Causa probable: Red o servidor no disponible\n")
                                            append("Solución: Verificar conexión a internet")
                                        }
                                        else -> {
                                            append("🚫 ERROR DESCONOCIDO\n")
                                            append("Revisar logs completos para más información")
                                        }
                                    }
                                }

                                onError(titulo, mensaje)
                            }
                        )
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ PASO 1: Falló la conectividad")
                        val titulo = "❌ ERROR DE CONECTIVIDAD"
                        val mensaje = buildString {
                            append("🔍 DIAGNÓSTICO:\n\n")
                            append("❌ No se pudo conectar al servidor\n\n")
                            append("📋 DETALLES:\n")
                            append("Error: ${error.message}\n\n")
                            append("🔧 POSIBLES SOLUCIONES:\n")
                            append("• Verificar que el servidor esté encendido\n")
                            append("• Verificar conexión a internet\n")
                            append("• Probar la URL en navegador:\n")
                            append("  http://3.21.53.102:8080/api/products")
                        }
                        onError(titulo, mensaje)
                    }
                )

            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error en diagnóstico: ${e.message}")
                val titulo = "❌ ERROR EN DIAGNÓSTICO"
                val mensaje = "Error inesperado durante el diagnóstico:\n\n${e.message}\n\nRevisar logs para más detalles."
                onError(titulo, mensaje)
            }
        }
    }

    /**
     * Prueba creación con datos mínimos para debugging
     */
    fun testMinimalCreation(callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("CrimeViewModel", "🧪 === PROBANDO CREACIÓN CON DATOS MÍNIMOS ===")

                val result = productRepository.testMinimalProductCreation()
                result.fold(
                    onSuccess = { product ->
                        Log.d("CrimeViewModel", "✅ DATOS MÍNIMOS FUNCIONARON!")
                        callback(true, "Producto mínimo creado: ${product.name}")
                    },
                    onFailure = { error ->
                        Log.e("CrimeViewModel", "❌ DATOS MÍNIMOS FALLARON: ${error.message}")
                        callback(false, error.message ?: "Error desconocido")
                    }
                )
            } catch (e: Exception) {
                Log.e("CrimeViewModel", "❌ Error en prueba mínima: ${e.message}")
                callback(false, e.message ?: "Excepción")
            }
        }
    }
}
