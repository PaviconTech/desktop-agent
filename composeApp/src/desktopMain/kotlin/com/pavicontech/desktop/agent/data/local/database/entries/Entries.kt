package com.pavicontech.desktop.agent.data.local.database.entries

import com.pavicontech.desktop.agent.data.local.database.entries.ItemClasses.itemClsCd
import com.pavicontech.desktop.agent.data.local.database.entries.Items.id
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


// Invoice Table
object Invoices : Table("Invoice") {
    val id = text("id").uniqueIndex()
    val fileName = text("fileName").uniqueIndex()
    val invoiceNumber = text("invoiceNumber").nullable()
    val extractionStatus = text("extractionStatus")
    val etimsStatus = text("etimsStatus").nullable()
    val items = text("items") // Stored as JSON string
    val totals = text("totals") // Stored as JSON string
    val createdAt = text("createdAt")
    val updatedAt = text("updatedAt")

    override val primaryKey = PrimaryKey(id)
}

// Item Table
object Items : Table("Item") {
    val id = integer("id") // Not auto-incrementing since you supply ID manually
    val barcode = text("barcode").nullable()
    val batchNumber = text("batchNumber").nullable()
    val businessId = integer("businessId")
    val createdAt = text("createdAt")
    val currentStock = text("currentStock")
    val deletedAt = text("deletedAt").nullable()
    val itemCategoryId = integer("itemCategoryId")
    val itemCategory = text("itemCategory") // Stored as JSON string
    val itemClassificationCode = text("itemClassificationCode").references(ItemClasses.itemClsCd, onDelete = ReferenceOption.SET_NULL).nullable()
    val itemCode = text("itemCode")
    val itemCodeDf = text("itemCodeDf")
    val itemName = text("itemName")
    val itemType = text("itemType")
    val originCountry = text("originCountry")
    val packagingUnit = text("packagingUnit")
    val picture = text("picture").nullable()
    val price = text("price")
    val quantityUnit = text("quantityUnit")
    val status = text("status")
    val taxCode = text("taxCode")
    val updatedAt = text("updatedAt")
    val userId = integer("userId")

    override val primaryKey = PrimaryKey(id)
}


object ItemClasses : Table("ItemClass") {
    val itemClsCd = text("itemClsCd")
    val itemClsNm = text("itemClsNm")
    val itemClsLvl = integer("itemClsLvl")
    val taxTyCd = text("taxTyCd").nullable()
    val mjrTgYn = text("mjrTgYn").nullable()
    val useYn = text("useYn")

    override val primaryKey = PrimaryKey(itemClsCd)
}


object Codes : Table("Codes") {
    val cdCls = text("cdCls")
    val cdClsNm = text("cdClsNm")
    val cdClsDesc = text("cdClsDesc").nullable()
    val useYn = text("useYn")
    val userDfnNm1 = text("userDfnNm1").nullable()
    val userDfnNm2 = text("userDfnNm2").nullable()
    val userDfnNm3 = text("userDfnNm3").nullable()

    override val primaryKey = PrimaryKey(cdCls)
}


object CodeDtls : Table("CodeDtl") {
    val id = integer("id").autoIncrement()
    val clsCd = text("clsCd").references(Codes.cdCls, onDelete = ReferenceOption.CASCADE)
    val cd = text("cd")
    val cdNm = text("cdNm")
    val cdDesc = text("cdDesc").nullable()
    val srtOrd = integer("srtOrd")
    val useYn = text("useYn")
    val userDfnCd1 = text("userDfnCd1").nullable()
    val userDfnCd2 = text("userDfnCd2").nullable()
    val userDfnCd3 = text("userDfnCd3").nullable()

    override val primaryKey = PrimaryKey(id)
}




object InvoiceEntries : Table("InvoiceEntry") {
    val invoiceNumber = text("invoiceNumber")
    val fileName = text("fileName").uniqueIndex()

    override val primaryKey = PrimaryKey(fileName)
}
