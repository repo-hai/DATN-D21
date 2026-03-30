package com.example.datn_mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.util.Resource
import com.example.datn_mobile.domain.repository.AuthRepository
import com.example.datn_mobile.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val credentials = userPreferencesRepository.userCredentials.firstOrNull()
            if (credentials != null) {
                when (authRepository.login(credentials.phoneNumber, credentials.password)) {
                    is Resource.Success -> _authState.value = AuthState.Authenticated
                    is Resource.Error -> _authState.value = AuthState.Unauthenticated
                    else -> {}
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
}
