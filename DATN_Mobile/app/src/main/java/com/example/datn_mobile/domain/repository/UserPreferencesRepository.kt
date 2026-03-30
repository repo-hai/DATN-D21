package com.example.datn_mobile.domain.repository

import com.example.datn_mobile.domain.model.UserCredentials
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val authToken: Flow<String?>
    val userCredentials: Flow<UserCredentials?>

    suspend fun saveAuthToken(token: String)
    suspend fun saveUserCredentials(credentials: UserCredentials)

    suspend fun clearAll()
}
