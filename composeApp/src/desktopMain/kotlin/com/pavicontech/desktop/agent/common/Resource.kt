package com.pavicontech.desktop.agent.common

sealed class Resource<T:Any>(
    val data: T? = null,
    val message: String? = null
){
    class Loading<T:Any>(data: T? = null, message: String? = "Loading ..."):Resource<T>(data, message)
    class Success<T:Any>(data: T?, message: String?):Resource<T>(data, message)
    class Error<T:Any>(data: T? = null, message: String?):Resource<T>(data, message)


}