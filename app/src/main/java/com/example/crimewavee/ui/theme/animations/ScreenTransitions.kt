package com.example.crimewavee.ui.theme.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun AnimatedScreenTransition(
    targetScreen: String,
    @Suppress("UNUSED_PARAMETER") previousScreen: String,
    content: @Composable () -> Unit
) {
    
    AnimatedContent(
        targetState = targetScreen,
        transitionSpec = {
            
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        modifier = Modifier.fillMaxSize(),
        label = "slide_transition",
        content = { screenState ->
           
            screenState 
            content()
        }
    )
}
