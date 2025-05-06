package com.pavicontech.desktop.agent.presentation.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.*

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ripple.createRippleModifierNode
import androidx.compose.runtime.*

@Composable
fun rippleOrFallbackImplementation(): Indication = rememberUpdatedState(
    LocalIndication.current
).value

@Composable
fun rememberTextSelectionColors(colors: Colors): TextSelectionColors {
    return remember(colors) {
        TextSelectionColors(
            handleColor = colors.primary,
            backgroundColor = colors.primary.copy(alpha = 0.4f)
        )
    }
}


@Composable
fun AppTheme(
    colors: Colors = PaviconColors,
    typography: Typography = PaviconTypography,
    shapes: Shapes = PaviconShapes,
    content: @Composable () -> Unit
) {
    val selectionColors = rememberTextSelectionColors(colors)
    val ripple = rippleOrFallbackImplementation()

    CompositionLocalProvider(
        LocalTextSelectionColors provides selectionColors,
        LocalIndication provides ripple
    ) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}