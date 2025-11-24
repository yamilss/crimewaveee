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
                val products = productRepository.getProductsAsync()
                _products.value = products
            } catch (e: Exception) {
                _products.value = emptyList()
            }
        }
    }

    // Métodos CRUD para productos conectados al microservicio
    fun createProduct(clothingItem: ClothingItem) {
        viewModelScope.launch {
            try {
                val result = productRepository.createProductInMicroservice(clothingItem)
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

    fun updateProductInService(clothingItem: ClothingItem) {
        viewModelScope.launch {
            try {
                val result = productRepository.updateProductInMicroservice(clothingItem)
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

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                val result = productRepository.deleteProductInMicroservice(productId)
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
}
