package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import kotlinx.serialization.json.Json
import java.sql.ResultSet

class ItemLocalRepositoryImpl : ItemLocalRepository {

    override suspend fun insertItem(item: Item) {
        insertItemsInternal(listOf(item))
    }

    override suspend fun insertAllItemsItem(items: List<Item>) {
        try {
            insertItemsInternal(items)
        }catch (e:Exception){
            e.message?.logger(Type.WARN)
        }
    }

    private fun insertItemsInternal(items: List<Item>) {
        val sql = """
    INSERT INTO Item (
        id, barcode, batchNumber, businessId, createdAt, currentStock, deletedAt,
        itemCategoryId, itemCategory, itemClassificationCode, itemCode, itemCodeDf,
        itemName, itemType, originCountry, packagingUnit, picture, price,
        quantityUnit, status, taxCode, updatedAt, userId
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT(id) DO UPDATE SET
        barcode = excluded.barcode,
        batchNumber = excluded.batchNumber,
        businessId = excluded.businessId,
        createdAt = excluded.createdAt,
        currentStock = excluded.currentStock,
        deletedAt = excluded.deletedAt,
        itemCategoryId = excluded.itemCategoryId,
        itemCategory = excluded.itemCategory,
        itemClassificationCode = excluded.itemClassificationCode,
        itemCode = excluded.itemCode,
        itemCodeDf = excluded.itemCodeDf,
        itemName = excluded.itemName,
        itemType = excluded.itemType,
        originCountry = excluded.originCountry,
        packagingUnit = excluded.packagingUnit,
        picture = excluded.picture,
        price = excluded.price,
        quantityUnit = excluded.quantityUnit,
        status = excluded.status,
        taxCode = excluded.taxCode,
        updatedAt = excluded.updatedAt,
        userId = excluded.userId
""".trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                for (item in items) {
                    stmt.setInt(1, item.id)
                    stmt.setString(2, item.barcode)
                    stmt.setString(3, item.batchNumber)
                    stmt.setInt(4, item.businessId)
                    stmt.setString(5, item.createdAt)
                    stmt.setString(6, item.currentStock)
                    stmt.setString(7, item.deletedAt)
                    stmt.setInt(8, item.itemCategoryId)
                    stmt.setString(9, Json.encodeToString(item.itemCategory))
                    stmt.setString(10, item.itemClassificationCode)
                    stmt.setString(11, item.itemCode)
                    stmt.setString(12, item.itemCodeDf)
                    stmt.setString(13, item.itemName)
                    stmt.setString(14, item.itemType)
                    stmt.setString(15, item.originCountry)
                    stmt.setString(16, item.packagingUnit)
                    stmt.setString(17, item.picture)
                    stmt.setString(18, item.price)
                    stmt.setString(19, item.quantityUnit)
                    stmt.setString(20, item.status)
                    stmt.setString(21, item.taxCode)
                    stmt.setString(22, item.updatedAt)
                    stmt.setInt(23, item.userId)

                    stmt.addBatch()
                }
                stmt.executeBatch()
            }
        }
    }

    override suspend fun getAllItems(): List<Item> {
        val sql = "SELECT * FROM Item"
        return DatabaseConfig.getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                val items = mutableListOf<Item>()
                while (rs.next()) {
                    items.add(rsToItem(rs))
                }
                items
            }
        }
    }

    override suspend fun getItemById(id: Int): Item? {
        val sql = "SELECT * FROM Item WHERE id = ?"
        return DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) rsToItem(rs) else null
            }
        }
    }

    override suspend fun deleteItem(id: Int) {
        val sql = "DELETE FROM Item WHERE id = ?"
        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
        }
    }

    private fun rsToItem(rs: ResultSet): Item {
        return Item(
            id = rs.getInt("id"),
            barcode = rs.getString("barcode"),
            batchNumber = rs.getString("batchNumber"),
            businessId = rs.getInt("businessId"),
            createdAt = rs.getString("createdAt"),
            currentStock = rs.getString("currentStock"),
            deletedAt = rs.getString("deletedAt"),
            itemCategoryId = rs.getInt("itemCategoryId"),
            itemCategory = Json.decodeFromString(rs.getString("itemCategory")),
            itemClassificationCode = rs.getString("itemClassificationCode"),
            itemCode = rs.getString("itemCode"),
            itemCodeDf = rs.getString("itemCodeDf"),
            itemName = rs.getString("itemName"),
            itemType = rs.getString("itemType"),
            originCountry = rs.getString("originCountry"),
            packagingUnit = rs.getString("packagingUnit"),
            picture = rs.getString("picture"),
            price = rs.getString("price"),
            quantityUnit = rs.getString("quantityUnit"),
            status = rs.getString("status"),
            taxCode = rs.getString("taxCode"),
            updatedAt = rs.getString("updatedAt"),
            userId = rs.getInt("userId")
        )
    }
}
