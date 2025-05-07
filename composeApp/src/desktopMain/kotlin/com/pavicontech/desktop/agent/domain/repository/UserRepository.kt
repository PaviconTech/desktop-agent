package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.request.SignInReq
import com.pavicontech.desktop.agent.data.remote.dto.response.signIn.SignInRes

interface UserRepository {

    suspend fun signIn(body:SignInReq):SignInRes

}