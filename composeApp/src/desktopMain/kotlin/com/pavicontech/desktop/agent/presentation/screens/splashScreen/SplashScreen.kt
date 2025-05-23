package com.pavicontech.desktop.agent.presentation.screens.splashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToDashBoard:()->Unit,
    onNavigateToSignIn:()->Unit
) {

    val session: GetUserSessionStatus = koinInject()

    LaunchedEffect(true) {
        val result = session()
        if (result) onNavigateToDashBoard() else onNavigateToSignIn()

    }

    Box(
        modifier = Modifier
            .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    MaterialTheme.colors.background,
                    MaterialTheme.colors.primary,
                ),
                radius = 150f,
                tileMode = TileMode.Clamp
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()

        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Etims",
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.onBackground
                )
                Text(
                    text = "Sync",
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.secondary
                )
            }
        }

    }
}