package com.pavicontech.desktop.agent.data.local.database

import java.sql.Connection
import java.sql.DriverManager
import java.nio.file.Paths

object DatabaseConfig {
    private val dbPath = Paths.get(System.getProperty("user.home"), "Documents", "DesktopAgent", "invoices.db").toString()

    fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:$dbPath")
    }

    fun init() {
        val dbFile = Paths.get(dbPath).toFile()
        dbFile.parentFile.mkdirs() // create DesktopAgent folder if not exists

        val sql = """
        CREATE TABLE IF NOT EXISTS Invoice (
            id TEXT PRIMARY KEY,
            fileName TEXT NOT NULL,
            extractionStatus TEXT,
            etimsStatus TEXT,
            items TEXT,
            totals TEXT,
            createdAt TEXT,
            updatedAt TEXT
        );
    """.trimIndent()

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sql)
            }
        }
    }
}
