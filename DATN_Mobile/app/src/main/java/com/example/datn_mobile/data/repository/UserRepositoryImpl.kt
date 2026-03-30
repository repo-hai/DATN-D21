package com.example.datn_mobile.data.repository

import com.example.datn_mobile.data.local.PreferenceDataSource
import com.example.datn_mobile.data.network.api.UserApiService
import com.example.datn_mobile.data.network.dto.UserUpdateRequest
import com.example.datn_mobile.data.network.dto.toUserProfile
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.UserProfile
import com.example.datn_mobile.domain.repository.UserRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    private val preferenceDataSource: PreferenceDataSource
) : UserRepository {

    override suspend fun getUserProfile(): Resource<UserProfile> {
        return try {
            val response = userApiService.getUserProfile()

            if (response.isSuccessful) {
                val profileResponse = response.body()

                if (profileResponse != null && profileResponse.result != null) {
                    val userProfile = profileResponse.result.toUserProfile()
                    Resource.Success(userProfile)
                } else {
                    Resource.Error("Profile response is empty or null")
                }
            } else {
                when (response.code()) {
                    401, 403 -> {
                        // Token expired or invalid - clear and logout
                        preferenceDataSource.clearToken()
                        Resource.Error("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.")
                    }
                    else -> Resource.Error("Failed to fetch profile: ${response.message()}")
                }
            }
        } catch (e: HttpException) {
            Resource.Error("Network error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.message}")
        }
    }

    override suspend fun updateUserProfile(
        fullName: String,
        address: String?,
        dob: String?
    ): Resource<UserProfile> {
        return try {
            val updateRequest = UserUpdateRequest(
                fullName = fullName,
                address = address,
                dob = dob
            )

            val response = userApiService.updateUserProfile(updateRequest)

            if (response.isSuccessful) {
                val updateResponse = response.body()

                if (updateResponse != null && updateResponse.result != null) {
                    val userProfile = updateResponse.result.toUserProfile()
                    Resource.Success(userProfile)
                } else {
                    Resource.Error("Update response is empty or null")
                }
            } else {
                when (response.code()) {
                    401, 403 -> {
                        preferenceDataSource.clearToken()
                        Resource.Error("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.")
                    }
                    else -> Resource.Error("Failed to update profile: ${response.message()}")
                }
            }
        } catch (e: HttpException) {
            Resource.Error("Network error: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Connection error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unknown error: ${e.message}")
        }
    }

    override suspend fun logout() {
        preferenceDataSource.clearToken()
    }
}

