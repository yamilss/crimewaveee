package com.example.crimewavee.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.data.api.ClothingItemApiService
import com.example.crimewavee.data.api.ClothingItemResponse
import com.example.crimewavee.data.api.CreateClothingItemApiRequest
import com.example.crimewavee.data.api.CreateClothingItemApiRequestNoId
import com.example.crimewavee.data.api.UpdateClothingItemApiRequest
import com.example.crimewavee.data.api.UpdateStockApiRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.google.gson.Gson

class ProductRepository(context: Context) {
    private val appContext = context.applicationContext
    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences("product_prefs_v3", Context.MODE_PRIVATE)

    // Configuración automática del microservicio
    // Detecta automáticamente: Emulador, Dispositivo Local, o AWS EC2
    private val baseUrl = ServerConfig.getBaseUrl()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .build()

    private val apiService = retrofit.create(ClothingItemApiService::class.java)
    private val gson = Gson()

    companion object {
        private const val KEY_PRODUCTS_INITIALIZED = "products_initialized_v3"
        private const val USE_MICROSERVICE = true // Usando microservicio con AWS RDS PostgreSQL
    }

    init {
        // Solo inicializar una vez
        if (!sharedPreferences.getBoolean(KEY_PRODUCTS_INITIALIZED, false)) {
            initializeDefaultProducts()
            sharedPreferences.edit()
                .putBoolean(KEY_PRODUCTS_INITIALIZED, true)
                .commit()
        }
    }

    private fun initializeDefaultProducts() {
        // Producto 1
        saveProduct("1", "Polera Satoru Gojo", "Diseño original de Satoru Gojo del anime Jujutsu Kaisen",
                   22000.0, "satorupolera", "POLERAS", true, false)

        // Producto 2
        saveProduct("2", "Polerón Toga Himiko", "Polerón con diseño de Himiko Toga del anime My Hero Academia",
                   42000.0, "togahoodie", "POLERONES", true, true)

        // Producto 3
        saveProduct("3", "Cuadro Given", "Cuadro decorativo minimalista con diseño del anime Given",
                   45000.0, "givencuadro", "CUADROS", true, false)
    }

    private fun saveProduct(id: String, name: String, description: String, price: Double,
                          imageUrl: String, category: String, isNew: Boolean, isFeatured: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString("product_${id}_name", name)
        editor.putString("product_${id}_description", description)
        editor.putString("product_${id}_price", price.toString())
        editor.putString("product_${id}_imageUrl", imageUrl)
        editor.putString("product_${id}_category", category)
        editor.putBoolean("product_${id}_isNew", isNew)
        editor.putBoolean("product_${id}_isFeatured", isFeatured)
        editor.commit()
    }

    fun getProducts(): List<ClothingItem> {
        val products = mutableListOf<ClothingItem>()

        for (i in 1..10) { // Buscar hasta 10 productos
            val id = i.toString()
            val name = sharedPreferences.getString("product_${id}_name", null)

            if (name != null) {
                val description = sharedPreferences.getString("product_${id}_description", "") ?: ""
                val priceStr = sharedPreferences.getString("product_${id}_price", "0.0") ?: "0.0"
                val price = priceStr.toDoubleOrNull() ?: 0.0
                val imageUrl = sharedPreferences.getString("product_${id}_imageUrl", "") ?: ""
                val categoryStr = sharedPreferences.getString("product_${id}_category", "POLERAS") ?: "POLERAS"
                val isNew = sharedPreferences.getBoolean("product_${id}_isNew", false)
                val isFeatured = sharedPreferences.getBoolean("product_${id}_isFeatured", false)

                val category = try {
                    ProductType.valueOf(categoryStr)
                } catch (e: Exception) {
                    ProductType.POLERAS
                }

                val sizes = when (category) {
                    ProductType.CUADROS -> listOf("30x39", "40x50", "50x70", "70x81")
                    else -> listOf("S", "M", "L", "XL")
                }

                products.add(
                    ClothingItem(
                        id = id,
                        name = name,
                        description = description,
                        price = price,
                        imageUrl = imageUrl,
                        imageUrls = listOf(imageUrl),
                        category = category,
                        isNew = isNew,
                        isFeatured = isFeatured,
                        sizes = sizes
                    )
                )
            }
        }

        return products
    }

