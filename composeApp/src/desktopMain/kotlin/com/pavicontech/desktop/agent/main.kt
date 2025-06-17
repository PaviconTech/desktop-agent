    package com.pavicontech.desktop.agent

    import SubmitInvoicesUseCase
    import androidx.compose.runtime.*
    import androidx.compose.ui.unit.DpSize
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.window.*
    import com.pavicontech.desktop.agent.common.utils.LogManager
    import com.pavicontech.desktop.agent.di.initKoin
    import com.pavicontech.desktop.agent.domain.InitUseCases
    import desktopagent.composeapp.generated.resources.Res
    import desktopagent.composeapp.generated.resources.kra
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import org.jetbrains.compose.resources.painterResource
    import org.koin.compose.koinInject
    import java.awt.*
    import java.awt.image.BufferedImage
    import javax.imageio.ImageIO
    import kotlin.system.exitProcess

    fun main() {
        initKoin()
        LogManager.init()

        application(exitProcessOnExit = false) {
            val initUseCases: InitUseCases = koinInject()
            CoroutineScope(Dispatchers.IO).launch {
                initUseCases()
            }

            var isVisible by remember { mutableStateOf(true) }

            val tray = SystemTray.getSystemTray()
            val iconImage: BufferedImage = ImageIO.read(object {}.javaClass.getResource("/icon.png"))

            lateinit var trayIcon: TrayIcon

            val popup = PopupMenu().apply {
                add(MenuItem("Open").apply {
                    addActionListener {
                        isVisible = true
                    }
                })
                add(MenuItem("Quit").apply {
                    addActionListener {
                        tray.remove(trayIcon)
                        exitProcess(0)
                    }
                })
            }

            trayIcon = TrayIcon(iconImage, "Etims Sync", popup).apply {
                isImageAutoSize = true
            }

            if (!tray.trayIcons.contains(trayIcon)) {
                try {
                    tray.add(trayIcon)
                } catch (e: Exception) {
                    println("Failed to add tray icon: ${e.message}")
                }
            }

            val windowState = rememberWindowState(
                placement = WindowPlacement.Floating,
                size = DpSize(width = 1200.dp, height = 800.dp)
            )

            Window(
                onCloseRequest = { isVisible = false },
                title = "Etims Sync",
                visible = isVisible,
                state = windowState,
                icon = painterResource(Res.drawable.kra)
            ) {
                App(width = windowState.size.width, height = windowState.size.height)
            }
        }
    }

