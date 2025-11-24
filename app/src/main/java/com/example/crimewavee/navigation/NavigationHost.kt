package com.example.crimewavee.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.crimewavee.ui.theme.viewmodel.AuthViewModel
import com.example.crimewavee.ui.theme.viewmodel.CartViewModel
import com.example.crimewavee.ui.theme.viewmodel.ClothingViewModel


@Composable
fun NavigationHost(
    onExitApp: () -> Unit = {}
) {

    val clothingViewModel: ClothingViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()


    val navigationState = remember { NavigationState() }
    

    val authState by authViewModel.authState
    val isAuthenticated = authState.isAuthenticated
    val currentUser = authState.currentUser


    LaunchedEffect(currentUser) {
        cartViewModel.setCurrentUser(currentUser)
    }


    val navigationActions = remember(navigationState, isAuthenticated) {
        NavigationActionsImpl(
            navigationState = navigationState,
            isAuthenticated = { isAuthenticated }
        )
    }


    BackHandler {
        navigationActions.navigateBack()
    }


    ScreenNavigator(
        navigationState = navigationState,
        navigationActions = navigationActions,
        clothingViewModel = clothingViewModel,
        authViewModel = authViewModel,
        cartViewModel = cartViewModel,
        isAuthenticated = isAuthenticated,
        currentUser = currentUser
    )


    ExitAppDialog(
        showDialog = navigationState.showExitDialog,
        onDismiss = { navigationActions.hideExitDialog() },
        onConfirm = onExitApp
    )
}