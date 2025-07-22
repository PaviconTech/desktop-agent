package com.pavicontech.desktop.agent.domain.usecase.items


import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.database.repository.ItemLocalRepository
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.PullCodesRes

class GetItemCodesListUseCase(
    private val itemLocalRepository: ItemLocalRepository
) {
    suspend operator fun invoke(): PullCodesRes?{
        return try {
            val res =itemLocalRepository.getAllCodes()
            res.kraResult.clsList.forEach { it.cdClsNm.logger(Type.DEBUG) }
            res
        }catch (e:Exception){
            e.printStackTrace()
            null

        }
    }
}