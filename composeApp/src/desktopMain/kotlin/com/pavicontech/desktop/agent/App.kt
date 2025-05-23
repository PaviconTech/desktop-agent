package com.pavicontech.desktop.agent


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.pavicontech.desktop.agent.presentation.helper.ObserveAsEvents
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.navigation.graphs.MainNavGraph
import com.pavicontech.desktop.agent.presentation.screens.signIn.components.SignInScreen
import com.pavicontech.desktop.agent.presentation.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

import kotlinx.coroutines.launch

@Composable
@Preview
fun App(width: Dp, height: Dp) {
    val navHostController = rememberNavController()

    AppTheme {
        val screenSize = LocalDensity.current.run {
            LocalDensity.current.density
        }
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        val scope = rememberCoroutineScope()
        ObserveAsEvents(
            flow = SnackbarController.events,
            snackbarHostState
        ) { event ->
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()

                val result = snackbarHostState.showSnackbar(
                    message = event.message,
                    actionLabel = event.action?.name,
                    duration = SnackbarDuration.Long
                )

                if (result == SnackbarResult.ActionPerformed) {
                    event.action?.action?.invoke()
                }
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth(0.2f)
                        .offset(
                            x = width-380.dp , y = -(height-100.dp)
                        )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            MainNavGraph(navHostController)
        }
    }
}