package com.pavicontech.desktop.agent.data.local.database.repository


import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.Cls
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.Dtl
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.PullCodesRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import com.pavicontech.desktop.agent.data.local.database.entries.*
import com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components.ItemsBody

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction



class ItemLocalRepositoryImpl : ItemLocalRepository {

    init {
        DatabaseConfig.init()
    }

    override suspend fun insertItem(item: Item) = withContext(Dispatchers.IO) {
        insertAllItemsItem(listOf(item))
    }

    override suspend fun insertClassificationCode(codes: List<ClassificationCode>) = withContext(Dispatchers.IO) {
        transaction {
            codes.forEach { code ->
                ItemClasses.insertIgnore {
                    it[itemClsCd] = code.itemClsCd
                    it[itemClsNm] = code.itemClsNm
                    it[itemClsLvl] = code.itemClsLvl
                    it[taxTyCd] = code.taxTyCd
                    it[mjrTgYn] = code.mjrTgYn
                    it[useYn] = code.useYn
                }
            }
        }
    }

    override suspend fun getAllClassificationCodes(): List<ClassificationCode> = withContext(Dispatchers.IO) {
        transaction {
            ItemClasses.selectAll().map {
                ClassificationCode(
                    itemClsCd = it[ItemClasses.itemClsCd],
                    itemClsNm = it[ItemClasses.itemClsNm],
                    itemClsLvl = it[ItemClasses.itemClsLvl],
                    taxTyCd = it[ItemClasses.taxTyCd],
                    mjrTgYn = it[ItemClasses.mjrTgYn],
                    useYn = it[ItemClasses.useYn]
                )
            }
        }
    }

    override suspend fun insertCodes(codes: PullCodesRes) = withContext(Dispatchers.IO) {
        transaction {
            codes.clsList.forEach { cls ->
                Codes.insertIgnore {
                    it[cdCls] = cls.cdCls
                    it[cdClsNm] = cls.cdClsNm
                    it[cdClsDesc] = cls.cdClsDesc
                    it[useYn] = cls.useYn
                    it[userDfnNm1] = cls.userDfnNm1
                    it[userDfnNm2] = cls.userDfnNm2
                    it[userDfnNm3] = cls.userDfnNm3
                }
                cls.dtlList.forEach { dtl ->
                    CodeDtls.insertIgnore {
                        it[clsCd] = cls.cdCls
                        it[cd] = dtl.cd
                        it[cdNm] = dtl.cdNm
                        it[cdDesc] = dtl.cdDesc
                        it[srtOrd] = dtl.srtOrd
                        it[useYn] = dtl.useYn
                        it[userDfnCd1] = dtl.userDfnCd1
                        it[userDfnCd2] = dtl.userDfnCd2
                        it[userDfnCd3] = dtl.userDfnCd3
                    }
                }
            }
        }
    }

    override suspend fun getAllCodes(): PullCodesRes = withContext(Dispatchers.IO) {
        transaction {
            val clsMap = linkedMapOf<String, Cls>()
            val dtlsMap = mutableMapOf<String, MutableList<Dtl>>()

            (Codes leftJoin CodeDtls).selectAll().orderBy(Codes.cdCls to SortOrder.ASC, CodeDtls.srtOrd to SortOrder.ASC).forEach {
                val cdCls = it[Codes.cdCls]
                val cls = clsMap.getOrPut(cdCls) {
                    Cls(
                        cdCls = cdCls,
                        cdClsNm = it[Codes.cdClsNm],
                        cdClsDesc = it[Codes.cdClsDesc],
                        useYn = it[Codes.useYn],
                        userDfnNm1 = it[Codes.userDfnNm1],
                        userDfnNm2 = it[Codes.userDfnNm2],
                        userDfnNm3 = it[Codes.userDfnNm3],
                        dtlList = emptyList()
                    )
                }

                if (it[CodeDtls.cd] != null) {
                    val dtl = Dtl(
                        cd = it[CodeDtls.cd],
                        cdNm = it[CodeDtls.cdNm],
                        cdDesc = it[CodeDtls.cdDesc],
                        srtOrd = it[CodeDtls.srtOrd],
                        useYn = it[CodeDtls.useYn],
                        userDfnCd1 = it[CodeDtls.userDfnCd1],
                        userDfnCd2 = it[CodeDtls.userDfnCd2],
                        userDfnCd3 = it[CodeDtls.userDfnCd3]
                    )
                    dtlsMap.getOrPut(cdCls) { mutableListOf() }.add(dtl)
                }
            }

            PullCodesRes(
                clsList = clsMap.map { (k, v) ->
                    v.copy(dtlList = dtlsMap[k] ?: emptyList())
                }
            )
        }
    }

    override suspend fun insertAllItemsItem(items: List<Item>) = withContext(Dispatchers.IO) {
        transaction {
            items.forEach { item ->
                Items.insertIgnore {
                    it[id] = item.id
                    it[barcode] = item.barcode
                    it[batchNumber] = item.batchNumber
                    it[businessId] = item.businessId
                    it[createdAt] = item.createdAt
                    it[currentStock] = item.currentStock
                    it[deletedAt] = item.deletedAt
                    it[itemCategoryId] = item.itemCategoryId ?: 0
                    it[itemCategory] = Json.encodeToString(item.itemCategory)
                    it[itemClassificationCode] = item.itemClassificationCode
                    it[itemCode] = item.itemCode
                    it[itemCodeDf] = item.itemCodeDf
                    it[itemName] = item.itemName
                    it[itemType] = item.itemType
                    it[originCountry] = item.originCountry
                    it[packagingUnit] = item.packagingUnit
                    it[picture] = item.picture
                    it[price] = item.price
                    it[quantityUnit] = item.quantityUnit
                    it[status] = item.status
                    it[taxCode] = item.taxCode
                    it[updatedAt] = item.updatedAt
                    it[userId] = item.userId
                }
            }
        }
    }

    override suspend fun getAllItems(): List<Item> = withContext(Dispatchers.IO) {
        transaction {
            Items.selectAll().map { row -> row.toItem() }
        }
    }

    override suspend fun getItemById(id: Int): Item? = withContext(Dispatchers.IO) {
        transaction {
            Items.select { Items.id eq id }
                .map { it.toItem() }
                .singleOrNull()
        }
    }

    override suspend fun deleteItem(id: Int) = withContext(Dispatchers.IO) {
        transaction {
            Items.deleteIgnoreWhere {
                Items.id eq id
            }
        }
        return@withContext
    }

    private fun ResultRow.toItem(): Item = Item(
        id = this[Items.id],
        barcode = this[Items.barcode],
        batchNumber = this[Items.batchNumber],
        businessId = this[Items.businessId],
        createdAt = this[Items.createdAt],
        currentStock = this[Items.currentStock],
        deletedAt = this[Items.deletedAt],
        itemCategoryId = this[Items.itemCategoryId],
        itemCategory = Json.decodeFromString(this[Items.itemCategory]),
        itemClassificationCode = this[Items.itemClassificationCode] ?: "",
        itemCode = this[Items.itemCode],
        itemCodeDf = this[Items.itemCodeDf],
        itemName = this[Items.itemName],
        itemType = this[Items.itemType],
        originCountry = this[Items.originCountry],
        packagingUnit = this[Items.packagingUnit],
        picture = this[Items.picture],
        price = this[Items.price],
        quantityUnit = this[Items.quantityUnit],
        status = this[Items.status],
        taxCode = this[Items.taxCode],
        updatedAt = this[Items.updatedAt],
        userId = this[Items.userId]
    )
}
