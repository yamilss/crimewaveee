package com.example.crimewavee.data.model

data class ClothingItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val imageUrls: List<String> = emptyList(),
    val category: ProductType,
    val isNew: Boolean = false,
    val isFeatured: Boolean = false,
    val specialOffer: String? = null,
    val sizes: List<String> = listOf("S", "M", "L", "XL"),
    val stock: Int = 10,
    val reviewCount: Int = 0
) {

    val allImages: List<String>
        get() = try {
            when {
                imageUrls.isNotEmpty() && imageUrls.size >= 3 -> imageUrls.take(3)
                imageUrls.isNotEmpty() -> {

                    val result = imageUrls.toMutableList()
                    while (result.size < 3) {
                        when (result.size) {
                            1 -> result.add("placeholder1")
                            2 -> result.add("placerholder2")
                        }
                    }
                    result.take(3)
                }
                imageUrl.isNotBlank() -> {

                    listOf(imageUrl, "placeholder1", "placerholder2")
                }
                else -> {

                    listOf("default_product", "placeholder1", "placerholder2")
                }
            }
        } catch (e: Exception) {

            listOf("default_product", "default_product_2", "default_product_3")
        }
}

data class ClothingCategory(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String
)

enum class ProductType {
    POLERAS,
    POLERONES,
    CUADROS
}
