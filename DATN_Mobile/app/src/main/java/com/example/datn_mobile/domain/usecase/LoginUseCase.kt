package com.example.datn_mobile.domain.usecase

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.UserCredentials
import com.example.datn_mobile.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor (
    private val authRepository: AuthRepository,
    //private val userRepository: UserRepository,
    //private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(credentials: UserCredentials) : Resource<Unit> {
        // validation
        if (credentials.phoneNumber.isBlank() || credentials.password.isBlank()) {
            return Resource.Error("Phone number and password cannot be blank")
        }

        val loginResult = authRepository.login(
            phoneNumber = credentials.phoneNumber,
            password = credentials.password
        )

        if (loginResult is Resource.Success) {
            // userRepository.fetchAndSaveProfile()
            // notificationRepository.registerDeviceToken()
        }

        if (loginResult is Resource.Loading) {
            /*TO DO*/
        }
        return loginResult
    }
}
