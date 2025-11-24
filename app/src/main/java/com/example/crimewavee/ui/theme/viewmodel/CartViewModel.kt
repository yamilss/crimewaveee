package com.example.crimewavee.ui.theme.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import com.example.crimewavee.data.model.Cart
import com.example.crimewavee.data.model.CartItem
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.Order
import com.example.crimewavee.data.model.OrderStatus
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.data.model.User
import com.example.crimewavee.data.repository.CartRepository

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepository = CartRepository(application.applicationContext)

    private val _cartState = mutableStateOf(Cart())
    val cartState = _cartState

    private val _lastOrder = mutableStateOf<Order?>(null)
    val lastOrder = _lastOrder

    private var currentUser: User? = null

    init {
        _cartState.value = getSampleCart()
        _lastOrder.value = null
    }

    fun setCurrentUser(user: User?) {
        currentUser = user
        if (user != null && !user.isAdmin) {
            cartRepository.setCurrentUser(user.email)
            _cartState.value = cartRepository.getCart()
            _lastOrder.value = cartRepository.getLastOrder()
        } else if (user != null && user.isAdmin) {
            cartRepository.setCurrentUser(null)
            _cartState.value = getSampleCart()
            _lastOrder.value = null
        } else {
            cartRepository.setCurrentUser(null)
            _cartState.value = Cart()
            _lastOrder.value = null
        }
    }

    private fun canUseCart(): Boolean {
        return currentUser != null && !currentUser!!.isAdmin
    }

    fun addToCart(clothingItem: ClothingItem, selectedSize: String, quantity: Int = 1) {
        if (!canUseCart()) return

        val cartItem = CartItem(
            id = "${clothingItem.id}_${selectedSize}_${System.currentTimeMillis()}",
            clothingItem = clothingItem,
            quantity = quantity,
            selectedSize = selectedSize
        )

        cartRepository.addItemToCart(cartItem)
        _cartState.value = cartRepository.getCart()
    }

    fun removeFromCart(cartItemId: String) {
        if (!canUseCart()) return

        cartRepository.removeItemFromCart(cartItemId)
        _cartState.value = cartRepository.getCart()
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        if (!canUseCart()) return

        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
            return
        }

        cartRepository.updateItemQuantity(cartItemId, newQuantity)
        _cartState.value = cartRepository.getCart()
    }

    fun clearCart() {
        if (!canUseCart()) return

        cartRepository.clearCart()
        _cartState.value = Cart()
    }

    fun checkout(): Order {
        if (!canUseCart()) {
            return Order(
                id = "INVALID_ORDER",
                items = emptyList(),
                totalAmount = 0.0,
                status = OrderStatus.REJECTED,
                rejectionReason = "Usuario no autorizado"
            )
        }

        val cart = _cartState.value
        val totalAmount = cart.totalAmount

        val status = if (totalAmount > 100000) {
            OrderStatus.REJECTED
        } else {
            OrderStatus.APPROVED
        }

        val order = Order(
            id = "ORDEN N-${System.currentTimeMillis()}",
            items = cart.items,
            totalAmount = totalAmount,
            status = status,
            rejectionReason = if (status == OrderStatus.REJECTED)
                "Monto insuficiente. Pruebe con otro medio de pago"
            else null
        )

        _lastOrder.value = order
        cartRepository.saveLastOrder(order)

        if (status == OrderStatus.APPROVED) {
            clearCart()
        }

        return order
    }

    fun getCartItemCount(): Int = if (canUseCart()) _cartState.value.totalItems else 0

    fun getCartTotal(): Double = if (canUseCart()) _cartState.value.totalAmount else 0.0

    fun isCurrentUserAdmin(): Boolean = currentUser?.isAdmin ?: false

    private fun getSampleCart(): Cart {
        val sampleItems = listOf(
            CartItem(
                id = "sample_1",
                clothingItem = ClothingItem(
                    id = "polera_1",
                    name = "Polera Básica",
                    description = "Polera cómoda de algodón 100%",
                    price = 15000.0,
                    imageUrl = "polera_basica",
                    imageUrls = listOf("polera_basica", "polera_basica_2", "polera_basica_3"),
                    category = ProductType.POLERAS,
                    sizes = listOf("S", "M", "L", "XL"),
                    stock = 5
                ),
                quantity = 2,
                selectedSize = "M"
            ),
            CartItem(
                id = "sample_2",
                clothingItem = ClothingItem(
                    id = "poleron_1",
                    name = "Polerón con Capucha",
                    description = "Polerón abrigado con capucha",
                    price = 35000.0,
                    imageUrl = "poleron_capucha",
                    imageUrls = listOf("poleron_capucha", "poleron_capucha_2", "poleron_capucha_3"),
                    category = ProductType.POLERONES,
                    sizes = listOf("S", "M", "L", "XL"),
                    stock = 3
                ),
                quantity = 1,
                selectedSize = "L"
            )
        )
        return Cart(items = sampleItems)
    }
}
