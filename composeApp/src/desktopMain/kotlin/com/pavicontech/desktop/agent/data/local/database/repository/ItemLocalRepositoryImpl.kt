package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.Cls
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.Dtl
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.PullCodesRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.sql.ResultSet

class ItemLocalRepositoryImpl : ItemLocalRepository {

    override suspend fun insertItem(item: Item) {
        insertItemsInternal(listOf(item))
    }

    override suspend fun insertClassificationCode(codes: List<ClassificationCode>) {
        try {
            val sql = """
        INSERT OR REPLACE INTO ItemClass (itemClsCd, itemClsNm, itemClsLvl, taxTyCd, mjrTgYn, useYn)
        VALUES (?, ?, ?, ?, ?, ?);
    """.trimIndent()

            DatabaseConfig.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    codes.forEach { item ->
                        stmt.setString(1, item.itemClsCd)
                        stmt.setString(2, item.itemClsNm)
                        stmt.setInt(3, item.itemClsLvl)
                        stmt.setString(4, item.taxTyCd)
                        stmt.setString(5, item.mjrTgYn)
                        stmt.setString(6, item.useYn)
                        stmt.addBatch()
                    }
                    stmt.executeBatch()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
override suspend fun getAllClassificationCodes(): List<ClassificationCode> {
        val codes = mutableListOf<ClassificationCode>()

        val sql = """
        SELECT itemClsCd, itemClsNm, itemClsLvl, taxTyCd, mjrTgYn, useYn
        FROM ItemClass;
    """.trimIndent()

        try {
            DatabaseConfig.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        val code = ClassificationCode(
                            itemClsCd = rs.getString("itemClsCd"),
                            itemClsNm = rs.getString("itemClsNm"),
                            itemClsLvl = rs.getInt("itemClsLvl"),
                            taxTyCd = rs.getString("taxTyCd"),
                            mjrTgYn = rs.getString("mjrTgYn"),
                            useYn = rs.getString("useYn")
                        )
                        codes.add(code)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return codes
    }

    override suspend fun insertCodes(codes: PullCodesRes) {
        try {
            DatabaseConfig.getConnection().use { conn ->
                conn.autoCommit = false

                // Insert Cls records
                val clsSql = """
                INSERT OR REPLACE INTO Codes (
                    cdCls, cdClsNm, cdClsDesc, useYn,
                    userDfnNm1, userDfnNm2, userDfnNm3
                ) VALUES (?, ?, ?, ?, ?, ?, ?);
            """.trimIndent()

                conn.prepareStatement(clsSql).use { clsStmt ->
                    codes.clsList.forEach { cls ->
                        clsStmt.setString(1, cls.cdCls)
                        clsStmt.setString(2, cls.cdClsNm)
                        clsStmt.setString(3, cls.cdClsDesc)
                        clsStmt.setString(4, cls.useYn)
                        clsStmt.setString(5, cls.userDfnNm1)
                        clsStmt.setString(6, cls.userDfnNm2)
                        clsStmt.setString(7, cls.userDfnNm3)
                        clsStmt.addBatch()
                    }
                    clsStmt.executeBatch()
                }

                // Insert Dtl records
                val dtlSql = """
                INSERT OR REPLACE INTO CodeDtl (
                    clsCd, cd, cdNm, cdDesc, srtOrd,
                    useYn, userDfnCd1, userDfnCd2, userDfnCd3
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """.trimIndent()

                conn.prepareStatement(dtlSql).use { dtlStmt ->
                    codes.clsList.forEach { cls ->
                        cls.dtlList.forEach { dtl ->
                            dtlStmt.setString(1, cls.cdCls)
                            dtlStmt.setString(2, dtl.cd)
                            dtlStmt.setString(3, dtl.cdNm)
                            dtlStmt.setString(4, dtl.cdDesc)
                            dtlStmt.setInt(5, dtl.srtOrd)
                            dtlStmt.setString(6, dtl.useYn)
                            dtlStmt.setString(7, dtl.userDfnCd1)
                            dtlStmt.setString(8, dtl.userDfnCd2)
                            dtlStmt.setString(9, dtl.userDfnCd3)
                            dtlStmt.addBatch()
                        }
                    }
                    dtlStmt.executeBatch()
                }

                conn.commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getAllCodes(): PullCodesRes = withContext(Dispatchers.IO) {
        val sql = """
        SELECT  c.cdCls, c.cdClsNm, c.cdClsDesc, c.useYn,
                c.userDfnNm1, c.userDfnNm2, c.userDfnNm3,
                d.cd, d.cdNm, d.cdDesc, d.srtOrd,
                d.useYn  AS dtlUseYn,
                d.userDfnCd1, d.userDfnCd2, d.userDfnCd3
        FROM Codes     c
        LEFT JOIN CodeDtl d ON d.clsCd = c.cdCls
        ORDER BY c.cdCls, d.srtOrd
    """.trimIndent()

        DatabaseConfig.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    buildPullCodes(rs)
                }
            }
        }
    }

    override suspend fun insertAllItemsItem(items: List<Item>) {
        try {
            insertItemsInternal(items)
        } catch (e: Exception) {
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


    private fun buildPullCodes(rs: ResultSet): PullCodesRes {
        val clsMap = linkedMapOf<String, MutableClsBuilder>()

        while (rs.next()) {
            val cdCls = rs.getString("cdCls")

            // 1️⃣  Ensure parent exists (one per class)
            val builder = clsMap.getOrPut(cdCls) {
                MutableClsBuilder(
                    cdCls = cdCls,
                    cdClsNm = rs.getString("cdClsNm"),
                    cdClsDesc = rs.getString("cdClsDesc"),
                    useYn = rs.getString("useYn"),
                    userDfnNm1 = rs.getString("userDfnNm1"),
                    userDfnNm2 = rs.getString("userDfnNm2"),
                    userDfnNm3 = rs.getString("userDfnNm3")
                )
            }

            // 2️⃣  Add child row if it exists (LEFT JOIN means Dtl columns may all be null)
            val cd = rs.getString("cd")    // will be null when no child
            if (cd != null) {
                builder.dtl.add(
                    Dtl(
                        cd = cd,
                        cdNm = rs.getString("cdNm"),
                        cdDesc = rs.getString("cdDesc"),
                        srtOrd = rs.getInt("srtOrd"),
                        useYn = rs.getString("dtlUseYn"),
                        userDfnCd1 = rs.getString("userDfnCd1"),
                        userDfnCd2 = rs.getString("userDfnCd2"),
                        userDfnCd3 = rs.getString("userDfnCd3"),
                    )
                )
            }
        }

        // 3️⃣  Freeze the builders into immutable data classes
        val clsList = clsMap.values.map(MutableClsBuilder::toImmutable)
        return PullCodesRes(clsList)
    }


    private class MutableClsBuilder(
        val cdCls: String,
        val cdClsNm: String,
        val cdClsDesc: String?,
        val useYn: String,
        val userDfnNm1: String?,
        val userDfnNm2: String?,
        val userDfnNm3: String?
    ) {
        val dtl: MutableList<Dtl> = mutableListOf()

        fun toImmutable() = Cls(
            cdCls = cdCls, cdClsNm = cdClsNm, cdClsDesc = cdClsDesc, useYn = useYn,
            userDfnNm1 = userDfnNm1, userDfnNm2 = userDfnNm2, userDfnNm3 = userDfnNm3,
            dtlList = dtl
        )
    }

}
