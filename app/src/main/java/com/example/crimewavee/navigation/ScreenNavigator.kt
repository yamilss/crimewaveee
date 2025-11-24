package com.example.crimewavee.navigation

import androidx.compose.runtime.Composable
import com.example.crimewavee.data.model.User
import com.example.crimewavee.ui.theme.animations.AnimatedScreenTransition
import com.example.crimewavee.ui.theme.screens.*
import com.example.crimewavee.ui.theme.screens.DetailsScreen
import com.example.crimewavee.ui.theme.viewmodel.AuthViewModel
import com.example.crimewavee.ui.theme.viewmodel.CartViewModel
import com.example.crimewavee.ui.theme.viewmodel.ClothingViewModel


@Composable
fun ScreenNavigator(
    navigationState: NavigationState,
    navigationActions: NavigationActions,
    clothingViewModel: ClothingViewModel,
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    isAuthenticated: Boolean,
    currentUser: User?
) {
    
    if (navigationState.currentScreen == Routes.DETAILS && navigationState.selectedItemId.isBlank()) {
        navigationActions.navigateToHome()
        return
    }

    AnimatedScreenTransition(
        targetScreen = navigationState.currentScreen,
        previousScreen = navigationState.previousScreen
    ) {
        when (navigationState.currentScreen) {
            Routes.LOGIN -> {
                if (isAuthenticated) {
                    navigationActions.navigateToHome()
                } else {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { navigationActions.navigateToHome() },
                        onNavigateToRegister = { navigationActions.navigateToRegister() },
                        onBackPressed = { navigationActions.navigateBack() }
                    )
                }
            }

            Routes.REGISTER -> {
                if (isAuthenticated) {
                    navigationActions.navigateToHome()
                } else {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = { navigationActions.navigateToHome() },
                        onNavigateToLogin = { navigationActions.navigateBack() },
                        onBackPressed = { navigationActions.navigateBack() }
                    )
                }
            }

            Routes.HOME -> {
                if (!isAuthenticated) {
                    navigationActions.navigateToLogin()
                } else {
                    HomeScreen(
                        clothingViewModel = clothingViewModel,
                        cartViewModel = cartViewModel,
                        onNavigateToProfile = { navigationActions.navigateToProfile() },
                        onNavigateToDetails = { itemId -> navigationActions.navigateToDetails(itemId) },
                        onNavigateToReport = { navigationActions.navigateToReport() },
                        onNavigateToCart = { navigationActions.navigateToCart() },
                        isAdmin = currentUser?.isAdmin ?: false
                    )
                }
            }

            Routes.PROFILE -> {
                ProfileScreen(
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToSettings = { navigationActions.navigateToSettings() },
                    onNavigateToStats = {
                        if (currentUser?.isAdmin == true) {
                            navigationActions.navigateToStats()
                        }
                    },
                    onNavigateToEditDetails = { navigationActions.navigateToEditDetails() },
                    onNavigateToReport = { navigationActions.navigateToReport() },
                    onNavigateToShippingAddress = { navigationActions.navigateToViewShippingAddress() },
                    onNavigateToBillingAddress = { navigationActions.navigateToViewBillingAddress() },
                    onLogout = {
                        authViewModel.logout()
                        navigationState.reset()
                    },
                    userEmail = currentUser?.email ?: "",
                    userPhone = currentUser?.phoneNumber,
                    isAdmin = currentUser?.isAdmin ?: false,
                    clothingViewModel = clothingViewModel
                )
            }

            Routes.SETTINGS -> {
                SettingsScreen(
                    onNavigateBack = { navigationActions.navigateBack() }
                )
            }

            Routes.DETAILS -> {
                DetailsScreen(
                    itemId = navigationState.selectedItemId,
                    clothingViewModel = clothingViewModel,
                    cartViewModel = cartViewModel,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToCart = { navigationActions.navigateToCart() }
                )
            }

            Routes.REPORT -> {
                ReportScreen(
                    clothingViewModel = clothingViewModel,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onReportSubmitted = { navigationActions.navigateToHome() }
                )
            }

            Routes.STATS -> {
                if (currentUser?.isAdmin == true) {
                    EmployeePanelScreen(
                        clothingViewModel = clothingViewModel,
                        onNavigateBack = { navigationActions.navigateBack() },
                        onNavigateToAddProduct = { navigationActions.navigateToReport() }
                    )
                } else {
                    navigationActions.navigateToHome()
                }
            }

            Routes.EDIT_DETAILS -> {
                EditDetailsScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onUpdateSuccess = { navigationActions.navigateBack() },
                    currentEmail = currentUser?.email ?: "",
                    currentPhone = currentUser?.phoneNumber
                )
            }

            Routes.VIEW_SHIPPING_ADDRESS -> {
                ViewShippingAddressScreen(
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToAdd = { navigationActions.navigateToShippingAddress() },
                    authViewModel = authViewModel
                )
            }

            Routes.VIEW_BILLING_ADDRESS -> {
                ViewBillingAddressScreen(
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToAdd = { navigationActions.navigateToBillingAddress() },
                    authViewModel = authViewModel
                )
            }

            Routes.SHIPPING_ADDRESS -> {
                ShippingAddressScreen(
                    onNavigateBack = { navigationActions.navigateBack() },
                    authViewModel = authViewModel
                )
            }

            Routes.BILLING_ADDRESS -> {
                BillingAddressScreen(
                    onNavigateBack = { navigationActions.navigateBack() },
                    authViewModel = authViewModel
                )
            }

            Routes.CART -> {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onNavigateToCheckout = { navigationActions.navigateToCheckout() }
                )
            }

            Routes.CHECKOUT -> {
                CheckoutScreen(
                    cartViewModel = cartViewModel,
                    onNavigateBack = { navigationActions.navigateBack() },
                    onOrderProcessed = { navigationActions.navigateToOrderResult() }
                )
            }

            Routes.ORDER_RESULT -> {
                OrderResultScreen(
                    cartViewModel = cartViewModel,
                    onNavigateToHome = { navigationActions.navigateToHome() },
                    onNavigateToCart = { navigationActions.navigateBack() }
                )
            }

            else -> {
                
                if (isAuthenticated) {
                    navigationActions.navigateToHome()
                } else {
                    navigationActions.navigateToLogin()
                }
            }
        }
    }
}