package com.example.crimewavee.microservice.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*
import java.time.LocalDateTime

// Entidad para productos/prendas
@Entity
@Table(name = "clothing_items")
data class ClothingItemEntity(
    @Id
    val id: String,

    @Column(nullable = false)
    val name: String,

    @Column(length = 1000)
    val description: String,

    @Column(nullable = false)
    val price: Double,

    @Column(name = "image_url")
    val imageUrl: String,

    @Column(nullable = false)
    val category: String,

    @Column(name = "is_new")
    val isNew: Boolean = false,

    @Column(name = "is_featured")
    val isFeatured: Boolean = false,

    @Column(nullable = false)
    val stock: Int = 0,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

// Repository para operaciones CRUD de productos
interface ClothingItemRepository : JpaRepository<ClothingItemEntity, String> {
    fun findByCategory(category: String): List<ClothingItemEntity>
    fun findByIsFeatured(isFeatured: Boolean): List<ClothingItemEntity>
    fun findByIsNew(isNew: Boolean): List<ClothingItemEntity>
    fun findByStockGreaterThan(stock: Int): List<ClothingItemEntity>
    fun findByNameContainingIgnoreCase(name: String): List<ClothingItemEntity>
}

// DTOs para productos
data class CreateClothingItemRequest(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isNew: Boolean = false,
    val isFeatured: Boolean = false,
    val stock: Int = 0
)

data class UpdateClothingItemRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val imageUrl: String? = null,
    val category: String? = null,
    val isNew: Boolean? = null,
    val isFeatured: Boolean? = null,
    val stock: Int? = null
)

data class UpdateStockRequest(
    val stock: Int
)

// Controlador REST para productos
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["*"])
class ClothingItemController(
    private val clothingItemRepository: ClothingItemRepository
) {

    @GetMapping
    fun getAllProducts(): List<ClothingItemEntity> {
        return clothingItemRepository.findAll()
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: String): ClothingItemEntity? {
        return clothingItemRepository.findById(id).orElse(null)
    }

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): List<ClothingItemEntity> {
        return clothingItemRepository.findByCategory(category)
    }

    @GetMapping("/featured")
    fun getFeaturedProducts(): List<ClothingItemEntity> {
        return clothingItemRepository.findByIsFeatured(true)
    }

    @GetMapping("/new")
    fun getNewProducts(): List<ClothingItemEntity> {
        return clothingItemRepository.findByIsNew(true)
    }

    @GetMapping("/in-stock")
    fun getProductsInStock(): List<ClothingItemEntity> {
        return clothingItemRepository.findByStockGreaterThan(0)
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): List<ClothingItemEntity> {
        return clothingItemRepository.findByNameContainingIgnoreCase(name)
    }

    @PostMapping
    fun createProduct(@RequestBody request: CreateClothingItemRequest): ClothingItemEntity {
        val product = ClothingItemEntity(
            id = request.id,
            name = request.name,
            description = request.description,
            price = request.price,
            imageUrl = request.imageUrl,
            category = request.category,
            isNew = request.isNew,
            isFeatured = request.isFeatured,
            stock = request.stock
        )
        return clothingItemRepository.save(product)
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: String,
        @RequestBody request: UpdateClothingItemRequest
    ): ClothingItemEntity? {
        val existingProduct = clothingItemRepository.findById(id).orElse(null)
        return if (existingProduct != null) {
            val updatedProduct = existingProduct.copy(
                name = request.name ?: existingProduct.name,
                description = request.description ?: existingProduct.description,
                price = request.price ?: existingProduct.price,
                imageUrl = request.imageUrl ?: existingProduct.imageUrl,
                category = request.category ?: existingProduct.category,
                isNew = request.isNew ?: existingProduct.isNew,
                isFeatured = request.isFeatured ?: existingProduct.isFeatured,
                stock = request.stock ?: existingProduct.stock,
                updatedAt = LocalDateTime.now()
            )
            clothingItemRepository.save(updatedProduct)
        } else {
            null
        }
    }

    @PutMapping("/{id}/stock")
    fun updateStock(
        @PathVariable id: String,
        @RequestBody request: UpdateStockRequest
    ): ClothingItemEntity? {
        val product = clothingItemRepository.findById(id).orElse(null)
        return if (product != null) {
            val updatedProduct = product.copy(
                stock = request.stock,
                updatedAt = LocalDateTime.now()
            )
            clothingItemRepository.save(updatedProduct)
        } else {
            null
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: String): Boolean {
        return if (clothingItemRepository.existsById(id)) {
            clothingItemRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // Endpoint para reducir stock (cuando se hace una compra)
    @PostMapping("/{id}/reduce-stock/{quantity}")
    fun reduceStock(@PathVariable id: String, @PathVariable quantity: Int): ClothingItemEntity? {
        val product = clothingItemRepository.findById(id).orElse(null)
        return if (product != null && product.stock >= quantity) {
            val updatedProduct = product.copy(
                stock = product.stock - quantity,
                updatedAt = LocalDateTime.now()
            )
            clothingItemRepository.save(updatedProduct)
        } else {
            null // No hay suficiente stock
        }
    }

    // Endpoint para inicializar productos de prueba (solo para desarrollo)
    @PostMapping("/init-sample-data")
    fun initializeSampleData(): Map<String, String> {
        return try {
            val sampleProducts = listOf(
                ClothingItemEntity(
                    id = "1",
                    name = "Polera Satoru Gojo",
                    description = "Diseño original de Satoru Gojo del anime Jujutsu Kaisen",
                    price = 22000.0,
                    imageUrl = "satorupolera",
                    category = "POLERAS",
                    isNew = true,
                    isFeatured = false,
                    stock = 50
                ),
                ClothingItemEntity(
                    id = "2",
                    name = "Polerón Toga Himiko",
                    description = "Polerón con diseño de Himiko Toga del anime My Hero Academia",
                    price = 42000.0,
                    imageUrl = "togahoodie",
                    category = "POLERONES",
                    isNew = true,
                    isFeatured = true,
                    stock = 30
                ),
                ClothingItemEntity(
                    id = "3",
                    name = "Cuadro Given",
                    description = "Cuadro decorativo minimalista con diseño del anime Given",
                    price = 45000.0,
                    imageUrl = "givencuadro",
                    category = "CUADROS",
                    isNew = true,
                    isFeatured = false,
                    stock = 15
                ),
                ClothingItemEntity(
                    id = "4",
                    name = "Cuadro Gojo",
                    description = "Cuadro decorativo minimalista con diseño de Satoru Gojo del anime Jujutsu Kaisen",
                    price = 35000.0,
                    imageUrl = "satorupolera",
                    category = "CUADROS",
                    isNew = true,
                    isFeatured = true,
                    stock = 20
                )
            )

            sampleProducts.forEach { product ->
                if (!clothingItemRepository.existsById(product.id)) {
                    clothingItemRepository.save(product)
                }
            }

            mapOf("message" to "Datos de prueba inicializados exitosamente")
        } catch (e: Exception) {
            mapOf("error" to "Error al inicializar datos: ${e.message}")
        }
    }
}
