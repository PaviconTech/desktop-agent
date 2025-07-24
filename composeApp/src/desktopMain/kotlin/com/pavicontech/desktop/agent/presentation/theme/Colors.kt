package com.pavicontech.desktop.agent.presentation.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Modernized color palette
val PaviconColors = lightColors(
    primary = Color(0xFF001F3F),           // Deep Navy Blue
    primaryVariant = Color(0xFF001530),    // Darker Navy for depth

    secondary = Color(0xFF4CAF50),         // Modern Fern Green (Material Green 500)
    secondaryVariant = Color(0xFF388E3C),  // Deeper Forest Green (Material Green 700)

    background = Color(0xFFF5F7F6),        // Soft cool gray with greenish undertone
    surface = Color(0xFFFFFFFF),           // Clean white for cards/dialogs
    error = Color(0xFFD32F2F),             // Material Red 700

    onPrimary = Color(0xFFFFFFFF),         // White text/icons on navy
    onSecondary = Color(0xFFFFFFFF),       // White text/icons on green
    onBackground = Color(0xFF1C2526),      // Dark gray for readability
    onSurface = Color(0xFF1C2526),         // Same as onBackground
    onError = Color(0xFFFFFFFF)            // White text on error
)
