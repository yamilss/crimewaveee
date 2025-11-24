package com.example.crimewavee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.crimewavee.navigation.NavigationHost
import com.example.crimewavee.ui.theme.theme.CrimewaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrimewaveTheme {
                NavigationHost(
                    onExitApp = { finish() }
                )

            }
        }
    }
}


