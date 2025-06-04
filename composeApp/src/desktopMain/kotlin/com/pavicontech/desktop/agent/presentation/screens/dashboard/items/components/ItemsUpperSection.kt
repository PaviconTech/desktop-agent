package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ItemsUpperSection(searchQuery:String, onSearchQueryChange: (String) -> Unit) {
    val brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colors.primary,
            MaterialTheme.colors.secondary,
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Items",
            style = MaterialTheme.typography.body1,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8 .dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.secondary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Export",
                        color = MaterialTheme.colors.onSecondary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.secondary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Add Items",
                        color = MaterialTheme.colors.onSecondary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.secondary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Batch Upload",
                        color = MaterialTheme.colors.onSecondary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
            Column {
                Text(
                    text = "Search:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.primary.copy(0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )

                ) {
                    BasicTextField(
                        singleLine = true,
                        value = searchQuery,
                        onValueChange = { onSearchQueryChange(it) },
                        modifier = Modifier
                            .width(300.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.CenterStart)
                    )
                }
            }
        }
    }
}