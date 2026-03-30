package com.example.datn_mobile.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @field:Json(name = "phoneNumber") val phoneNumber: String,
    @field:Json(name = "password") val password: String
)
