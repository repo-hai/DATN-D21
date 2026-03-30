package com.example.datn_mobile.data.network.dto

import com.example.datn_mobile.domain.model.Role
import com.example.datn_mobile.domain.model.UserProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PermissionDto(
    @field:Json(name="name") val name: String,
    @field:Json(name="description") val description: String?
)

@JsonClass(generateAdapter = true)
data class RoleDto (
    @field:Json(name="name") val name: String?,
    @field:Json(name="description") val description: String?,
    @field:Json(name="permissions") val permissions: List<PermissionDto>?
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @field:Json(name="id") val id: String,
    @field:Json(name="fullName") val fullName: String?,
    @field:Json(name="address") val address: String?,
    @field:Json(name="dob") val dob: String?,
    @field:Json(name="email") val email: String?,
    @field:Json(name="phoneNumber") val phoneNumber: String?,
    @field:Json(name="roles") val roles: List<RoleDto>?
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @field:Json(name="code") val code: Int,
    @field:Json(name="result") val result: UserDto
)

/**
 * mapping UserResponse -> UserProfile model
 */
fun RegisterResponse.toUserProfile() : UserProfile {
    val userDto = this.result
    val modelRoles: List<Role>

    if (userDto.roles.isNullOrEmpty()) {
        modelRoles = listOf(Role.USER)
    } else {
        modelRoles = userDto.roles
            .mapNotNull { roleDto ->
                Role.fromString(roleDto.name)
            }
    }

    return UserProfile(
        id = userDto.id,
        email = userDto.email,
        fullName = userDto.fullName,
        address = userDto.address,
        dob = userDto.dob,
        phoneNumber = userDto.phoneNumber,
        roles = modelRoles
    )
}