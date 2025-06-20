package com.pavicontech.desktop.agent.data.local.database

import com.pavicontech.desktop.agent.data.local.database.entries.CodeDtls
import com.pavicontech.desktop.agent.data.local.database.entries.Codes
import com.pavicontech.desktop.agent.data.local.database.entries.InvoiceEntries
import com.pavicontech.desktop.agent.data.local.database.entries.Invoices
import com.pavicontech.desktop.agent.data.local.database.entries.ItemClasses
import com.pavicontech.desktop.agent.data.local.database.entries.Items
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.DriverManager
import java.nio.file.Paths


object DatabaseConfig {
    private val dbPath = Paths.get(System.getProperty("user.home"), "Documents", "DesktopAgent", "invoices.db").toString()

    fun init() {
        // Create parent folders
        Paths.get(dbPath).toFile().parentFile.mkdirs()

        // Connect with Exposed
        val db = Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")

        // Enable WAL mode outside transaction
        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { connection ->
            connection.createStatement().use { stmt ->
                stmt.execute("PRAGMA journal_mode=WAL;")
            }
        }

        // Set isolation level
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        // Create tables inside transaction
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(
                Invoices,
                Items,
                ItemClasses,
                Codes,
                CodeDtls,
                InvoiceEntries
            )
        }
    }
}
