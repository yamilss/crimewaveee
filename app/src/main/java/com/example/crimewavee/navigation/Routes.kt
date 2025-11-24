package com.example.crimewavee.navigation


object Routes {
    // Pantallas de autenticación
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // Pantalla principal
    const val HOME = "home"
    
    // Pantallas de perfil y configuración
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val EDIT_DETAILS = "editDetails"
    
    // Pantallas de productos
    const val DETAILS = "details"
    const val REPORT = "report" // Agregar productos (admin)
    
    // Pantallas de administración
    const val STATS = "stats" // Panel de empleado/admin
    
    // Pantallas de carrito y compras
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDER_RESULT = "orderResult"
    
    // Pantallas de direcciones
    const val SHIPPING_ADDRESS = "shippingAddress"
    const val BILLING_ADDRESS = "billingAddress"

    // Pantallas de gestión
    const val PRODUCT_MANAGEMENT = "productManagement"
    const val CRIME_NEWS = "crimeNews"
    const val VIEW_SHIPPING_ADDRESS = "viewShippingAddress"
    const val VIEW_BILLING_ADDRESS = "viewBillingAddress"
}