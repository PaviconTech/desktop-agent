package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.domain.usecase.GetSalesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SalesScreenViewModel(
    private val getSalesUseCase: GetSalesUseCase
): ScreenModel {

    private val viewModelScope = CoroutineScope(Dispatchers.Main.immediate)
    var getSalesState by mutableStateOf(GetSalesState())
        private set

    fun getSales(){
        viewModelScope.launch {
            getSalesUseCase().collect {result ->
                println("${result.message}: ${result.data}")
                when(result){
                    is Resource.Success -> {
                        getSalesState = getSalesState.copy(
                            isLoading = false,
                            isSuccessful = true,
                            invoices = result.data ?: emptyList(),
                            error = result.message ?: "An unknown error occurred."
                        )

                    }
                    is Resource.Error -> {
                        getSalesState = getSalesState.copy(
                            isLoading = false,
                            isSuccessful = false,
                            invoices = emptyList(),
                            error = result.message ?: "An unknown error occurred."
                        )

                    }
                    is Resource.Loading -> {
                        getSalesState = getSalesState.copy(
                            isLoading = true,
                            isSuccessful = false,
                            invoices = emptyList(),
                            error = ""

                        )

                    }

                }

            }
        }
    }

    fun backgroundSync(){
        viewModelScope.launch {
            while (true){
                delay(1000)
                getSalesUseCase().collect{ result->
                    println("${result.message}: ${result.data}")
                    when(result){
                        is Resource.Success -> {
                            getSalesState = getSalesState.copy(
                                isLoading = false,
                                isSuccessful = true,
                                invoices = result.data ?: emptyList(),
                                error = result.message ?: "An unknown error occurred."
                            )
                            println("Syncing invoices successful")

                        }
                        is Resource.Error -> {
                            println("An Error while syncing: ${result.message}")

                        }
                        is Resource.Loading -> {
                            println("Syncing Invoices........")

                        }

                    }
                }
            }
        }
    }

    init {
        //getSales()
        //backgroundSync()
    }


}