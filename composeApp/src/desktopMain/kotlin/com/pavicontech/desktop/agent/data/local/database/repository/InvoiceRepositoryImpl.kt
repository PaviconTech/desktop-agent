package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import kotlinx.serialization.json.Json
import java.sql.ResultSet

class InvoiceRepositoryImpl() : InvoiceRepository {

    init {
        DatabaseConfig.init()
    }

    override suspend fun getInvoicesByInvoiceNumber(invoiceNumber: String): List<Invoice> {
        val sql = "SELECT * FROM Invoice WHERE invoiceNumber = ?"

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, invoiceNumber)
                val rs = stmt.executeQuery()

                val invoices = mutableListOf<Invoice>()
                while (rs.next()) {
                    invoices.add(rsToInvoice(rs))
                }
                return invoices
            }
        }
    }

    override suspend fun deleteInvoiceByFileName(fileName: String) {
        val sql = "DELETE FROM Invoice WHERE fileName = ?"

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, fileName)
                stmt.executeUpdate()
            }
        }
    }


    override suspend fun insertInvoice(invoice: Invoice) {
        val selectSql = "SELECT id FROM Invoice WHERE fileName = ?"

        DatabaseConfig.getConnection().use { conn ->
            val exists = conn.prepareStatement(selectSql).use { stmt ->
                stmt.setString(1, invoice.fileName)
                val rs = stmt.executeQuery()
                rs.next() // true if row exists
            }

            val sql = if (exists) {
                """
            UPDATE Invoice
            SET
                id = ?,
                extractionStatus = ?,
                etimsStatus = ?,
                items = ?,
                totals = ?,
                createdAt = ?,
                updatedAt = ?
            WHERE fileName = ?
            """
            } else {
                """
            INSERT INTO Invoice (id, fileName, extractionStatus, etimsStatus, items, totals, createdAt, updatedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """
            }

            conn.prepareStatement(sql).use { stmt ->
                if (exists) {
                    stmt.setString(1, invoice.id)
                    stmt.setString(2, invoice.extractionStatus.name)
                    stmt.setString(3, invoice.etimsStatus.name)
                    stmt.setString(4, Json.encodeToString(invoice.items))
                    stmt.setString(5, Json.encodeToString(invoice.totals))
                    stmt.setString(6, invoice.createdAt)
                    stmt.setString(7, invoice.updatedAt)
                    stmt.setString(8, invoice.fileName) // for WHERE clause
                } else {
                    stmt.setString(1, invoice.id)
                    stmt.setString(2, invoice.fileName)
                    stmt.setString(3, invoice.extractionStatus.name)
                    stmt.setString(4, invoice.etimsStatus.name)
                    stmt.setString(5, Json.encodeToString(invoice.items))
                    stmt.setString(6, Json.encodeToString(invoice.totals))
                    stmt.setString(7, invoice.createdAt)
                    stmt.setString(8, invoice.updatedAt)
                }

                stmt.executeUpdate()
            }
        }
    }



    override suspend fun updateInvoice(fileName: String, invoice: Invoice) {
        val sql = """
            UPDATE Invoice SET
                   etimsStatus = ?, items = ?,invoiceNumber = ?, extractionStatus = ?, totals = ?, updatedAt = ?
            WHERE fileName = ?
        """.trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, invoice.etimsStatus.name)
                stmt.setString(2, Json.encodeToString(invoice.items))
                stmt.setString(3, invoice.invoiceNumber) // invoiceNumber
                stmt.setString(4, invoice.extractionStatus.name) // extractionStatus
                stmt.setString(5, Json.encodeToString(invoice.totals))
                stmt.setString(6, invoice.updatedAt)
                stmt.setString(7, fileName)
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
            invoiceNumber = rs.getString("invoiceNumber"), // ✅ Safe: nullable assigned to nullable
            extractionStatus = enumValueOf(rs.getString("extractionStatus")),
            etimsStatus = enumValueOf(rs.getString("etimsStatus")),
            items = Json.decodeFromString(rs.getString("items")),
            totals = Json.decodeFromString(rs.getString("totals")),
            createdAt = rs.getString("createdAt"),
            updatedAt = rs.getString("updatedAt")
        )
    }


}
