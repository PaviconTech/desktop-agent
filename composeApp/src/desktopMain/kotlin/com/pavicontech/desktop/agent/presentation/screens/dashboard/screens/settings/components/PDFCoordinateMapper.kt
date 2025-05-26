package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFileUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.save
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import java.awt.image.BufferedImage
import java.io.File
import kotlin.math.abs
import kotlin.math.min


@Composable
fun PDFCoordinateMapper(isVisible: Boolean) {
    var pdfImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val undoRedoState = remember { UndoRedoState<List<Box>>(emptyList()) }


    var drawingStart by remember { mutableStateOf<Offset?>(null) }
    var currentBox by remember { mutableStateOf<Box?>(null) }

    var qrCodeBox: BoxCoordinates? by remember { mutableStateOf(null) }
    var kraInfoBox: BoxCoordinates? by remember { mutableStateOf(null) }
    val openPdfFile: SelectFileUseCase = koinInject()
    val scope = rememberCoroutineScope()
    var pdfFile: File? by remember { mutableStateOf(null) }
    val keyValueStorage: KeyValueStorage = koinInject()

    LaunchedEffect(pdfFile) {
        pdfFile?.let { pdfImage = loadPdfFirstPageAsImage(it) }
    }
    LaunchedEffect(Unit) {
        keyValueStorage.get(Constants.LAST_SELECTED_PRINTOUT_INVOICE)?.let { lastSelected ->
            val file = File(lastSelected)
            if (file.exists()) {
                pdfFile = file
            }
        }
    }
    LaunchedEffect(Unit) {
        val coordinates = mutableListOf<BoxCoordinates>()
        keyValueStorage.get(Constants.QR_CODE_COORDINATES)?.let { coordinates.add(BoxCoordinates.fromJson(it)) }
        keyValueStorage.get(Constants.KRA_INFO_COORDINATES)?.let{coordinates.add(BoxCoordinates.fromJson(it))}
        undoRedoState.set(coordinates.map { it.toBox() })
    }

    AnimatedVisibility(isVisible) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val file = openPdfFile()
                            pdfFile = file
                            file?.let {
                                keyValueStorage.set(Constants.LAST_SELECTED_PRINTOUT_INVOICE, it.absolutePath)
                            }
                        }
                    },
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary)
                ) {
                    Text("Select File")
                }

                IconButton(onClick = { undoRedoState.undo() }, enabled = undoRedoState.canUndo) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Undo",
                        tint = MaterialTheme.colors.primary
                    )
                }
                IconButton(onClick = { undoRedoState.redo() }, enabled = undoRedoState.canRedo) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Redo",
                        tint = MaterialTheme.colors.primary
                    )
                }
                IconButton(onClick = { undoRedoState.set(emptyList()) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colors.primary
                    )
                }
                IconButton(
                    enabled = undoRedoState.value.size == 2,
                    onClick = {
                        scope.launch {
                            kraInfoBox = undoRedoState.value.first().toNamedBox("kraInfoBox")
                            qrCodeBox = undoRedoState.value.last().toNamedBox("qrCodeBox")
                            " KRA INFO COORDINATES: $kraInfoBox".logger(Type.INFO)
                            " QR CODE COORDINATES: $qrCodeBox".logger(Type.INFO)
                            keyValueStorage.set(key = Constants.QR_CODE_COORDINATES, value = Json.encodeToString(qrCodeBox))
                            keyValueStorage.set(key = Constants.KRA_INFO_COORDINATES, value = Json.encodeToString(kraInfoBox))
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.save),
                        contentDescription = "save",
                        tint =  if (undoRedoState.value.size == 2) MaterialTheme.colors.secondary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            pdfImage?.let { image ->
                Box(
                    modifier = Modifier.wrapContentSize().border(1.dp, Color.Gray)
                ) {
                    Image(
                        bitmap = image, contentDescription = "PDF Page", modifier = Modifier.pointerInput(Unit) {
                            detectDragGestures(onDragStart = { offset ->
                                drawingStart = offset
                                currentBox = null
                            }, onDragEnd = {
                                currentBox?.let { box ->
                                    undoRedoState.set(undoRedoState.value + box)
                                }
                                drawingStart = null
                                currentBox = null
                            }, onDrag = { change, dragAmount ->
                                drawingStart?.let { start ->
                                    val end = change.position
                                    currentBox = Box(start, end)
                                }
                            })
                        })
                    Canvas(modifier = Modifier.matchParentSize()) {
                        undoRedoState.value.forEach { box ->
                            drawRect(
                                color = Color.Red, topLeft = box.topLeft, size = box.size, style = Stroke(width = 2f)
                            )
                        }
                        currentBox?.let { box ->
                            drawRect(
                                color = Color.Blue, topLeft = box.topLeft, size = box.size, style = Stroke(width = 2f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Boxes:", style = MaterialTheme.typography.h4)
                undoRedoState.value.forEachIndexed { index, box ->
                    Text("${index + 1}: x=${box.topLeft.x.toInt()}, y=${box.topLeft.y.toInt()}, width=${box.size.width.toInt()}, height=${box.size.height.toInt()}")
                }
            } ?: Text("Loading PDF...")

        }
    }
}


data class Box(val start: Offset, val end: Offset) {
    val topLeft: Offset
        get() = Offset(min(start.x, end.x), min(start.y, end.y))
    val size: Size
        get() = Size(abs(end.x - start.x), abs(end.y - start.y))

    fun toNamedBox(name: String) = BoxCoordinates(
        name = name,
        startX = start.x,
        startY = start.y,
        endX = end.x,
        endY = end.y
    )
}


class UndoRedoState<T>(initial: T) {
    private val _undoStack = mutableListOf<T>()
    private val _redoStack = mutableListOf<T>()
    private val _state = mutableStateOf(initial)

    val value: T
        get() = _state.value

    val canUndo: Boolean get() = _undoStack.isNotEmpty()
    val canRedo: Boolean get() = _redoStack.isNotEmpty()

    fun set(newValue: T) {
        _undoStack.add(_state.value)
        _state.value = newValue
        _redoStack.clear()
    }

    fun undo() {
        if (canUndo) {
            _redoStack.add(_state.value)
            _state.value = _undoStack.removeLast()
        }
    }

    fun redo() {
        if (canRedo) {
            _undoStack.add(_state.value)
            _state.value = _redoStack.removeLast()
        }
    }
}

suspend fun loadPdfFirstPageAsImage(file: File): ImageBitmap? = withContext(Dispatchers.IO) {
    try {
        val document = PDDocument.load(file)
        val renderer = PDFRenderer(document)
        val image: BufferedImage = renderer.renderImageWithDPI(0, 150f)
        document.close()
        return@withContext image.toComposeImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}

@Serializable
data class BoxCoordinates       (
    val name: String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
){
    fun toBox() = Box(
        start = Offset(startX, startY),
        end = Offset(endX, endY)
    )

    companion object{
        fun toJson() = Json.encodeToString(this)
        fun fromJson(string: String) = Json.decodeFromString<BoxCoordinates>(string)
    }
}
