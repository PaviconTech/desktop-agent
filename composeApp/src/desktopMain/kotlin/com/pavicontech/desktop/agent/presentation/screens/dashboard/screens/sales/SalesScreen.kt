    package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales

    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.lazy.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SalesBody
    import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SalesUpperSection
    import org.koin.compose.koinInject

    @Composable
    fun SalesScreen() {
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            SalesUpperSection()
            SalesBody()
        }
    }
    
    
    
