package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

/*
 * ────────────────────────────────────────────────────────────────
 *  Add New Item Dialog (Compose Desktop)
 * ────────────────────────────────────────────────────────────────
 *  • Search‑as‑you‑type dropdowns (arrow keys + Enter friendly)
 *  • Enter moves focus to the next input for rapid data entry
 *  • Numeric‑only / decimal‑only enforcement on stock & prices
 *  • Rounded 12 dp surface, roomy paddings, Material‑friendly
 *  • Strong‑typed output via `Item` data class
 * ────────────────────────────────────────────────────────────────
 */

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import com.pavicontech.desktop.agent.data.remote.dto.request.AddItemReq
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.Dtl
import com.pavicontech.desktop.agent.domain.usecase.items.AddItemUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetClassificationCodesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemCodesListUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.items.AddItemState
import kotlin.invoke

/* ───────────────────── Dialog Entry ───────────────────── */
@Composable
fun AddNewItemDialog(
    isLoading: Boolean,
    isShown: Boolean,
    onDismissRequest: () -> Unit,
    onRefresh: () -> Unit,
) {
    if (!isShown) return

    DialogWindow(
        onCloseRequest = onDismissRequest,
        state = rememberDialogState(width = 1200.dp, height = 850.dp),
        title = "Add New Item",
        resizable = true,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            color = MaterialTheme.colors.surface
        ) {
            AddNewItemForm(
                onAddItem = { /* Item submission logic */ },
                onCancel = onDismissRequest,
                isLoading = isLoading,
                onRefresh = onRefresh
            )
        }
    }
}

