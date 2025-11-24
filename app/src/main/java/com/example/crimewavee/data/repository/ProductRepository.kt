package com.example.crimewavee.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.data.api.ClothingItemApiService
import com.example.crimewavee.data.api.ClothingItemResponse
import com.example.crimewavee.data.api.CreateClothingItemApiRequest
import com.example.crimewavee.data.api.UpdateClothingItemApiRequest
import com.example.crimewavee.data.api.UpdateStockApiRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

        // Producto 4 - Ahora destacado
        saveProduct("4", "Cuadro Gojo", "Cuadro decorativo minimalista con diseño de Satoru Gojo del anime Jujutsu Kaisen",
                   35000.0, "satorupolera", "CUADROS", true, true)
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
                val request = CreateClothingItemApiRequest(
                    id = clothingItem.id,
                    name = clothingItem.name,
                    description = clothingItem.description,
                    price = clothingItem.price,
                    imageUrl = clothingItem.imageUrl,
                    category = clothingItem.category.name,
                    isNew = clothingItem.isNew,
                    isFeatured = clothingItem.isFeatured,
                    stock = 10 // Stock por defecto
                )
                val response = apiService.createProduct(request)
                Result.success(response.toClothingItem())
            } catch (e: Exception) {
                // Fallback a creación local
                if (addProduct(clothingItem)) {
                    Result.success(clothingItem)
                } else {
                    Result.failure(e)
                }
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
                apiService.deleteProduct(productId)
                Result.success(true)
            } catch (e: Exception) {
                // Fallback a eliminación local
                Result.success(deleteProduct(productId))
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
}
