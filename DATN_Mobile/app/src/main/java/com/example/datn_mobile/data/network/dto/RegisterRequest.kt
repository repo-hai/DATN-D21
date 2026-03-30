package com.example.datn_mobile.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * match with register request params
 */
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @field:Json(name="phoneNumber") val phoneNumber: String,
    @field:Json(name="password") val password: String,
    @field:Json(name="role") val role: String = "USER" // default value
)