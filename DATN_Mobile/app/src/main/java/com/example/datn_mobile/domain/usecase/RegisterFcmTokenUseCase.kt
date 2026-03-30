package com.example.datn_mobile.domain.usecase

import android.util.Log
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterFcmTokenUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return try {
            Log.d("RegisterFcmTokenUseCase", "Starting FCM token registration...")
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d("RegisterFcmTokenUseCase", "FCM Token obtained: $token")
            val result = notificationRepository.registerDeviceToken(token)
            Log.d("RegisterFcmTokenUseCase", "Device token registration result: $result")
            result
        } catch (e: Exception) {
            Log.e("RegisterFcmTokenUseCase", "Error registering FCM token", e)
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
