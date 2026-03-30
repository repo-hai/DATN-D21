package com.example.datn_mobile.domain.repository

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.UserProfile

interface UserRepository {
    suspend fun getUserProfile(): Resource<UserProfile>
    suspend fun updateUserProfile(fullName: String, address: String?, dob: String?): Resource<UserProfile>
    suspend fun logout()
}

