package com.dakkho.android.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class ForgotPasswordState {
        data object Idle : ForgotPasswordState()
        data object Loading : ForgotPasswordState()
        data object Success : ForgotPasswordState()
        data class Error(val message: String) : ForgotPasswordState()
    }

    private val _state = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
        if (_state.value is ForgotPasswordState.Error) {
            _state.value = ForgotPasswordState.Idle
        }
    }

    fun submitEmail() {
        val emailValue = _email.value.trim()

        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Enter a valid email address"
            return
        }

        _state.value = ForgotPasswordState.Loading

        viewModelScope.launch {
            val result = authRepository.forgotPassword(email = emailValue)

            result.onSuccess {
                _state.value = ForgotPasswordState.Success
            }.onFailure { error ->
                _state.value = ForgotPasswordState.Error(
                    message = error.localizedMessage ?: "Failed to send reset email. Please try again."
                )
            }
        }
    }

    fun resetState() {
        _state.value = ForgotPasswordState.Idle
    }
}
