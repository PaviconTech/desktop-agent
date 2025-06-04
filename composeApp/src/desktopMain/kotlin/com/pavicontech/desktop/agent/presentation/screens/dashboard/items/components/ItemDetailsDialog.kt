package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item


@Composable
fun ItemDetailsDialog(
    item: Item,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Use mutableStateOf for editable fields (simplified for demo)
    var itemName by remember { mutableStateOf(item.itemName) }
    var itemClassification by remember { mutableStateOf(item.itemClassificationCode) }
    var itemType by remember { mutableStateOf(item.itemClassificationCode) }
    var originCountry by remember { mutableStateOf(item.originCountry) }
    var packagingType by remember { mutableStateOf(item.packagingUnit) }
    var quantityType by remember { mutableStateOf(item.quantityUnit) }
    var taxCode by remember { mutableStateOf(item.taxCode) }
    var barCode by remember { mutableStateOf(item.barcode ?: "") }
    var productCategory by remember { mutableStateOf(item.itemCategory.category ?: "") }
    var price by remember { mutableStateOf(item.price) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .widthIn(min = 300.dp, max = 800.dp)
                .padding(16.dp),
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Item Details",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Editable Grid Layout
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                       Row(
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.SpaceBetween,
                           modifier = Modifier.fillMaxWidth()
                       ) {
                           EditableField("Item Name", itemName) { itemName = it }
                           EditableField("Item Classification", itemClassification) { itemClassification = it }
                       }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EditableField("Item Type", itemType) { itemType = it }
                        EditableField("Contry of Origin", originCountry) { originCountry = it }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EditableField("Packaging Type", packagingType) { packagingType = it }
                        EditableField("Quantity Type", quantityType) { quantityType = it }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EditableField("Tax Code", taxCode) { taxCode = it }
                        EditableField("Bar Code", barCode) { barCode = it }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EditableField("Product Category", productCategory) { productCategory = it }
                        EditableField("Unit Price", price) { price = it }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { /* Save changes here */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Edit Item")
                }
            }
        }
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.width(240.dp)) {
        Text(text = label, style = MaterialTheme.typography.caption)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.05f)
            )
        )
    }
}



