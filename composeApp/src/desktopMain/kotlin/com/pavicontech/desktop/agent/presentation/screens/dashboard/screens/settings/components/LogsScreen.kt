package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavicontech.desktop.agent.common.utils.LogManager
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.sic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.PrintWriter
import java.io.RandomAccessFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data class representing a structured log entry
 */
data class LogEntry(
    val timestamp: LocalDateTime,
    val level: LogLevel,
    val message: String,
    val rawText: String
)

/**
 * Enum representing different log levels with associated colors
 */
enum class LogLevel(val color: Color) {
    INFO(Color(0xFF2196F3)),
    WARNING(Color(0xFFFFC107)),
    ERROR(Color(0xFFF44336)),
    DEBUG(Color(0xFF4CAF50)),
    EXCEPTION(Color(0xFFE91E63)),
    INIT(Color(0xFF9C27B0)),
    UNKNOWN(Color(0xFF757575))
}

/**
 * A composable that displays logs from the app.log file with real-time updates
 */
@Composable
fun LogsScreen() {
    val scope = rememberCoroutineScope()
    val logFile = LogManager.retrieveLogFile()

    // State for logs
    var logs by remember { mutableStateOf<List<LogEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var autoScroll by remember { mutableStateOf(true) }

    // LazyListState for controlling scroll position
    val listState = rememberLazyListState()

    // Function to clear logs
    fun clearLogs() {
        scope.launch(Dispatchers.IO) {
            try {
                // Clear the file by opening it with truncate mode
                PrintWriter(logFile).close()
                // Add initial log entry
                LogManager.logInfo("Logs cleared")
                // Refresh logs
                logs = emptyList()
            } catch (e: Exception) {
                LogManager.logError("Failed to clear logs: ${e.message}")
            }
        }
    }

    // Function to copy logs to clipboard
    fun copyLogsToClipboard() {
        val logText = logs.joinToString("\n") { it.rawText }
        val selection = StringSelection(logText)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, null)
        scope.launch {
            LogManager.logInfo("Logs copied to clipboard")
        }
    }

    // Set up log streaming
    LaunchedEffect(logFile) {
        streamLogs(logFile).collect { newLogs ->
            logs = newLogs
            isLoading = false

            // Auto-scroll to bottom if enabled
            if (autoScroll && newLogs.isNotEmpty()) {
                listState.animateScrollToItem(newLogs.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with title and actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Application Logs",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Auto-scroll toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Checkbox(
                        checked = autoScroll,
                        onCheckedChange = { autoScroll = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.primary
                        )
                    )
                    Text("Auto-scroll", style = MaterialTheme.typography.body2)
                }

                // Copy logs button
                IconButton(
                    onClick = { copyLogsToClipboard() },
                    modifier = Modifier.height(36.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colors.primary.copy(alpha = 0.1f),)

                ) {
                    Icon(
                        painter = painterResource(Res.drawable.sic),
                        contentDescription = "Clear Logs",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Clear logs button
                IconButton(
                    onClick = { clearLogs() },

                    modifier = Modifier.height(36.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colors.error.copy(alpha = 0.1f),)

                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Clear Logs",
                        tint = MaterialTheme.colors.error,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Refresh button
               /* Button(
                    onClick = { isLoading = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colors.primary
                    ),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh Logs",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Refresh")
                }*/
            }
        }

        // Log display area
        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFF1E1E1E)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (logs.isEmpty()) {
                    Text(
                        "No logs available",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(logs) { logEntry ->
                            LogEntryItem(logEntry)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable for displaying a single log entry
 */
@Composable
fun LogEntryItem(logEntry: LogEntry) {
    val formattedTimestamp = logEntry.timestamp.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timestamp
        Text(
            text = formattedTimestamp,
            color = Color.White.copy(alpha = 0.7f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.width(180.dp)
        )

        // Level indicator
        Surface(
            color = logEntry.level.color.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(80.dp)
        ) {
            Text(
                text = logEntry.level.name,
                color = logEntry.level.color,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        // Message
        Text(
            text = logEntry.message,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Visible
        )
    }
}

/**
 * Function to stream logs from the log file
 */
fun streamLogs(logFile: File): Flow<List<LogEntry>> = flow {
    if (!logFile.exists()) {
        emit(emptyList())
        return@flow
    }

    var lastPosition = 0L
    val logEntries = mutableListOf<LogEntry>()

    while (true) {
        val newEntries = withContext(Dispatchers.IO) {
            val currentSize = logFile.length()
            if (currentSize > lastPosition) {
                RandomAccessFile(logFile, "r").use { raf ->
                    raf.seek(lastPosition)
                    val buffer = ByteArray((currentSize - lastPosition).toInt())
                    raf.read(buffer)
                    lastPosition = currentSize

                    val newLines = String(buffer).split("\n")
                    newLines.filter { it.isNotBlank() }.map { parseLine(it) }
                }
            } else {
                emptyList()
            }
        }

        if (newEntries.isNotEmpty()) {
            logEntries.addAll(newEntries)
            emit(logEntries.toList())
        }

        delay(500) // Check for new logs every 500ms
    }
}

/**
 * Parse a log line into a structured LogEntry
 */
fun parseLine(line: String): LogEntry {
    // Default values
    var timestamp = LocalDateTime.now()
    var level = LogLevel.UNKNOWN
    var message = line

    // Try to parse timestamp and level from the line
    val regex = """\[(.*?)\] \[(.*?)\] (.*)""".toRegex()
    val matchResult = regex.find(line)

    if (matchResult != null) {
        val (timestampStr, levelStr, msg) = matchResult.destructured

        // Parse timestamp
        try {
            timestamp = LocalDateTime.parse(timestampStr)
        } catch (e: Exception) {
            // If parsing fails, keep the default
        }

        // Determine log level
        level = when {
            levelStr.contains("ERROR", ignoreCase = true) -> LogLevel.ERROR
            levelStr.contains("WARN", ignoreCase = true) -> LogLevel.WARNING
            levelStr.contains("DEBUG", ignoreCase = true) -> LogLevel.DEBUG
            levelStr.contains("EXCEPTION", ignoreCase = true) -> LogLevel.EXCEPTION
            levelStr.contains("INIT", ignoreCase = true) -> LogLevel.INIT
            else -> LogLevel.INFO
        }

        message = msg
    }

    return LogEntry(timestamp, level, message, line)
}
