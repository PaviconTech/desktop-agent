package com.pavicontech.desktop.agent.data.local.database

import java.sql.Connection
import java.sql.DriverManager
import java.nio.file.Paths

object DatabaseConfig {
    private val dbPath =
        Paths.get(System.getProperty("user.home"), "Documents", "DesktopAgent", "invoices.db")
            .toString()

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

        val itemTableSql = """
    CREATE TABLE IF NOT EXISTS Item (
        id INTEGER PRIMARY KEY,
        barcode TEXT,
        batchNumber TEXT,
        businessId INTEGER NOT NULL,
        createdAt TEXT NOT NULL,
        currentStock TEXT NOT NULL,
        deletedAt TEXT,
        itemCategoryId INTEGER NOT NULL,
        itemCategory TEXT NOT NULL, -- JSON column
        itemClassificationCode TEXT NOT NULL,
        itemCode TEXT NOT NULL,
        itemCodeDf TEXT NOT NULL,
        itemName TEXT NOT NULL,
        itemType TEXT NOT NULL,
        originCountry TEXT NOT NULL,
        packagingUnit TEXT NOT NULL,
        picture TEXT,
        price TEXT NOT NULL,
        quantityUnit TEXT NOT NULL,
        status TEXT NOT NULL,
        taxCode TEXT NOT NULL,
        updatedAt TEXT NOT NULL,
        userId INTEGER NOT NULL
    );
""".trimIndent()


        getConnection().use { conn ->
            // 👇 Enable Write-Ahead Logging
            conn.createStatement().use { stmt ->
                stmt.execute("PRAGMA journal_mode=WAL") // ✅ Enables concurrent reads/writes
                stmt.execute(sql)
                stmt.execute(itemTableSql)
            }
        }
    }
}
