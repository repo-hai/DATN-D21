package com.example.datn_mobile.domain.usecase

import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.repository.AuthRepository
import com.example.datn_mobile.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CheckAuthStatusUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase
) {
    suspend operator fun invoke(): Resource<Unit> {
        val credentials = userPreferencesRepository.userCredentials.firstOrNull()
            ?: return Resource.Error("No credentials stored")

        return when (val loginResult = authRepository.login(credentials.phoneNumber, credentials.password)) {
            is Resource.Success -> {
                // Sau khi login thành công, đăng ký FCM token
                registerFcmTokenUseCase()
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                // Nếu login thất bại, xóa credentials cũ và trả về lỗi
                authRepository.clearAuthToken()
                Resource.Error("Auto-login failed: ${loginResult.message}")
            }
            else -> Resource.Loading()
        }
    }
}