    fun addProduct(product: ClothingItem): Boolean {
        if (getProducts().any { it.id == product.id }) {
            return false
        }

        saveProduct(
            product.id,
            product.name,
            product.description,
            product.price,
            product.imageUrl,
            product.category.name,
            product.isNew,
            product.isFeatured
        )
        return true
    }

    fun updateProduct(product: ClothingItem): Boolean {
        val existingProducts = getProducts()
        if (existingProducts.any { it.id == product.id }) {
            saveProduct(
                product.id,
                product.name,
                product.description,
                product.price,
                product.imageUrl,
                product.category.name,
                product.isNew,
                product.isFeatured
            )
            return true
        }
        return false
    }

    fun deleteProduct(productId: String): Boolean {
        val editor = sharedPreferences.edit()
        val keys = listOf("name", "description", "price", "imageUrl", "category", "isNew", "isFeatured")
        keys.forEach { key ->
            editor.remove("product_${productId}_$key")
        }
        return editor.commit()
    }

    fun getProductById(id: String): ClothingItem? {
        val name = sharedPreferences.getString("product_${id}_name", null) ?: return null

        val description = sharedPreferences.getString("product_${id}_description", "") ?: ""
        val priceStr = sharedPreferences.getString("product_${id}_price", "0.0") ?: "0.0"
        val price = priceStr.toDoubleOrNull() ?: 0.0
        val imageUrl = sharedPreferences.getString("product_${id}_imageUrl", "") ?: ""
        val categoryStr = sharedPreferences.getString("product_${id}_category", "POLERAS") ?: "POLERAS"
        val isNew = sharedPreferences.getBoolean("product_${id}_isNew", false)
        val isFeatured = sharedPreferences.getBoolean("product_${id}_isFeatured", false)

        val category = try {
            ProductType.valueOf(categoryStr)
        } catch (e: Exception) {
            ProductType.POLERAS
        }

        val sizes = when (category) {
            ProductType.CUADROS -> listOf("30x39", "40x50", "50x70", "70x81")
            else -> listOf("S", "M", "L", "XL")
        }

        return ClothingItem(
            id = id,
            name = name,
            description = description,
            price = price,
            imageUrl = imageUrl,
            imageUrls = listOf(imageUrl),
            category = category,
            isNew = isNew,
            isFeatured = isFeatured,
            sizes = sizes
        )
    }

    fun getProductsByCategory(category: ProductType): List<ClothingItem> {
        return getProducts().filter { it.category == category }
    }

    fun getFeaturedProducts(): List<ClothingItem> {
        return getProducts().filter { it.isFeatured }
    }

    fun getNewProducts(): List<ClothingItem> {
        return getProducts().filter { it.isNew }
    }

    fun forceResetProducts() {
        sharedPreferences.edit().clear().commit()
        initializeDefaultProducts()
        sharedPreferences.edit()
            .putBoolean(KEY_PRODUCTS_INITIALIZED, true)
            .commit()
    }

    fun clearProducts() {
        sharedPreferences.edit().clear().commit()
    }

