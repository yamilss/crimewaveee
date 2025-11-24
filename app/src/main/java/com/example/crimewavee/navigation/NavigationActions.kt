package com.example.crimewavee.navigation


interface NavigationActions {
    // Navegación básica
    fun navigateBack()
    fun navigateToHome()
    
    // Autenticación
    fun navigateToLogin()
    fun navigateToRegister()
    
    // Perfil y configuración
    fun navigateToProfile()
    fun navigateToSettings()
    fun navigateToEditDetails()
    
    // Productos
    fun navigateToDetails(itemId: String)
    fun navigateToReport()
    
    // Administración
    fun navigateToStats()
    
    // Carrito y compras
    fun navigateToCart()
    fun navigateToCheckout()
    fun navigateToOrderResult()
    
    // Direcciones
    fun navigateToShippingAddress()
    fun navigateToBillingAddress()
    fun navigateToViewShippingAddress()
    fun navigateToViewBillingAddress()
    
    // Control de aplicación
    fun showExitDialog()
    fun hideExitDialog()
}


class NavigationActionsImpl(
    private val navigationState: NavigationState,
    private val isAuthenticated: () -> Boolean
) : NavigationActions {
    
    override fun navigateBack() {
        navigationState.navigateBack(isAuthenticated()) {
            navigationState.showExitDialog()
        }
    }
    
    override fun navigateToHome() {
        navigationState.clearNavigationStack()
        navigationState.navigateDirectly(Routes.HOME)
    }
    
    override fun navigateToLogin() {
        navigationState.navigateToScreen(Routes.LOGIN)
    }
    
    override fun navigateToRegister() {
        navigationState.navigateToScreen(Routes.REGISTER)
    }
    
    override fun navigateToProfile() {
        navigationState.navigateToScreen(Routes.PROFILE)
    }
    
    override fun navigateToSettings() {
        navigationState.navigateToScreen(Routes.SETTINGS)
    }
    
    override fun navigateToEditDetails() {
        navigationState.navigateToScreen(Routes.EDIT_DETAILS)
    }
    
    override fun navigateToDetails(itemId: String) {
        if (itemId.isNotBlank()) {
            navigationState.setSelectedItemId(itemId)
            navigationState.navigateToScreen(Routes.DETAILS)
        }
    }
    
    override fun navigateToReport() {
        navigationState.navigateToScreen(Routes.REPORT)
    }
    
    override fun navigateToStats() {
        navigationState.navigateToScreen(Routes.STATS)
    }
    
    override fun navigateToCart() {
        navigationState.navigateToScreen(Routes.CART)
    }
    
    override fun navigateToCheckout() {
        navigationState.navigateToScreen(Routes.CHECKOUT)
    }
    
    override fun navigateToOrderResult() {
        navigationState.navigateToScreen(Routes.ORDER_RESULT)
    }
    
    override fun navigateToShippingAddress() {
        navigationState.navigateToScreen(Routes.SHIPPING_ADDRESS)
    }
    
    override fun navigateToBillingAddress() {
        navigationState.navigateToScreen(Routes.BILLING_ADDRESS)
    }
    
    override fun navigateToViewShippingAddress() {
        navigationState.navigateToScreen(Routes.VIEW_SHIPPING_ADDRESS)
    }
    
    override fun navigateToViewBillingAddress() {
        navigationState.navigateToScreen(Routes.VIEW_BILLING_ADDRESS)
    }
    
    override fun showExitDialog() {
        navigationState.showExitDialog()
    }
    
    override fun hideExitDialog() {
        navigationState.hideExitDialog()
    }
}