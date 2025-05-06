package com.pavicontech.desktop.agent.presentation.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Modernized color palette
val PaviconColors = lightColors(
    primary = Color(0xFF1B3A6B),           // Refined Navy Blue (softer, more approachable)
    primaryVariant = Color(0xFF0F2550),    // Darker Navy for depth
    secondary = Color(0xFFF57C00),         // Vibrant Orange (Material Orange 600)
    secondaryVariant = Color(0xFFEF6C00),  // Slightly deeper orange for accents

    background = Color(0xFFF8FAFC),        // Light blue-gray for modern warmth
    surface = Color(0xFFFFFFFF),           // Clean white for cards and dialogs
    error = Color(0xFFD32F2F),             // Softer red for errors (Material Red 700)

    onPrimary = Color(0xFFFFFFFF),         // White text/icons on navy
    onSecondary = Color(0xFF1C2526),       // Near-black for contrast on orange
    onBackground = Color(0xFF1C2526),      // Dark gray for readability
    onSurface = Color(0xFF1C2526),         // Consistent with onBackground
    onError = Color(0xFFFFFFFF)            // White text on error
)