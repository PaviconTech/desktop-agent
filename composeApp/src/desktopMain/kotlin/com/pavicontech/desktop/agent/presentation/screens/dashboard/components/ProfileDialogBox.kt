package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindow
import com.pavicontech.desktop.agent.domain.model.BusinessInformation



@Composable
fun ProfileDialogBox(
    isDialogVisible: Boolean,
    profile: BusinessInformation?,
    onDismiss: () -> Unit
) {
    if (isDialogVisible && profile != null) {
        AppDialog(
            title = "Business Profile",
            onDismissRequest = onDismiss
        ) {
            Text(
                text = "Business Profile",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            )

            InfoRow(label = "Business Name", value = profile.name)
            InfoRow(label = "Taxpayer Name", value = profile.taxpayerName)
            InfoRow(label = "KRA PIN", value = profile.kraPin)
            InfoRow(label = "Branch", value = "${profile.branchName} (${profile.branchId})")
            InfoRow(label = "District", value = profile.districtName)
            InfoRow(label = "Province", value = profile.provinceName)
            InfoRow(label = "Sector", value = profile.sectorName)
            InfoRow(label = "SDC ID", value = profile.sdcId)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}




@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}






@Composable
fun AppDialog(
    title: String,
    onDismissRequest: () -> Unit,
    width: Dp = 700.dp,
    height: Dp = 700.dp,
    resizable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    DialogWindow(
        onCloseRequest = onDismissRequest,
        title = title,
        resizable = resizable,


    ) {
        Surface(
            modifier = Modifier
                .width(width)
                .height(height)
                .padding(24.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            color = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                content()
            }
        }
    }
}

