package com.pavicontech.desktop.agent.presentation.helper

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val color: Color? = null,
    val message: String,
    val action: SnackbarAction? = null
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)

object SnackbarController {

    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    // Store the current event for reference
    private var currentEvent: SnackbarEvent? = null

    suspend fun sendEvent(event: SnackbarEvent) {
        currentEvent = event
        _events.send(event)
    }

    // Get the current event (may be null if no event has been sent)
    fun getCurrentEvent(): SnackbarEvent? {
        return currentEvent
    }

    fun getEventColor(message: String): Color {
        return when {
            message.contains("error", ignoreCase = true) || 
            message.contains("fail", ignoreCase = true) -> Color.Red

            message.contains("success", ignoreCase = true) || 
            message.contains("successful", ignoreCase = true) -> Color(0xFF4CAF50) // Green

            message.contains("warning", ignoreCase = true) || 
            message.contains("warn", ignoreCase = true) -> Color(0xFFFFC107) // Amber

            else -> Color(0xFF2196F3) // Blue as default
        }
    }
}
