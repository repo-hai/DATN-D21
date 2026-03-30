package com.example.datn_mobile.domain.repository

import com.example.datn_mobile.data.util.Resource

interface NotificationRepository {
    suspend fun registerDeviceToken(fcmToken: String): Resource<Unit>
    suspend fun deleteAllTokens(): Resource<Unit>
}
