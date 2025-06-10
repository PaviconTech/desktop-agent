package com.pavicontech.desktop.agent.presentation.screens.splashScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToDashBoard:()->Unit,
    onNavigateToInstallation:()->Unit,
    onNavigateToSignIn:()->Unit
) {

    val session: GetUserSessionStatus = koinInject()
    val keyValueStorage: KeyValueStorage = koinInject()
    val watchFolder by keyValueStorage.observe(Constants.WATCH_FOLDER).collectAsState(null)


    LaunchedEffect(watchFolder) {
        delay(2000)
        val result = session()
        val isInstallationComplete = keyValueStorage.get(Constants.INSTALLATION_PROCESS_STATUS)?.toBooleanStrictOrNull() ?: false
        if (isInstallationComplete) {
            if (result) onNavigateToDashBoard() else onNavigateToSignIn()
        }else{
            onNavigateToInstallation()
        }


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