package com.pavicontech.desktop.agent.domain.usecase

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepository
import com.pavicontech.desktop.agent.data.local.KeyValueStorage
import com.pavicontech.desktop.agent.data.repository.InvoiceRepositoryImpl
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.repository.InvoiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SubmitInvoicesUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val directoryWatcherRepository: DirectoryWatcherRepository,
    private val keyValueStorage: KeyValueStorage
) {


    suspend operator fun invoke(): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: ""
            val bussinessInfo = try {
                val json = keyValueStorage.get(Constants.BUSINESS_INFORMATION) ?: ""
                Json.decodeFromString<BusinessInformation>(json)
            } catch (e: Exception) {
                null
            }
            directoryWatcherRepository.watchDirectory(
                path = "/home/dev-pasaka/Desktop",
                onDelete = { dir ->

                },
                onModify = { dir ->
                    bussinessInfo?.let {
                        invoiceRepository.sale(
                            body = it.toInvoiceReq().copy(
                                file = dir.fullDirectory.fileToByteArray(),
                                fileName = dir.fileName
                            ),
                            token = token
                        )
                    }
                }

            ).collect { event ->
                when (event) {
                    is Resource.Loading -> {
                        println("Event: ${event.message}  ${event.data} \n")
                    }

                    is Resource.Success -> {
                        println("File: ${event.data?.fileName}  ${event.data?.fullDirectory} \n")
                        bussinessInfo?.let {
                            invoiceRepository.sale(
                                body = it.toInvoiceReq().copy(
                                    file = event.data?.fullDirectory?.fileToByteArray(),
                                    fileName = event.data?.fileName ?: ""
                                ),
                                token = token
                            )
                        }
                    }

                    is Resource.Error -> {
                        println("Event: ${event.message}  ${event.data} \n")
                    }
                }
            }
        } catch (e: Exception) {


        }
    }
}