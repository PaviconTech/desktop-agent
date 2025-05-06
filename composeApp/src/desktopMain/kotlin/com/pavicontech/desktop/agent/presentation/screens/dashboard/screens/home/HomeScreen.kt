package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.home


import androidx.compose.foundation.lazy.*

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeScreenViewModel = koinViewModel()
    val listState = rememberLazyListState()

}


