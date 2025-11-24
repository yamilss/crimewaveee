package com.example.crimewavee.microservice

// Spring Boot imports
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin

// JPA/Hibernate imports (javax para Spring Boot 2.x)
import javax.persistence.*
import javax.validation.constraints.*
import javax.validation.Valid

// Java/Kotlin imports
import java.time.LocalDateTime
import java.math.BigDecimal

@SpringBootApplication
class CrimewaveProductsApplication

fun main(args: Array<String>) {
    runApplication<CrimewaveProductsApplication>(*args)
}

// Entidad y Repository se definen en el archivo separado ClothingItemController.kt

// Repository se define en el archivo separado ClothingItemController.kt

// DTOs para productos
data class CreateClothingItemRequest(
    @NotBlank(message = "ID es obligatorio")
    val id: String,

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 200, message = "Nombre no puede superar 200 caracteres")
    val name: String,

    @Size(max = 2000, message = "Descripción no puede superar 2000 caracteres")
    val description: String,

    @DecimalMin(value = "0.0", message = "Precio debe ser mayor a 0")
    val price: Double,

    @Size(max = 500, message = "URL de imagen no puede superar 500 caracteres")
    val imageUrl: String,

    @NotBlank(message = "Categoría es obligatoria")
    val category: String,

    val isNew: Boolean = false,
    val isFeatured: Boolean = false,

    @Min(value = 0, message = "Stock debe ser mayor o igual a 0")
    val stock: Int = 0
)

data class UpdateClothingItemRequest(
    @Size(max = 200, message = "Nombre no puede superar 200 caracteres")
    val name: String? = null,

    @Size(max = 2000, message = "Descripción no puede superar 2000 caracteres")
    val description: String? = null,

    @DecimalMin(value = "0.0", message = "Precio debe ser mayor a 0")
    val price: Double? = null,

    @Size(max = 500, message = "URL de imagen no puede superar 500 caracteres")
    val imageUrl: String? = null,

    val category: String? = null,
    val isNew: Boolean? = null,
    val isFeatured: Boolean? = null,

    @Min(value = 0, message = "Stock debe ser mayor o igual a 0")
    val stock: Int? = null
)

data class UpdateStockRequest(
    @Min(value = 0, message = "Stock debe ser mayor o igual a 0")
    val stock: Int
)


