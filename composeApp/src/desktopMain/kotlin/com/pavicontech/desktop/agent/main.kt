package com.pavicontech.desktop.agent

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.MutableDocument
import com.pavicontech.desktop.agent.di.initKoin
fun main() = application {
    initKoin()
    initKotBase()

    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Desktop Agent",
        state = windowState
    ) {
        // Access window size here
        val width = windowState.size.width
        val height = windowState.size.height

        println("Window size: width = $width, height = $height")

        App(width = width, height = height)
    }
}


fun initKotBase(){
    CouchbaseLite.init() // Required before using Couchbase Lite

    val config = DatabaseConfiguration()
    val database = Database("mydb", config)

    // Example: create and save a document
    val doc = MutableDocument()
        .setString("type", "user")
        .setString("name", "Pasaka")

    database.save(doc)

    println("Saved doc ID: ${doc.id}")
}