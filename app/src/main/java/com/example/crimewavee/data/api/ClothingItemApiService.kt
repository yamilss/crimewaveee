package com.example.crimewavee.data.api

import retrofit2.http.*
import com.google.gson.annotations.SerializedName

// Modelos para el microservicio de productos
data class ClothingItemResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("isNew")
    val isNew: Boolean,
    @SerializedName("isFeatured")
    val isFeatured: Boolean,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class CreateClothingItemApiRequest(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("isNew")
    val isNew: Boolean = false,
    @SerializedName("isFeatured")
    val isFeatured: Boolean = false,
    @SerializedName("stock")
    val stock: Int = 0
)

data class UpdateClothingItemApiRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("isNew")
    val isNew: Boolean? = null,
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,
    @SerializedName("stock")
    val stock: Int? = null
)

data class UpdateStockApiRequest(
    @SerializedName("stock")
    val stock: Int
)

// Interface para el microservicio de productos
interface ClothingItemApiService {

    @GET("api/products")
    suspend fun getAllProducts(): List<ClothingItemResponse>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: String): ClothingItemResponse

    @GET("api/products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<ClothingItemResponse>

    @GET("api/products/featured")
    suspend fun getFeaturedProducts(): List<ClothingItemResponse>

    @GET("api/products/new")
    suspend fun getNewProducts(): List<ClothingItemResponse>

    @GET("api/products/in-stock")
    suspend fun getProductsInStock(): List<ClothingItemResponse>

    @POST("api/products")
    suspend fun createProduct(@Body request: CreateClothingItemApiRequest): ClothingItemResponse

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body request: UpdateClothingItemApiRequest
    ): ClothingItemResponse

    @PUT("api/products/{id}/stock")
    suspend fun updateStock(
        @Path("id") id: String,
        @Body request: UpdateStockApiRequest
    ): ClothingItemResponse

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)

    @POST("api/products/{id}/reduce-stock/{quantity}")
    suspend fun reduceStock(
        @Path("id") id: String,
        @Path("quantity") quantity: Int
    ): ClothingItemResponse?
}
