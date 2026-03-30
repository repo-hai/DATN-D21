package com.example.datn_mobile.data.repository

import com.example.datn_mobile.data.local.PreferenceDataSource
import com.example.datn_mobile.domain.model.UserCredentials
import com.example.datn_mobile.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val preferenceDataSource: PreferenceDataSource
) : UserPreferencesRepository {

    override val authToken: Flow<String?> = preferenceDataSource.tokenFlow

    override val userCredentials: Flow<UserCredentials?> = combine(
        preferenceDataSource.phoneNumberFlow,
        preferenceDataSource.passwordFlow
    ) { phone, password ->
        if (phone != null && password != null) {
            UserCredentials(phone, password)
        } else {
            null
        }
    }

    override suspend fun saveAuthToken(token: String) {
        preferenceDataSource.saveToken(token)
    }

    override suspend fun saveUserCredentials(credentials: UserCredentials) {
        preferenceDataSource.savePhoneNumber(credentials.phoneNumber)
        preferenceDataSource.savePassword(credentials.password)
    }

    override suspend fun clearAll() {
        preferenceDataSource.clearAll()
    }
}
