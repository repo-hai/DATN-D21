package com.example.datn_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.local.PreferenceDataSource
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.model.UserProfile
import com.example.datn_mobile.domain.usecase.GetUserProfileUseCase
import com.example.datn_mobile.domain.usecase.UpdateUserProfileUseCase
import com.example.datn_mobile.utils.MessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val hasToken: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val preferenceDataSource: PreferenceDataSource
) : ViewModel() {

    private val _editProfileState = MutableStateFlow(EditProfileState())
    val editProfileState = _editProfileState.asStateFlow()

    init {
        checkAuthenticationAndLoadProfile()
    }

    /**
     * Kiểm tra xem user có đăng nhập và load profile
     */
    private fun checkAuthenticationAndLoadProfile() {
        viewModelScope.launch {
            preferenceDataSource.tokenFlow.collect { token ->
                val hasToken = !token.isNullOrBlank()
                _editProfileState.value = _editProfileState.value.copy(hasToken = hasToken)

                if (hasToken) {
                    _editProfileState.value = _editProfileState.value.copy(isAuthenticated = true)
                    loadUserProfile()
                } else {
                    _editProfileState.value = _editProfileState.value.copy(
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
            _editProfileState.value = _editProfileState.value.copy(isLoading = true, error = null)

            when (val result = getUserProfileUseCase()) {
                is Resource.Success -> {
                    _editProfileState.value = _editProfileState.value.copy(
                        userProfile = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    // Don't show error - let user edit with empty form
                    _editProfileState.value = _editProfileState.value.copy(
                        userProfile = null,
                        isLoading = false,
                        error = null
                    )
                }
                else -> { }
            }
        }
    }

    fun updateUserProfile(
        fullName: String?,
        address: String?,
        dob: String?
    ) {
        viewModelScope.launch {
            _editProfileState.value = _editProfileState.value.copy(
                isSaving = true,
                error = null
            )

            when (val result = updateUserProfileUseCase(
                fullName = fullName ?: "",
                address = address,
                dob = dob
            )) {
                is Resource.Success -> {
                    _editProfileState.value = _editProfileState.value.copy(
                        userProfile = result.data,
                        isSaving = false,
                        error = null
                    )
                    MessageManager.showSuccess("✅ Cập nhật profile thành công")
                }
                is Resource.Error -> {
                    _editProfileState.value = _editProfileState.value.copy(
                        isSaving = false,
                        error = result.message
                    )
                    MessageManager.showError(result.message ?: "Cập nhật profile thất bại")
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
}