/* ────────────────────── Form Body ─────────────────────── */
@Composable
private fun AddNewItemForm(
    isLoading: Boolean,
    onAddItem: (AddItemReq) -> Unit,
    onCancel: () -> Unit,
    onRefresh: () -> Unit,
) {
    /* Inject use‑cases */
    val getCodes: GetItemCodesListUseCase = koinInject()
    val getClassCodes: GetClassificationCodesUseCase = koinInject()
    val scope = rememberCoroutineScope()

    val addItem: AddItemUseCase = koinInject()
    var addingState by remember { mutableStateOf(AddItemState()) }
    var formHasBeenSubmitted by remember { mutableStateOf(false) }

    /* Remote data lists */
    val classifications = remember { mutableStateListOf<ClassificationCode>() }
    val taxTypes = remember { mutableStateListOf<Dtl>() }
    val countries = remember { mutableStateListOf<Dtl>() }
    val quantityUnits = remember { mutableStateListOf<Dtl>() }
    val packagingUnits = remember { mutableStateListOf<Dtl>() }
    val itemTypes = remember { mutableStateListOf<Dtl>() }

    /* Fetch once */
    LaunchedEffect(Unit) {
        scope.launch {
            getCodes()?.let { res ->
                fun pull(name: String) =
                    res.clsList.firstOrNull { it.cdClsNm == name }?.dtlList ?: emptyList()
                taxTypes += pull("Taxation Type").distinctBy(Dtl::cd)
                countries += pull("Country").distinctBy(Dtl::cd)
                quantityUnits += pull("Quantity Unit").distinctBy(Dtl::cd)
                packagingUnits += pull("Packing Unit").distinctBy(Dtl::cd)
                itemTypes += pull("Item Type").distinctBy(Dtl::cd)
            }
        }
        scope.launch { getClassCodes()?.let { classifications += it } }
    }

    /* Local state */
    var itemName by remember { mutableStateOf("") }
    var itemCode by remember { mutableStateOf("") }
    var initialStock by remember { mutableStateOf("") }
    var unitPriceRetail by remember { mutableStateOf("") }
    var unitPriceWholesale by remember { mutableStateOf("") }
    var maxStock by remember { mutableStateOf("") }
    var reorderLevel by remember { mutableStateOf("") }
    var lowStockLevel by remember { mutableStateOf("") }

    var selClassification by remember { mutableStateOf<ClassificationCode?>(null) }
    var selCountry by remember { mutableStateOf<Dtl?>(null) }
    var selItemType by remember { mutableStateOf<Dtl?>(null) }
    var selTaxType by remember { mutableStateOf<Dtl?>(null) }
    var selPackagingUnit by remember { mutableStateOf<Dtl?>(null) }
    var selQuantityUnit by remember { mutableStateOf<Dtl?>(null) }

    /* Focus helper */
    val focus = LocalFocusManager.current
    fun Modifier.onEnterNext() = onKeyEvent { key ->
        if (key.key == Key.Enter) {
            focus.moveFocus(FocusDirection.Next); true
        } else false
    }

    /* Validation */
    val requiredFieldsValid = itemName.isNotBlank() && 
                             selClassification != null && 
                             selCountry != null && 
                             selItemType != null && 
                             selTaxType != null && 
                             selPackagingUnit != null && 
                             selQuantityUnit != null

    /* UI */
    Column(
        Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header with title and description
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Create New Item",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.primary
                )
            )
            Text(
                "Add a new product or service to your inventory",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary
            )
            Divider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )
        }

        // Form content in a Card for better visual grouping
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section: Basic Information
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Basic Information",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    /* Row 1 */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name *") },
                            singleLine = true,
                            isError = formHasBeenSubmitted && itemName.isBlank(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                unfocusedBorderColor = if (formHasBeenSubmitted && itemName.isBlank()) 
                                    MaterialTheme.colors.error else MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                                errorBorderColor = MaterialTheme.colors.error
                            ),
                            modifier = Modifier.weight(1f).onEnterNext()
                        )
                        SearchableDropdown(
                            selected = selItemType,
                            label = "Item Type *",
                            options = itemTypes,
                            optionLabel = { it.cdNm },
                            onSelect = { selItemType = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    /* Row 2 */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SearchableDropdown(
                            selected = selClassification,
                            label = "Classification *",
                            options = classifications,
                            optionLabel = { it.itemClsNm },
                            onSelect = { selClassification = it },
                            modifier = Modifier.weight(1f)
                        )
                        SearchableDropdown(
                            selected = selCountry,
                            label = "Country of Origin *",
                            options = countries,
                            optionLabel = { it.cdNm },
                            onSelect = { selCountry = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section: Tax and Units
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Tax and Units",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    /* Row 3 */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SearchableDropdown(
                            selected = selTaxType,
                            label = "Tax Type *",
                            options = taxTypes,
                            optionLabel = { it.cdNm },
                            onSelect = { selTaxType = it },
                            modifier = Modifier.weight(1f)
                        )
                        SearchableDropdown(
                            selected = selPackagingUnit,
                            label = "Packaging Unit *",
                            options = packagingUnits,
                            optionLabel = { it.cdNm },
                            onSelect = { selPackagingUnit = it },
                            modifier = Modifier.weight(1f)
                        )
                        SearchableDropdown(
                            selected = selQuantityUnit,
                            label = "Quantity Unit *",
                            options = quantityUnits,
                            optionLabel = { it.cdNm },
                            onSelect = { selQuantityUnit = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section: Inventory Details
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Inventory Details",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(Modifier.height(4.dp))

                    /* Row 4 */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = itemCode,
                            onValueChange = { itemCode = it },
                            label = { Text("Barcode") },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.weight(1f).onEnterNext()
                        )
                        OutlinedTextField(
                            value = initialStock,
                            onValueChange = { initialStock = it.filter(Char::isDigit) },
                            label = { Text("Initial Stock*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.weight(1f).onEnterNext()
                        )
                    }

                    /* Row 5 */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = unitPriceRetail,
                            onValueChange = { unitPriceRetail = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Price (Retail)*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.weight(1f).onEnterNext()
                        )

                        // Added wholesale price field
                        OutlinedTextField(
                            value = unitPriceWholesale,
                            onValueChange = { unitPriceWholesale = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Price (Wholesale)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.weight(1f).onEnterNext()
                        )
                    }
                }
            }
        }

        // Required fields note
        if (formHasBeenSubmitted && !requiredFieldsValid) {
            Text(
                "Please fill in all required fields marked with *",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Success message
        if (addingState.isSuccessful) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Item added successfully!",
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Cancel")
            }

            // Add Item button with loading state
            if (addingState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Button(
                    enabled = requiredFieldsValid  && initialStock.isNotBlank() && unitPriceRetail.isNotBlank(),
                    onClick = {
                        formHasBeenSubmitted = true

                        if (requiredFieldsValid) {
                            scope.launch {
                                addingState = AddItemState(isLoading = true)
                                val res = addItem.invoke(
                                    AddItemReq(
                                        itemName = itemName,
                                        itemClassificationCode = selClassification?.itemClsCd ?: "",
                                        itemStdNm = itemName,
                                        itemType = selItemType?.cd ?: "",
                                        originCountry = selCountry?.cd ?: "",
                                        packagingUnit = selPackagingUnit?.cd ?: "",
                                        quantityUnit = selQuantityUnit?.cd ?: "",
                                        taxCode = selTaxType?.cd ?: "",
                                        price = unitPriceRetail,
                                        currentStock = initialStock.toIntOrNull() ?: 0
                                    )
                                )
                                addingState = AddItemState(isLoading = false, isSuccessful = res?.status ?: false)
                                onRefresh()
                            }
                        }
                    },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    )
                ) {
                    Text("Add Item")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SearchableDropdown(
    selected: T?,
    label: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var userIsTyping by remember { mutableStateOf(false) }
    var highlightedIndex by remember { mutableStateOf(0) }
    val focus = LocalFocusManager.current

    // Filter options as you type
    val filteredOptions = remember(query, options) {
        if (query.isBlank()) options
        else options.filter { optionLabel(it).contains(query, ignoreCase = true) }
    }

    // Reset highlighted index when filtered options change
    LaunchedEffect(filteredOptions) {
        highlightedIndex = 0
    }

    // Show query if typing, or selected item label
    val displayText = if (userIsTyping) query else selected?.let(optionLabel) ?: ""

    // Required field indicator
    val isRequired = label.contains("*")
    val labelColor = if (selected == null && isRequired) 
        MaterialTheme.colors.error.copy(alpha = 0.7f) else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)

    Box(modifier = modifier) {
        Column {
            // Text field with dropdown icon
            OutlinedTextField(
                value = displayText,
                onValueChange = {
                    query = it
                    userIsTyping = true
                    expanded = true
                },
                label = { 
                    Text(text = label)
                },
                trailingIcon = { 
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown, 
                            contentDescription = "Toggle dropdown",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            expanded = true
                            userIsTyping = true
                            if (query.isEmpty() && selected != null) {
                                query = optionLabel(selected)
                            }
                        }
                    }
                    .onKeyEvent { keyEvent ->
                        when (keyEvent.key) {
                            Key.Escape -> {
                                expanded = false
                                true
                            }
                            Key.Enter -> {
                                if (expanded && filteredOptions.isNotEmpty() && highlightedIndex >= 0 && highlightedIndex < filteredOptions.size) {
                                    val selectedItem = filteredOptions[highlightedIndex]
                                    onSelect(selectedItem)
                                    query = optionLabel(selectedItem)
                                    userIsTyping = false
                                    expanded = false
                                    focus.moveFocus(FocusDirection.Next)
                                    true
                                } else {
                                    focus.moveFocus(FocusDirection.Next)
                                    true
                                }
                            }
                            Key.DirectionDown -> {
                                if (expanded) {
                                    highlightedIndex = (highlightedIndex + 1).coerceAtMost(filteredOptions.size - 1)
                                    true
                                } else false
                            }
                            Key.DirectionUp -> {
                                if (expanded) {
                                    highlightedIndex = (highlightedIndex - 1).coerceAtLeast(0)
                                    true
                                } else false
                            }
                            else -> false
                        }
                    },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = MaterialTheme.colors.primary,
                    cursorColor = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.surface
                )
            )

            // Dropdown with position information
            if (expanded) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                    ) {
                        if (filteredOptions.isEmpty()) {
                            item {
                                Text(
                                    "No results found", 
                                    modifier = Modifier.padding(16.dp), 
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        } else {
                            itemsIndexed(filteredOptions) { index, item ->
                                val isHighlighted = index == highlightedIndex
                                val isSelected = selected == item

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelect(item)
                                            query = optionLabel(item)
                                            userIsTyping = false
                                            expanded = false
                                            focus.moveFocus(FocusDirection.Next)
                                        },
                                    color = when {
                                        isHighlighted && isSelected -> MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                        isHighlighted -> MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                        isSelected -> MaterialTheme.colors.primary.copy(alpha = 0.12f)
                                        else -> MaterialTheme.colors.surface
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Index number with better styling
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.body2,
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                        // Item text
                                        Text(
                                            text = optionLabel(item),
                                            style = MaterialTheme.typography.body1,
                                            fontWeight = if (isHighlighted || isSelected) FontWeight.Medium else FontWeight.Normal
                                        )
                                    }
                                }

                                // Add a subtle divider between items
                                if (index < filteredOptions.size - 1) {
                                    Divider(
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
                                        thickness = 1.dp
                                    )
                                }
                            }

                            // Add an empty item at the end for better scrolling experience
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


/* ──────────────────── Data Class ─────────────────────── */

data class Item(
    val name: String,
    val classificationCode: String,
    val countryOfOrigin: String,
    val itemType: String,
    val taxCode: String,
    val packagingType: String,
    val quantityUnit: String,
    val code: String,
    val initialStock: Int,
    val unitPriceRetail: Double,
    val unitPriceWholesale: Double,
    val maxStock: Int,
    val reorderLevel: Int,
    val lowStockLevel: Int
)
