package com.example.datn_mobile.data.repository

import com.example.datn_mobile.data.network.api.AuthApiService
import com.example.datn_mobile.data.network.dto.LoginRequest as LoginRequestDto
import com.example.datn_mobile.data.network.dto.RegisterRequest as RegisterRequestDto
import com.example.datn_mobile.data.network.dto.RegisterResponse
import com.example.datn_mobile.data.network.dto.toUserProfile
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.RegisterCredentials
import com.example.datn_mobile.domain.model.UserCredentials
import com.example.datn_mobile.domain.model.UserProfile
import com.example.datn_mobile.domain.repository.AuthRepository
import com.example.datn_mobile.domain.repository.UserPreferencesRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApiService,
    private val prefsRepo: UserPreferencesRepository,
    private val moshi: Moshi
) : AuthRepository {

    override suspend fun login(phoneNumber: String, password: String): Resource<Unit> {
        return try {
            val response = authApi.login(LoginRequestDto(phoneNumber, password))
            if (response.isSuccessful && response.body()?.result != null) {
                val token = response.body()!!.result!!.token
                prefsRepo.saveAuthToken(token)
                prefsRepo.saveUserCredentials(UserCredentials(phoneNumber, password))
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun register(credentials: RegisterCredentials): Resource<UserProfile> {
        return try {
            val registerRequestDto = RegisterRequestDto(
                phoneNumber = credentials.phoneNumber,
                password = credentials.password,
                role = credentials.role ?: "USER"
            )
            val response = authApi.register(registerRequestDto)
            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body != null) {
                    val adapter = moshi.adapter(RegisterResponse::class.java)
                    val registerResponse = adapter.fromJson(body)
                    if (registerResponse != null && registerResponse.code == 1000) {
                        val userProfile = registerResponse.toUserProfile()
                        Resource.Success(userProfile)
                    } else {
                        Resource.Error("Registration failed with code: ${registerResponse?.code}")
                    }
                } else {
                    Resource.Error("Response body is null")
                }
            } else {
                Resource.Error(response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun logout() {
        try {
            authApi.logout()
        } catch (e: Exception) {
            // Ignore errors on logout
        }
        prefsRepo.clearAll()
    }

    override fun getAuthToken(): Flow<String?> {
        return prefsRepo.authToken
    }

    override suspend fun clearAuthToken() {
        prefsRepo.clearAll()
    }
}
