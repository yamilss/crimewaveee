package com.example.crimewavee.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NavigationState {
    var currentScreen by mutableStateOf(Routes.LOGIN)
        private set
    
    var previousScreen by mutableStateOf(Routes.LOGIN)
        private set
    
    private var _selectedItemId by mutableStateOf("")
    val selectedItemId: String get() = _selectedItemId
    
    var navigationStack by mutableStateOf(listOf<String>())
        private set
    
    var showExitDialog by mutableStateOf(false)
        private set


    fun navigateToScreen(screen: String) {
        if (currentScreen != screen) {
            navigationStack = navigationStack + currentScreen
            previousScreen = currentScreen
            currentScreen = screen
        }
    }


    fun navigateBack(isAuthenticated: Boolean, onShowExitDialog: () -> Unit) {
        when {
            navigationStack.isNotEmpty() -> {
                previousScreen = currentScreen
                currentScreen = navigationStack.lastOrNull() ?: Routes.HOME
                navigationStack = navigationStack.dropLast(1)
            }
            currentScreen == Routes.HOME -> {
                onShowExitDialog()
            }
            currentScreen == Routes.LOGIN || currentScreen == Routes.REGISTER -> {
                onShowExitDialog()
            }
            isAuthenticated -> {
                previousScreen = currentScreen
                currentScreen = Routes.HOME
            }
            else -> {
                previousScreen = currentScreen
                currentScreen = Routes.LOGIN
            }
        }
    }


    fun setSelectedItemId(itemId: String) {
        _selectedItemId = itemId
    }

   
    fun showExitDialog() {
        showExitDialog = true
    }

   
    fun hideExitDialog() {
        showExitDialog = false
    }

  
    fun navigateDirectly(screen: String) {
        previousScreen = currentScreen
        currentScreen = screen
    }


    fun clearNavigationStack() {
        navigationStack = emptyList()
    }

   
    fun reset() {
        navigationStack = emptyList()
        previousScreen = currentScreen
        currentScreen = Routes.LOGIN
        _selectedItemId = ""
        showExitDialog = false
    }
}