    // Métodos para integración con microservicio
    suspend fun getAllProductsFromMicroservice(): Result<List<ClothingItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllProducts()
                val clothingItems = response.map { it.toClothingItem() }
                Result.success(clothingItems)
            } catch (e: Exception) {
                // Fallback a datos locales
                Result.success(getProducts())
            }
        }
    }

    suspend fun createProductInMicroservice(clothingItem: ClothingItem): Result<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🌐 === CREANDO PRODUCTO EN API ===")
                Log.d("ProductRepository", "📍 URL API: $baseUrl")
                Log.d("ProductRepository", "📦 Producto: ${clothingItem.name}")
                Log.d("ProductRepository", "📊 ID=${clothingItem.id}, Stock=${clothingItem.stock}, Precio=${clothingItem.price}")
                Log.d("ProductRepository", "🖼️ Imagen URL: ${clothingItem.imageUrl}")
                Log.d("ProductRepository", "📂 Categoría: ${clothingItem.category.name}")

                val request = CreateClothingItemApiRequest(
                    id = clothingItem.id,
                    name = clothingItem.name,
                    description = clothingItem.description,
                    price = clothingItem.price,
                    imageUrl = clothingItem.imageUrl,
                    category = clothingItem.category.name,
                    isNew = clothingItem.isNew,
                    isFeatured = clothingItem.isFeatured,
                    stock = clothingItem.stock
                )

                Log.d("ProductRepository", "📡 Enviando request POST a /api/products...")
                val response = apiService.createProduct(request)

                Log.d("ProductRepository", "✅ ¡API RESPONDIÓ EXITOSAMENTE!")
                Log.d("ProductRepository", "📦 Producto creado: ${response.name} (ID: ${response.id})")
                Log.d("ProductRepository", "💾 Guardado en PostgreSQL con éxito")

                Result.success(response.toClothingItem())
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ === ERROR CREANDO EN API ===")
                Log.e("ProductRepository", "📍 URL que falló: $baseUrl")
                Log.e("ProductRepository", "❌ Error: ${e.message}")
                Log.e("ProductRepository", "🔍 Tipo de error: ${e::class.simpleName}")

                // Imprimir stack trace completo para debugging
                e.printStackTrace()

                Log.e("ProductRepository", "🔄 Intentando guardar localmente como fallback...")

                // Fallback a creación local
                val localSuccess = addProduct(clothingItem)
                if (localSuccess) {
                    Log.w("ProductRepository", "⚠️ Producto guardado SOLO LOCALMENTE (no en servidor)")
                    Result.success(clothingItem)
                } else {
                    Log.e("ProductRepository", "❌ FALLÓ TODO: ni API ni local funcionaron")
                    Result.failure(e)
                }
            }
        }
    }

    /**
     * Crea un producto en el microservicio SIN FALLBACK LOCAL - Función estricta
     */
    suspend fun createProductInMicroserviceStrict(clothingItem: ClothingItem): Result<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🌐 === CREANDO PRODUCTO EN API (ESTRICTO) ===")
                Log.d("ProductRepository", "📍 URL API: $baseUrl")
                Log.d("ProductRepository", "🔗 Endpoint completo: ${baseUrl}api/products")
                Log.d("ProductRepository", "📦 Producto: ${clothingItem.name}")
                Log.d("ProductRepository", "📊 Precio=${clothingItem.price}, Stock=${clothingItem.stock}")
                Log.d("ProductRepository", "🖼️ Imagen URL: ${clothingItem.imageUrl}")
                Log.d("ProductRepository", "📂 Categoría: ${clothingItem.category.name}")

                // VALIDACIÓN PREVIA DE DATOS
                Log.d("ProductRepository", "🔍 VALIDANDO DATOS ANTES DE ENVIAR...")

                if (clothingItem.name.isBlank()) {
                    throw IllegalArgumentException("Nombre del producto no puede estar vacío")
                }
                if (clothingItem.description.isBlank()) {
                    throw IllegalArgumentException("Descripción del producto no puede estar vacía")
                }
                if (clothingItem.price <= 0) {
                    throw IllegalArgumentException("Precio debe ser mayor a 0. Valor actual: ${clothingItem.price}")
                }
                if (clothingItem.stock < 0) {
                    throw IllegalArgumentException("Stock no puede ser negativo. Valor actual: ${clothingItem.stock}")
                }

                // Crear request SIN ID (que el servidor lo genere automáticamente)
                val request = CreateClothingItemApiRequestNoId(
                    name = clothingItem.name.trim(),
                    description = clothingItem.description.trim(),
                    price = clothingItem.price,
                    imageUrl = clothingItem.imageUrl.trim(),
                    category = clothingItem.category.name,
                    isNew = clothingItem.isNew,
                    isFeatured = clothingItem.isFeatured,
                    stock = clothingItem.stock
                )

                Log.d("ProductRepository", "📋 REQUEST JSON COMPLETO:")
                Log.d("ProductRepository", "   {")
                Log.d("ProductRepository", "     \"name\": \"${request.name}\"")
                Log.d("ProductRepository", "     \"description\": \"${request.description}\"")
                Log.d("ProductRepository", "     \"price\": ${request.price}")
                Log.d("ProductRepository", "     \"imageUrl\": \"${request.imageUrl}\"")
                Log.d("ProductRepository", "     \"category\": \"${request.category}\"")
                Log.d("ProductRepository", "     \"isNew\": ${request.isNew}")
                Log.d("ProductRepository", "     \"isFeatured\": ${request.isFeatured}")
                Log.d("ProductRepository", "     \"stock\": ${request.stock}")
                Log.d("ProductRepository", "   }")

                // Serializar a JSON para debugging
                val jsonString = gson.toJson(request)
                Log.d("ProductRepository", "🔍 JSON SERIALIZADO: $jsonString")

                Log.d("ProductRepository", "📡 Enviando request POST a /api/products...")

                // Intentar primero con el endpoint normal (el servidor puede no soportar el endpoint sin ID)
                val response = try {
                    Log.d("ProductRepository", "🔄 Intentando endpoint estándar /api/products")
                    apiService.createProductNoId(request)
                } catch (e: Exception) {
                    Log.w("ProductRepository", "⚠️ Endpoint sin ID falló, intentando con endpoint normal...")

                    // Si falla, crear un request con ID temporal
                    val requestWithId = CreateClothingItemApiRequest(
                        id = "temp_${System.currentTimeMillis()}", // ID temporal
                        name = request.name,
                        description = request.description,
                        price = request.price,
                        imageUrl = request.imageUrl,
                        category = request.category,
                        isNew = request.isNew,
                        isFeatured = request.isFeatured,
                        stock = request.stock
                    )

                    Log.d("ProductRepository", "🔄 Usando endpoint normal con ID temporal: ${requestWithId.id}")
                    apiService.createProduct(requestWithId)
                }

                Log.d("ProductRepository", "✅ ¡API RESPONDIÓ EXITOSAMENTE!")
                Log.d("ProductRepository", "📦 Producto creado: ${response.name} (ID: ${response.id})")
                Log.d("ProductRepository", "💾 Confirmado: Guardado en PostgreSQL")
                Log.d("ProductRepository", "🌐 Ahora visible en Postman")

                Result.success(response.toClothingItem())
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ === FALLO TOTAL EN API ===")
                Log.e("ProductRepository", "📍 URL completa: ${baseUrl}api/products")
                Log.e("ProductRepository", "❌ Error específico: ${e.message}")
                Log.e("ProductRepository", "🔍 Tipo de error: ${e::class.simpleName}")
                Log.e("ProductRepository", "📱 Stack trace completo:")
                e.printStackTrace()

                // Información adicional del error
                when (e) {
                    is java.net.ConnectException -> {
                        Log.e("ProductRepository", "🚫 Error de conexión - Servidor no disponible")
                    }
                    is java.net.SocketTimeoutException -> {
                        Log.e("ProductRepository", "⏰ Timeout - Servidor muy lento")
                    }
                    is retrofit2.HttpException -> {
                        Log.e("ProductRepository", "🌐 Error HTTP ${e.code()}: ${e.message()}")
                        Log.e("ProductRepository", "🔗 URL que falló: ${e.response()?.raw()?.request?.url}")

                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            Log.e("ProductRepository", "📋 RESPUESTA COMPLETA DEL SERVIDOR:")
                            Log.e("ProductRepository", "   Status: ${e.code()}")
                            Log.e("ProductRepository", "   Message: ${e.message()}")
                            Log.e("ProductRepository", "   Body: $errorBody")

                            // Análisis específico para HTTP 400
                            if (e.code() == 400) {
                                Log.e("ProductRepository", "🚫 HTTP 400 BAD REQUEST DETECTADO:")
                                when {
                                    errorBody?.contains("name", ignoreCase = true) == true -> {
                                        Log.e("ProductRepository", "   ❌ Problema con el campo 'name'")
                                    }
                                    errorBody?.contains("price", ignoreCase = true) == true -> {
                                        Log.e("ProductRepository", "   ❌ Problema con el campo 'price'")
                                    }
                                    errorBody?.contains("category", ignoreCase = true) == true -> {
                                        Log.e("ProductRepository", "   ❌ Problema con el campo 'category'")
                                    }
                                    errorBody?.contains("required", ignoreCase = true) == true -> {
                                        Log.e("ProductRepository", "   ❌ Campos requeridos faltantes")
                                    }
                                    errorBody?.contains("validation", ignoreCase = true) == true -> {
                                        Log.e("ProductRepository", "   ❌ Error de validación")
                                    }
                                    else -> {
                                        Log.e("ProductRepository", "   ❌ Error HTTP 400 genérico")
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("ProductRepository", "❌ No se pudo leer error del servidor: ${ex.message}")
                        }
                    }
                    else -> {
                        Log.e("ProductRepository", "❓ Error desconocido: ${e.javaClass.simpleName}")
                    }
                }

                Log.e("ProductRepository", "⚠️ SIN FALLBACK - Producto NO guardado")

                // NO HAY FALLBACK LOCAL - Si falla la API, falla todo
                Result.failure(Exception("Error creando en servidor: ${e.message}"))
            }
        }
    }

    suspend fun updateProductInMicroservice(clothingItem: ClothingItem): Result<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateClothingItemApiRequest(
                    name = clothingItem.name,
                    description = clothingItem.description,
                    price = clothingItem.price,
                    imageUrl = clothingItem.imageUrl,
                    category = clothingItem.category.name,
                    isNew = clothingItem.isNew,
                    isFeatured = clothingItem.isFeatured
                )
                val response = apiService.updateProduct(clothingItem.id, request)
                Result.success(response.toClothingItem())
            } catch (e: Exception) {
                // Fallback a actualización local
                if (updateProduct(clothingItem)) {
                    Result.success(clothingItem)
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    suspend fun deleteProductInMicroservice(productId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🌐 Eliminando producto de API: $productId")
                Log.d("ProductRepository", "🔗 URL usada: $baseUrl")

                apiService.deleteProduct(productId)
                Log.d("ProductRepository", "✅ API respondió exitosamente - producto eliminado: $productId")

                Result.success(true)
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error en API eliminando $productId: ${e.message}")
                Log.e("ProductRepository", "🔄 Intentando fallback local...")

                // Fallback a eliminación local
                val localSuccess = deleteProduct(productId)
                if (localSuccess) {
                    Log.d("ProductRepository", "✅ Producto eliminado localmente como fallback")
                } else {
                    Log.e("ProductRepository", "❌ Fallback local también falló")
                }
                Result.success(localSuccess)
            }
        }
    }

    suspend fun updateStockInMicroservice(productId: String, newStock: Int): Result<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateStockApiRequest(newStock)
                val response = apiService.updateStock(productId, request)
                Result.success(response.toClothingItem())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun reduceStockInMicroservice(productId: String, quantity: Int): Result<ClothingItem?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.reduceStock(productId, quantity)
                Result.success(response?.toClothingItem())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Método de conveniencia que usa microservicio o datos locales según configuración
    suspend fun getProductsAsync(): List<ClothingItem> {
        return if (USE_MICROSERVICE) {
            getAllProductsFromMicroservice().getOrElse { getProducts() }
        } else {
            getProducts()
        }
    }

    // === FUNCIONES DE SINCRONIZACIÓN MEJORADAS ===

    /**
     * Sincroniza los productos locales con el servidor
     * Descarga todos los productos del servidor y actualiza el cache local
     */
    suspend fun syncWithServer(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🔄 Iniciando sincronización con servidor...")

                // Obtener productos del servidor
                val serverProducts = apiService.getAllProducts()
                Log.d("ProductRepository", "✅ Obtenidos ${serverProducts.size} productos del servidor")

                // Limpiar datos locales y reemplazar con datos del servidor
                clearAllProducts()
                serverProducts.forEach { apiProduct ->
                    val localProduct = apiProduct.toClothingItem()
                    addProductToCache(localProduct)
                }

                // Marcar como sincronizado
                sharedPreferences.edit()
                    .putLong("last_sync", System.currentTimeMillis())
                    .apply()

                Log.d("ProductRepository", "✅ Sincronización completada exitosamente")
                Result.success(true)
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error en sincronización: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Verifica si necesita sincronización (más de 5 minutos desde la última sync)
     */
    fun needsSync(): Boolean {
        val lastSync = sharedPreferences.getLong("last_sync", 0)
        val now = System.currentTimeMillis()
        return (now - lastSync) > 300_000 // 5 minutos
    }

    /**
     * Obtiene productos con sincronización automática si es necesario
     */
    suspend fun getProductsWithAutoSync(): List<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                // Si usa microservicio y necesita sync, sincronizar primero
                if (USE_MICROSERVICE && needsSync()) {
                    Log.d("ProductRepository", "🔄 Sincronización automática iniciada...")
                    syncWithServer()
                }

                // Obtener productos (ya sincronizados si era necesario)
                if (USE_MICROSERVICE) {
                    getAllProductsFromMicroservice().getOrElse { getProducts() }
                } else {
                    getProducts()
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error en getProductsWithAutoSync: ${e.message}")
                getProducts() // Fallback a datos locales
            }
        }
    }

    /**
     * Crea un producto directamente en el microservicio
     */
    suspend fun createProductAndSync(clothingItem: ClothingItem): Result<ClothingItem> {
        Log.d("ProductRepository", "🚀 === CREAR PRODUCTO EN API (DIRECTO) ===")
        return createProductInMicroservice(clothingItem)
    }

    /**
     * Actualiza un producto y lo sincroniza inmediatamente
     */
    suspend fun updateProductAndSync(clothingItem: ClothingItem): Result<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "✏️ Actualizando producto: ${clothingItem.name}")

                if (USE_MICROSERVICE) {
                    // Actualizar en el servidor
                    val result = updateProductInMicroservice(clothingItem)

                    if (result.isSuccess) {
                        // Sincronizar inmediatamente
                        syncWithServer()
                        Log.d("ProductRepository", "✅ Producto actualizado y sincronizado: ${clothingItem.name}")
                    }

                    result
                } else {
                    // Solo local
                    if (updateProduct(clothingItem)) {
                        Result.success(clothingItem)
                    } else {
                        Result.failure(Exception("No se pudo actualizar el producto localmente"))
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error actualizando producto: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Elimina un producto del servidor y sincroniza
     */
    suspend fun deleteProductAndSync(productId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🗑️ Eliminando producto: $productId")

                if (USE_MICROSERVICE) {
                    // Eliminar del servidor
                    apiService.deleteProduct(productId)

                    // Sincronizar inmediatamente
                    syncWithServer()
                    Log.d("ProductRepository", "✅ Producto eliminado y sincronizado: $productId")

                    Result.success(true)
                } else {
                    // Solo local
                    val success = deleteProduct(productId)
                    if (success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception("No se pudo eliminar el producto localmente"))
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error eliminando producto: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * FUNCIONES DE DIAGNÓSTICO Y LIMPIEZA
     * Para solucionar problemas como el Cuadro Gojo persistente
     */

    /**
     * Diagnóstica y reporta diferencias entre datos locales y del servidor
     */
    suspend fun diagnosticDataInconsistency(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val localProducts = getProducts()
                val serverResponse = apiService.getAllProducts()
                val serverProducts = serverResponse.map { it.toClothingItem() }

                val report = StringBuilder()
                report.appendLine("📊 DIAGNÓSTICO DE CONSISTENCIA DE DATOS")
                report.appendLine("=====================================")
                report.appendLine("📱 Productos locales: ${localProducts.size}")
                report.appendLine("🌐 Productos servidor: ${serverProducts.size}")
                report.appendLine()

                // Productos que están en local pero no en servidor
                val onlyLocal = localProducts.filter { local ->
                    serverProducts.none { server -> server.id == local.id }
                }
                if (onlyLocal.isNotEmpty()) {
                    report.appendLine("❌ SOLO EN LOCAL (deberían eliminarse):")
                    onlyLocal.forEach { product ->
                        report.appendLine("   - ID: ${product.id}, Nombre: ${product.name}")
                    }
                    report.appendLine()
                }

                // Productos que están en servidor pero no en local
                val onlyServer = serverProducts.filter { server ->
                    localProducts.none { local -> local.id == server.id }
                }
                if (onlyServer.isNotEmpty()) {
                    report.appendLine("❌ SOLO EN SERVIDOR (deberían agregarse):")
                    onlyServer.forEach { product ->
                        report.appendLine("   - ID: ${product.id}, Nombre: ${product.name}")
                    }
                    report.appendLine()
                }

                // Buscar específicamente productos con "Gojo"
                val gojoLocal = localProducts.filter { it.name.contains("Gojo", ignoreCase = true) }
                val gojoServer = serverProducts.filter { it.name.contains("Gojo", ignoreCase = true) }

                report.appendLine("🔍 PRODUCTOS CON 'GOJO':")
                report.appendLine("📱 Local: ${gojoLocal.size} productos")
                gojoLocal.forEach { report.appendLine("   - ID: ${it.id}, Nombre: ${it.name}") }
                report.appendLine("🌐 Servidor: ${gojoServer.size} productos")
                gojoServer.forEach { report.appendLine("   - ID: ${it.id}, Nombre: ${it.name}") }
                report.appendLine()

                if (onlyLocal.isEmpty() && onlyServer.isEmpty()) {
                    report.appendLine("✅ DATOS CONSISTENTES")
                } else {
                    report.appendLine("❌ INCONSISTENCIA DETECTADA - Se recomienda sincronización forzada")
                }

                Log.d("ProductRepository", report.toString())
                Result.success(report.toString())
            } catch (e: Exception) {
                val error = "❌ Error en diagnóstico: ${e.message}"
                Log.e("ProductRepository", error)
                Result.failure(Exception(error))
            }
        }
    }

    /**
     * Elimina completamente un producto específico del servidor Y del cache local
     */
    suspend fun forceDeleteProduct(productId: String, productName: String? = null): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🗑️ ELIMINACIÓN FORZADA - ID: $productId, Nombre: $productName")

                var deleted = false

                // 1. Intentar eliminar del servidor por ID
                try {
                    apiService.deleteProduct(productId)
                    Log.d("ProductRepository", "✅ Eliminado del servidor por ID: $productId")
                    deleted = true
                } catch (e: Exception) {
                    Log.w("ProductRepository", "⚠️ No se pudo eliminar del servidor por ID: ${e.message}")
                }

                // 2. Si hay nombre, buscar y eliminar por nombre
                if (productName != null && !deleted) {
                    try {
                        val allProducts = apiService.getAllProducts()
                        val targetProducts = allProducts.filter {
                            it.name.contains(productName, ignoreCase = true)
                        }

                        targetProducts.forEach { product ->
                            try {
                                apiService.deleteProduct(product.id)
                                Log.d("ProductRepository", "✅ Eliminado del servidor por nombre: ${product.name} (ID: ${product.id})")
                                deleted = true
                            } catch (e: Exception) {
                                Log.w("ProductRepository", "⚠️ Error eliminando ${product.name}: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("ProductRepository", "⚠️ Error buscando por nombre: ${e.message}")
                    }
                }

                // 3. Eliminar del cache local
                deleteProduct(productId)

                // 4. Si hay nombre, eliminar todas las coincidencias locales
                if (productName != null) {
                    val localProducts = getProducts()
                    localProducts.filter {
                        it.name.contains(productName, ignoreCase = true)
                    }.forEach { product ->
                        deleteProduct(product.id)
                        Log.d("ProductRepository", "🗑️ Eliminado del cache local: ${product.name} (ID: ${product.id})")
                    }
                }

                // 5. Forzar sincronización completa
                syncWithServer()

                Log.d("ProductRepository", "✅ ELIMINACIÓN FORZADA COMPLETADA")
                Result.success(deleted)
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error en eliminación forzada: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Limpia completamente el cache local y re-sincroniza desde el servidor
     */
    suspend fun forceFullResync(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🔄 RESINCRONIZACIÓN COMPLETA INICIADA")

                // 1. Limpiar completamente el cache local
                clearAllProducts()
                Log.d("ProductRepository", "🗑️ Cache local limpiado")

                // 2. Obtener datos frescos del servidor
                val serverProducts = apiService.getAllProducts()
                Log.d("ProductRepository", "📥 Descargados ${serverProducts.size} productos del servidor")

                // 3. Repoblar cache local con datos del servidor
                serverProducts.forEach { apiProduct ->
                    val localProduct = apiProduct.toClothingItem()
                    addProductToCache(localProduct)
                }

                // 4. Marcar como sincronizado
                sharedPreferences.edit()
                    .putLong("last_sync", System.currentTimeMillis())
                    .putBoolean("force_resync_completed", true)
                    .apply()

                Log.d("ProductRepository", "✅ RESINCRONIZACIÓN COMPLETA EXITOSA")
                Result.success(true)
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error en resincronización: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Función específica para eliminar el Cuadro Gojo problemático
     */
    suspend fun deleteCuadroGojoSpecific(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🎯 ELIMINACIÓN ESPECÍFICA: Cuadro Gojo")

                // Buscar y eliminar todos los productos con "Gojo" en el nombre
                val result = forceDeleteProduct("4", "Cuadro Gojo")

                if (result.isSuccess) {
                    // También buscar variaciones del nombre
                    forceDeleteProduct("", "Gojo")
                    forceDeleteProduct("", "cuadro.*gojo")

                    Log.d("ProductRepository", "✅ Cuadro Gojo eliminado exitosamente")
                } else {
                    Log.w("ProductRepository", "⚠️ No se pudo eliminar Cuadro Gojo completamente")
                }

                result
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error eliminando Cuadro Gojo: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Función auxiliar para limpiar todos los productos del cache local
     */
    private fun clearAllProducts() {
        sharedPreferences.edit().clear().apply()
        sharedPreferences.edit()
            .putBoolean(KEY_PRODUCTS_INITIALIZED, true)
            .apply()
    }

    /**
     * Función auxiliar para agregar un producto al cache local sin validaciones
     */
    private fun addProductToCache(product: ClothingItem): Boolean {
        return try {
            val json = gson.toJson(product)
            sharedPreferences.edit()
                .putString("product_${product.id}", json)
                .apply()
            true
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error guardando producto en cache: ${e.message}")
            false
        }
    }

    // Extensión para convertir response del API a ClothingItem
    private fun ClothingItemResponse.toClothingItem(): ClothingItem {
        val productType = try {
            ProductType.valueOf(this.category)
        } catch (e: Exception) {
            ProductType.POLERAS
        }

        val sizes = when (productType) {
            ProductType.CUADROS -> listOf("30x39", "40x50", "50x70", "70x81")
            else -> listOf("S", "M", "L", "XL")
        }

        return ClothingItem(
            id = this.id,
            name = this.name,
            description = this.description,
            price = this.price,
            imageUrl = this.imageUrl,
            imageUrls = listOf(this.imageUrl),
            category = productType,
            isNew = this.isNew,
            isFeatured = this.isFeatured,
            sizes = sizes
        )
    }

    /**
     * Función de prueba para determinar exactamente qué formato espera el servidor
     */
    suspend fun testMinimalProductCreation(): Result<ClothingItem> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🧪 === PRUEBA CON DATOS MÍNIMOS ===")

                // Crear el producto más básico posible
                val minimalRequest = CreateClothingItemApiRequestNoId(
                    name = "Test Product",
                    description = "Test Description",
                    price = 15000.0,
                    imageUrl = "test-image",
                    category = "POLERAS",
                    isNew = false,
                    isFeatured = false,
                    stock = 1
                )

                Log.d("ProductRepository", "📋 Enviando datos mínimos:")
                Log.d("ProductRepository", "   name: Test Product")
                Log.d("ProductRepository", "   price: 15000.0")
                Log.d("ProductRepository", "   category: POLERAS")
                Log.d("ProductRepository", "   stock: 1")

                val response = apiService.createProductNoId(minimalRequest)
                Log.d("ProductRepository", "✅ Datos mínimos funcionaron!")

                Result.success(response.toClothingItem())
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Incluso datos mínimos fallan: ${e.message}")
                Result.failure(e)
            }
        }
    }
}
