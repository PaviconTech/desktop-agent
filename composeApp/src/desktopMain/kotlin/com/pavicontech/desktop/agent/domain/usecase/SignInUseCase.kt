package com.pavicontech.desktop.agent.domain.usecase

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.request.SignInReq
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.SavedUserCredentials
import com.pavicontech.desktop.agent.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.days

class SignInUseCase(
    private val repository: UserRepository,
    private val keyValueStorage: KeyValueStorage
) {
    operator fun invoke(
        kraPin: String,
        username: String,
        password: String
    ): Flow<Resource<BusinessInformation>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.signIn(
                body = SignInReq(
                    deviceDescription = "",
                    password = password,
                    username = username,
                    pin = kraPin,
                    platform = "desktop"
                )
            )
            if (response.status) {
                keyValueStorage.set(Constants.AUTH_TOKEN, response.accessToken)
                keyValueStorage.set(Constants.AUTH_TOKEN_EXPIRY,
                    (System.currentTimeMillis() + 30.days.inWholeMilliseconds).toString()
                )
                keyValueStorage.set(
                    Constants.SAVED_USER_CREDENTIALS,
                    SavedUserCredentials(username = username, kraPin = kraPin).toJson()
                )
                keyValueStorage.set(
                    Constants.BUSINESS_INFORMATION,
                    response.business.toBusinessInformation().toJsonString()
                )
                emit(Resource.Success(message = "Sign in Success", data = response.business.toBusinessInformation()))
            } else emit(Resource.Error(message = "Sign In failed. Kindly check your credentials"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(message = e.message ?: "An expected error occurred"))
        }
    }

}