package com.example.datn_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.local.PreferenceDataSource
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.UserProfile
import com.example.datn_mobile.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val hasToken: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val preferenceDataSource: PreferenceDataSource
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()

    init {
        checkAuthentication()
    }

    /**
     * Kiểm tra xem user có đăng nhập hay không
     */
    private fun checkAuthentication() {
        viewModelScope.launch {
            preferenceDataSource.tokenFlow.collect { token ->
                val hasToken = !token.isNullOrBlank()
                _profileState.value = _profileState.value.copy(hasToken = hasToken)

                if (hasToken) {
                    _profileState.value = _profileState.value.copy(isAuthenticated = true)
                    loadUserProfile()
                } else {
                    _profileState.value = _profileState.value.copy(
                        isAuthenticated = false,
                        userProfile = null,
                        error = null
                    )
                }
            }
        }
    }

    /**
     * Lấy thông tin profile của user
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true, error = null)

            when (val result = getUserProfileUseCase()) {
                is Resource.Success -> {
                    _profileState.value = _profileState.value.copy(
                        userProfile = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> { }
            }
        }
    }

    /**
     * Refresh profile data
     */
    fun refreshProfile() {
        loadUserProfile()
    }

    /**
     * Logout
     */
    fun logout() {
        viewModelScope.launch {
            preferenceDataSource.clearToken()
            _profileState.value = ProfileState(isAuthenticated = false)
        }
    }
}



