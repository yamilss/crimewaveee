package com.example.crimewavee.data.model

data class User(
    val email: String,
    val password: String,
    val phoneNumber: String? = null,
    val rut: String? = null, // RUT chileno con formato validado
    val isAdmin: Boolean = false,
    val shippingAddress: ShippingAddress? = null,
    val billingAddress: BillingAddress? = null
)


data class AuthState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null
)
