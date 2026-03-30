package com.example.datn_mobile.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @field:Json(name = "code") val code: Int,
    @field:Json(name = "message") val message: String?
)

