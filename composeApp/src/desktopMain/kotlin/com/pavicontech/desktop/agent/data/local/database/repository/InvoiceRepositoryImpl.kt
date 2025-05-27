package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import kotlinx.serialization.json.Json
import java.sql.ResultSet

class InvoiceRepositoryImpl() : InvoiceRepository {

    init {
        DatabaseConfig.init()
    }

    override suspend fun insertInvoice(invoice: Invoice) {
        val sql = """
            INSERT INTO Invoice (id, fileName, extractionStatus, etimsStatus, items, totals, createdAt, updatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, invoice.id)
                stmt.setString(2, invoice.fileName)
                stmt.setString(3, invoice.extractionStatus.name)
                stmt.setString(4, invoice.etimsStatus.name)
                stmt.setString(5, Json.encodeToString(invoice.items))
                stmt.setString(6, Json.encodeToString(invoice.totals))
                stmt.setString(7, invoice.createdAt)
                stmt.setString(8, invoice.updatedAt)
                stmt.executeUpdate()
            }
        }
    }

    override suspend fun updateInvoice(fileName: String, invoice: Invoice) {
        val sql = """
            UPDATE Invoice SET
                extractionStatus = ?, etimsStatus = ?, items = ?, totals = ?, updatedAt = ?
            WHERE fileName = ?
        """.trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, invoice.extractionStatus.name)
                stmt.setString(2, invoice.etimsStatus.name)
                stmt.setString(3, Json.encodeToString(invoice.items))
                stmt.setString(4, Json.encodeToString(invoice.totals))
                stmt.setString(5, invoice.updatedAt)
                stmt.setString(6, fileName)
                stmt.executeUpdate()
            }
        }
    }

    override suspend fun deleteInvoice(id: String) {
        val sql = "DELETE FROM Invoice WHERE id = ?"

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, id)
                stmt.executeUpdate()
            }
        }
    }

    override suspend fun getInvoiceById(id: String): Invoice? {
        val sql = "SELECT * FROM Invoice WHERE id = ?"

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) rsToInvoice(rs) else null
            }
        }
    }

    override suspend fun getAllInvoices(): List<Invoice> {
        val sql = "SELECT * FROM Invoice"

        return DatabaseConfig.getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                val invoices = mutableListOf<Invoice>()
                while (rs.next()) {
                    invoices.add(rsToInvoice(rs))
                }
                invoices
            }
        }
    }

    private fun rsToInvoice(rs: ResultSet): Invoice {
        return Invoice(
            id = rs.getString("id"),
            fileName = rs.getString("fileName"),
            extractionStatus = enumValueOf(rs.getString("extractionStatus")),
            etimsStatus = enumValueOf(rs.getString("etimsStatus")),
            items = Json.decodeFromString(rs.getString("items")),
            totals = Json.decodeFromString(rs.getString("totals")),
            createdAt = rs.getString("createdAt"),
            updatedAt = rs.getString("updatedAt")
        )
    }
}
