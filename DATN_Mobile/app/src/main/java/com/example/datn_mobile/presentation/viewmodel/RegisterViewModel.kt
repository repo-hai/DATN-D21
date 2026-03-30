package com.example.datn_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.local.PreferenceDataSource
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.RegisterCredentials
import com.example.datn_mobile.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * data context of RegisterViewModel
 */
data class RegisterState(
    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val preferenceDataSource: PreferenceDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onRegisterClicked(phoneNumber: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (phoneNumber.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                _state.value = RegisterState(errorMessage = "Thông tin nhập không đầy đủ!")
                return@launch
            }
            if (password != confirmPassword) {
                _state.value = RegisterState(errorMessage = "Mật khẩu xác nhận không khớp!")
                return@launch
            }

            _state.value = RegisterState(isLoading = true)


            // for now, default role when register on mobile app is normal user.
            val credentials = RegisterCredentials(phoneNumber, password, null)

            when (val result = registerUseCase(credentials)) {
                is Resource.Success -> {
                    // Save phone number for login pre-fill
                    preferenceDataSource.savePhoneNumber(phoneNumber)
                    _state.value = RegisterState(isRegisterSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = RegisterState(errorMessage = result.message)
                }
                else -> { /*DO NOTHING HERE*/}
            }
        }
    }
}