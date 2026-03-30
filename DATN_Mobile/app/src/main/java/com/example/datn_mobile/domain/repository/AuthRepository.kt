package com.example.datn_mobile.domain.repository

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.RegisterCredentials
import com.example.datn_mobile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(phoneNumber: String, password: String): Resource<Unit>
    suspend fun register(credentials: RegisterCredentials): Resource<UserProfile>
    suspend fun logout()
    fun getAuthToken(): Flow<String?>
    suspend fun clearAuthToken()
}
