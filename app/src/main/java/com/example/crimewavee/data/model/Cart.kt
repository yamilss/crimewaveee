package com.example.crimewavee.data.model

import com.example.crimewavee.data.model.ClothingItem

data class CartItem(
    val id: String,
    val clothingItem: ClothingItem,
    val quantity: Int,
    val selectedSize: String
) {
    val totalPrice: Double
        get() = clothingItem.price * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    companion object {
        private const val SHIPPING_THRESHOLD = 50_000.0
        private const val SHIPPING_FEE_VALUE = 5_000.0
        private const val IVA_RATE = 0.19
    }

    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val totalItems: Int
        get() = items.sumOf { it.quantity }

    val isEmpty: Boolean
        get() = items.isEmpty()

    val shippingFee: Double
        get() = if (subtotal >= SHIPPING_THRESHOLD) 0.0 else SHIPPING_FEE_VALUE

    val iva: Double
        get() = subtotal - (subtotal / (1 + IVA_RATE)) 

    val totalAmount: Double
        get() = subtotal + shippingFee 
}

enum class OrderStatus {
    PENDING,
    APPROVED,
    REJECTED,
    COMPLETED
}

data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val rejectionReason: String? = if (status == OrderStatus.REJECTED) "Monto insuficiente. Pruebe con otro medio de pago" else null
)
