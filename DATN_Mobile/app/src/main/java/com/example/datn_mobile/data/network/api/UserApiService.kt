package com.example.datn_mobile.data.network.api

import com.example.datn_mobile.data.network.dto.ProfileResponse
import com.example.datn_mobile.data.network.dto.UpdateProfileResponse
import com.example.datn_mobile.data.network.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApiService {
    @GET("/bej3/users/profile/my-info")
    suspend fun getUserProfile(): Response<ProfileResponse>

    @PUT("/bej3/users/profile/my-info/update")
    suspend fun updateUserProfile(@Body request: UserUpdateRequest): Response<UpdateProfileResponse>
}