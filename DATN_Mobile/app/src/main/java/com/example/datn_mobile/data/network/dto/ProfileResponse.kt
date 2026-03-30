package com.example.datn_mobile.data.network.dto

import com.example.datn_mobile.domain.model.UserProfile
import com.example.datn_mobile.domain.model.Role
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponseData(
    @field:Json(name="id") val id: String,
    @field:Json(name="fullName") val fullName: String? = null,
    @field:Json(name="address") val address: String? = null,
    @field:Json(name="dob") val dob: String? = null,
    @field:Json(name="email") val email: String? = null,
    @field:Json(name="phoneNumber") val phoneNumber: String? = null,
    @field:Json(name="roles") val roles: List<Any>? = null
)

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    @field:Json(name="code") val code: Int,
    @field:Json(name="result") val result: ProfileResponseData?
)

fun ProfileResponseData.toUserProfile(): UserProfile {
    return UserProfile(
        id = id,
        fullName = fullName,
        address = address,
        dob = dob,
        email = email,
        phoneNumber = phoneNumber,
        roles = roles?.mapNotNull { role ->
            when (role) {
                is String -> {
                    try {
                        Role.valueOf(role)
                    } catch (_: Exception) {
                        Role.USER
                    }
                }
                is LinkedHashMap<*, *> -> {
                    try {
                        val roleName = (role["name"] as? String)?.uppercase() ?: return@mapNotNull null
                        Role.valueOf(roleName)
                    } catch (_: Exception) {
                        Role.USER
                    }
                }
                else -> null
            }
        } ?: listOf(Role.USER)
    )
}

