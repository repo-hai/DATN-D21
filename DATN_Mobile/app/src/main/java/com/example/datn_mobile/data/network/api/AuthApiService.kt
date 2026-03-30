package com.example.datn_mobile.data.network.api

import com.example.datn_mobile.data.network.dto.LoginRequest
import com.example.datn_mobile.data.network.dto.LoginResponse
import com.example.datn_mobile.data.network.dto.RegisterRequest
import com.example.datn_mobile.data.network.interceptor.NoAuth
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @NoAuth
    @POST("/bej3/auth/log-in")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @NoAuth
    @POST("/bej3/users/create")
    suspend fun register(@Body credentials: RegisterRequest): Response<ResponseBody>

    @POST("/bej3/auth/logout")
    suspend fun logout(): Response<Unit>
}
