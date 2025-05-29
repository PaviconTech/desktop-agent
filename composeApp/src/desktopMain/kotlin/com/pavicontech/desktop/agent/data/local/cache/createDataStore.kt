package com.pavicontech.desktop.agent.data.local.cache


import java.io.File
import java.nio.file.Paths
import kotlin.io.path.pathString


fun createDataStore(): File {
    val home = System.getProperty("user.home")
    val path = Paths.get(home, "Documents", "DesktopAgent",)

    val dir = File(path.pathString)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return File(dir, "profile_prefs.json")
}

