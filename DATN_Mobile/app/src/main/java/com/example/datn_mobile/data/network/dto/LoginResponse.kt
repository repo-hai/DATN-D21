package com.example.datn_mobile.data.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResult(
    @field:Json(name = "token") val token: String,
    @field:Json(name="authenticated") val authenticated: Boolean
)
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @field:Json(name="code") val code: Int,
    @field:Json(name="result") val result: TokenResult
)
