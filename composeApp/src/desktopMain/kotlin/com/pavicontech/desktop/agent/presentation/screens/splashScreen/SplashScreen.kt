package com.pavicontech.desktop.agent.presentation.screens.splashScreen

import androidx.compose.foundation.Image
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
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.taxpoint_black
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
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
        val osName = System.getProperty("os.name")
        val result = session()
        when {
            osName.contains("Windows", ignoreCase = true) -> {
                val isInstallationComplete = keyValueStorage.get(Constants.INSTALLATION_PROCESS_STATUS)?.toBooleanStrictOrNull() ?: false
                if (isInstallationComplete) {
                    if (result) onNavigateToDashBoard() else onNavigateToSignIn()
                }else{
                    onNavigateToInstallation()
                }
            }
            osName.contains("Linux", ignoreCase = true) -> {

                    if (result) onNavigateToDashBoard() else onNavigateToSignIn()

            }
            osName.contains("Mac", ignoreCase = true) -> {
                if (result) onNavigateToDashBoard() else onNavigateToSignIn()

            }
            else -> {

            }
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
                Image(
                    painter = painterResource(Res.drawable.taxpoint_black),
                    contentDescription = "Taxpoint Logo",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

    }
}