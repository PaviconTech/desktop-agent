package com.pavicontech.desktop.agent.data.local.database.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.database.MongoDBConfig
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class InvoiceRepositoryImpl(
    private val db: MongoDBConfig
): InvoiceRepository {
    init {
        db.getDatabaseName() logger(Type.INFO)
    }
    private val invoiceCollection = db.mongoClient.getDatabase(db.getDatabaseName()).getCollection<Invoice>("invoice")
    override suspend fun insertInvoice(invoice: Invoice): Boolean {
        return invoiceCollection.insertOne(invoice).wasAcknowledged()
    }

    override suspend fun getInvoices(): List<Invoice> {
        return invoiceCollection.find(Filters.eq(Invoice::kraPin.name, db.pin)).toList()
    }

    override suspend fun getInvoiceById(id: String): Invoice? {
        return invoiceCollection.find(Filters.eq(Invoice::id.name, id)).firstOrNull()
    }

    override suspend fun updateInvoice(fileName:String, invoice: Invoice): Boolean {
        val result =  invoiceCollection.findOneAndUpdate(
            filter = Filters.eq(Invoice::fileName.name, fileName),
            Updates.combine(
                Updates.set(Invoice::extractionStatus.name, invoice.extractionStatus),
                Updates.set(Invoice::items.name, invoice.items),
                Updates.set(Invoice::totals.name, invoice.totals),
                Updates.set(Invoice::updatedAt.name, invoice.updatedAt)
            )
        )

        return result  != null
    }

    override suspend fun deleteInvoice(id: String): Boolean {
        return invoiceCollection.deleteOne(Filters.eq(Invoice::id.name, id)).wasAcknowledged()
    }
}