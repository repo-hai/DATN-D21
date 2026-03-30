package com.example.datn_mobile.data.network.dto

import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class RegisterFcmTokenRequest(
    val token: String,
    val deviceName: String,
    val osType: String
)

@JsonClass(generateAdapter = true)
data class FcmTokenResponse(
    val id: String?,
    val token: String?,
    val deviceName: String?,
    val osType: String?,
    val registeredAt: Instant?
)
