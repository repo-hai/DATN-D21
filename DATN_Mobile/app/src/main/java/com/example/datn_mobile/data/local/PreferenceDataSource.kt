package com.example.datn_mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/*
lưu token, logout: prefs.saveToken(newToken), prefs.clearToken()
Đọc, lấy token cho Interceptor: prefs.tokenFlow.first()
Đọc isFirstLaunch: prefs.isFirstLaunchFlow.stateIn(scope = viewModelScope,
started = SharingStarted.WhileSubcribed(5000)
.initialValue = true )
 */

private val Context.dataStore by preferencesDataStore(name = "user_preferences")
@Singleton
class PreferenceDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_PHONE_NUMBER = stringPreferencesKey("phone_number")
        private val KEY_PASSWORD = stringPreferencesKey("password")
    }

    val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_IS_FIRST_LAUNCH] ?: true
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map {
        it[KEY_ACCESS_TOKEN]
    }

    val phoneNumberFlow: Flow<String?> = context.dataStore.data.map {
        it[KEY_PHONE_NUMBER]
    }

    val passwordFlow: Flow<String?> = context.dataStore.data.map {
        it[KEY_PASSWORD]
    }

    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { it[KEY_IS_FIRST_LAUNCH] = isFirst }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_ACCESS_TOKEN] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(KEY_ACCESS_TOKEN) }
    }

    suspend fun savePhoneNumber(phoneNumber: String) {
        context.dataStore.edit { it[KEY_PHONE_NUMBER] = phoneNumber }
    }

    suspend fun clearPhoneNumber() {
        context.dataStore.edit { it.remove(KEY_PHONE_NUMBER) }
    }

    suspend fun savePassword(password: String) {
        context.dataStore.edit { it[KEY_PASSWORD] = password }
    }

    suspend fun clearPassword() {
        context.dataStore.edit { it.remove(KEY_PASSWORD) }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}