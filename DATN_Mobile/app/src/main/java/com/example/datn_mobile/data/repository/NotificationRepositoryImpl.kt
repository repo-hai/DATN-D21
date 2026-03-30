package com.example.datn_mobile.data.repository

import android.os.Build
import android.util.Log
import com.example.datn_mobile.data.network.api.NotificationApiService
import com.example.datn_mobile.data.network.dto.RegisterFcmTokenRequest
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApiService: NotificationApiService
) : NotificationRepository {

    override suspend fun registerDeviceToken(fcmToken: String): Resource<Unit> {
        return try {
            Log.d("NotificationRepository", "Registering device token: $fcmToken")
            val request = RegisterFcmTokenRequest(
                token = fcmToken,
                deviceName = Build.MODEL,
                osType = "Android"
            )
            Log.d("NotificationRepository", "Request: $request")
            val response = notificationApiService.registerDeviceToken(request)
            Log.d("NotificationRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("NotificationRepository", "Device token registered successfully")
                Resource.Success(Unit)
            } else {
                Log.e("NotificationRepository", "Failed to register token: ${response.message()}")
                Resource.Error("Failed to register token: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error registering device token", e)
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun deleteAllTokens(): Resource<Unit> {
        return try {
            Log.d("NotificationRepository", "Deleting all tokens...")
            val response = notificationApiService.deleteAllTokens()
            if (response.isSuccessful) {
                Log.d("NotificationRepository", "All tokens deleted successfully")
                Resource.Success(Unit)
            } else {
                Log.e("NotificationRepository", "Failed to delete tokens: ${response.message()}")
                Resource.Error("Failed to delete tokens: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error deleting tokens", e)
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
