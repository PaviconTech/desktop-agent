package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components

import RetryInvoicingUseCase
import ShareInvoiceUseCase
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.fileSystem.Directory
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.domain.usecase.invoices.GetFilteredInvoicesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.DeleteInvoiceUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.loadPdfFirstPageAsImage
import io.github.vinceglb.filekit.utils.toFile
import io.ktor.network.tls.CipherSuite
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.path.pathString


@Composable
fun InvoiceRow(
    index: Int, invoice: Invoice, onRefresh: () -> Unit, modifier: Modifier = Modifier
) {
    val deleteInvoiceUseCase: DeleteInvoiceUseCase = koinInject()
    val shareInvoiceUseCase:ShareInvoiceUseCase = koinInject()
    val getItemsUseCase: GetItemsUseCase = koinInject()
    val extractionColor = when (invoice.extractionStatus) {
        ExtractionStatus.SUCCESSFUL -> Color(0xFF4CAF50)
        ExtractionStatus.FAILED -> Color(0xFFF44336)
        ExtractionStatus.PENDING -> Color(0xFFFFC107)
    }

    val etimsColor = when (invoice.etimsStatus) {
        EtimsStatus.SUCCESSFUL -> Color(0xFF4CAF50)
        EtimsStatus.FAILED -> Color(0xFFF44336)
        EtimsStatus.PENDING -> Color(0xFFFFC107)
        else -> Color(0xFFFFC107)
    }

    val createdDate = invoice.createdAt.toLocalFormattedString()
    val updatedDate = invoice.updatedAt?.toLocalFormattedString() ?: "-"
    val keyValueStorage: KeyValueStorage = koinInject()
    val retryInvoice: RetryInvoicingUseCase = koinInject()
    val getFilteredInvoices: GetFilteredInvoicesUseCase = koinInject()
    val scope = rememberCoroutineScope()


    var showMoreItems by remember { mutableStateOf(false) }
    var isDialogBoxVisible by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var selectedPath by remember { mutableStateOf<String?>(null) }
    var watchFolder: String? by remember { mutableStateOf(null) }
    val selectedFilter by keyValueStorage.observe(Constants.INVOICE_STATUS_FILTER).collectAsState("ALL")

    LaunchedEffect(Unit) {
        watchFolder = keyValueStorage.get(Constants.WATCH_FOLDER) ?: ""
    }

    var isLoading by remember { mutableStateOf(false) }


    ShowInvoice(
        file = selectedFile, showDialogBox = isDialogBoxVisible, onDismiss = {
            isDialogBoxVisible = false
            selectedFile = null
        })

    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 1. Position
                Text(
                    text = "#$index",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .width(50.dp)

                )

                // 2. File name
                Text(
                    text = invoice.fileName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(0.2f).width(300.dp),
                    maxLines = 1
                )

                // 3. Extraction Status
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .width(400.dp)
                        .padding(end = 8.dp)
                        //.weight(0.35f)
                ) {
                    StatusBadge(
                        "Extraction",
                        invoice.extractionStatus.name,
                        extractionColor,
                        viewInvoice = watchFolder != null,
                        onViewInvoice = {
                            val path = watchFolder?.let { Path(it, invoice.fileName.replaceAfterLast('.', "pdf")) }

                            selectedFile = path?.toFile()
                            isDialogBoxVisible = true
                        })
                    Spacer(Modifier.width(4.dp))

                    // 4. ETIMS Status
                    StatusBadge(
                        "ETIMS",
                        invoice.etimsStatus?.name ?: "",
                        etimsColor,
                        viewInvoice = invoice.etimsStatus == EtimsStatus.SUCCESSFUL,
                        onViewInvoice = {

                            selectedFile =
                                Path(Constants.FISCALIZED_RECEIPTS_PATH.pathString, invoice.fileName).toFile()
                            isDialogBoxVisible = true
                        },
                        isRetryLoading = isLoading,
                        onRetry = {
                            val path = watchFolder?.let { Path(it, invoice.fileName) }
                            scope.launch {
                                retryInvoice(
                                    file = Directory(
                                    fullDirectory = path?.toFile()?.path ?: "",
                                    path = Constants.WATCH_FOLDER,
                                    fileName = invoice.fileName
                                ), isLoading = { isLoading = it }, onSuccess = {
                                    onRefresh()
                                }, onError = {})
                            }
                        })
                }

                // 5. Items
                Column(
                    modifier = Modifier.weight(0.2f)
                ) {

                    if (!showMoreItems) {
                        if (invoice.items.isNotEmpty()) {
                            val it = invoice.items.first()
                            Column(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = """
                                        ${it.itemDescription}
                                        Qty ${it.quantity} • KES ${it.amount}
                                    """.trimIndent(), fontSize = 12.sp, modifier = Modifier.fillMaxWidth()

                                )
                                TextButton(
                                    onClick = { showMoreItems = !showMoreItems },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        backgroundColor = Color.Transparent
                                    )
                                ) {
                                    if (invoice.items.size > 1) {
                                        Text("+${invoice.items.size - 1}more")
                                    }
                                }
                            }
                        }

                    }
                    if (showMoreItems) {
                        Column {
                            invoice.items.forEach {
                                Text(
                                    text = "${it.itemDescription} • Qty ${it.quantity} • KES ${it.amount}",
                                    fontSize = 12.sp,
                                    modifier = Modifier.width(300.dp)
                                )
                            }
                            TextButton(
                                onClick = { showMoreItems = !showMoreItems },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = Color.Transparent
                                )
                            ) {
                                Text("show less")
                            }
                        }
                    }
                }

                // 6. Total Price
                Column(
                    modifier = Modifier.weight(0.2f)
                ) {
                    Text(
                        text = "Total Amount", fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                    val totalAmount = invoice.items.sumOf { it.amount * it.quantity }
                    val totalTax = invoice.items.sumOf { it.taxAmount }

                    Text(
                        text = "KES %.2f".format(totalAmount + totalTax),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }


                // 8 & 9. Created/Updated
                Column(
                    horizontalAlignment = Alignment.Start, modifier = Modifier.weight(0.2f).width(200.dp)
                ) {
                    Text(
                        text = "Created: $createdDate",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Updated: $updatedDate",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()

                    )
                }

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colors.primary.copy(0.3f)),
                        onClick = {
                            scope.launch {

                                shareInvoiceUseCase(
                                    attachment = Path(
                                        Constants.FISCALIZED_RECEIPTS_PATH.pathString,
                                        invoice.fileName
                                    ).toFile()
                                )
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Default.Share, contentDescription = "Share Invoice",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colors.error.copy(0.3f)),
                        onClick = {
                            scope.launch {
                                deleteInvoiceUseCase(invoice.fileName, invoice.invoiceNumber)
                                getItemsUseCase()
                                onRefresh()
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Default.Delete, contentDescription = "Delete Invoice",
                            tint = MaterialTheme.colors.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowInvoice(
    file: File?, showDialogBox: Boolean, onDismiss: () -> Unit
) {
    var image: ImageBitmap? by remember(file) { mutableStateOf(null) }
    val isPdf = remember(file) { file?.extension?.lowercase() == "pdf" }
    val isPng = remember(file) { file?.extension?.lowercase() == "png" }

    // Load PDF or PNG image
    LaunchedEffect(file) {
        image = when {
            file == null -> null
            isPdf -> loadPdfFirstPageAsImage(file)
            isPng -> file.inputStream().use { androidx.compose.ui.res.loadImageBitmap(it) }
            else -> null
        }
    }

    if (showDialogBox) {
        Dialog(
            onDismissRequest = onDismiss, properties = DialogProperties(
                dismissOnBackPress = true, dismissOnClickOutside = true
            )
        ) {
            image?.let {
                Image(bitmap = it, contentDescription = "Invoice")
            } ?: Surface(modifier = Modifier.size(200.dp)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Invoice not found")
                }
            }
        }
    }
}


@Composable
fun StatusBadge(
    label: String,
    status: String,
    color: Color,
    viewInvoice: Boolean,
    isRetryLoading: Boolean = false,
    onViewInvoice: () -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.width(200.dp).wrapContentHeight().clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.7f)

        ) {
            Text(
                text = "$label:",
                fontSize = 10.sp,
                color = color,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color
            )
        }

        if (viewInvoice) {
            TextButton(
                onClick = onViewInvoice, colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Transparent
                ), modifier = Modifier.weight(0.3f)
            ) {
                Text(
                    text = "view",
                    fontSize = 10.sp,
                    )
            }
        }
        if (!viewInvoice) {
            if (isRetryLoading) {
                CircularProgressIndicator()
            } else {
                TextButton(
                    onClick = onRetry, colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "retry", textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}


fun String.toLocalFormattedString(): String {
    return try {
        val utcInstant = Instant.parse(this)
        val localDateTime = utcInstant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        this
    }
}

