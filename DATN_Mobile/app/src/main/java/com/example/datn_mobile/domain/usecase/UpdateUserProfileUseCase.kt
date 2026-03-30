package com.example.datn_mobile.domain.usecase

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.UserProfile
import com.example.datn_mobile.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        fullName: String,
        address: String?,
        dob: String?
    ): Resource<UserProfile> {
        // Validation
        if (fullName.isBlank()) {
            return Resource.Error("Tên đầy đủ không được để trống")
        }

        return userRepository.updateUserProfile(fullName, address, dob)
    }
}

