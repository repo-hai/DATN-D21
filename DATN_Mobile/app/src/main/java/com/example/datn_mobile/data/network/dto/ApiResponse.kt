package com.example.datn_mobile.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Generic API response wrapper
 * Tất cả API responses từ backend có structure: { code, result, message }
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @field:Json(name = "code") val code: Int,
    @field:Json(name = "result") val result: T?,
    @field:Json(name = "message") val message: String? = null
)

