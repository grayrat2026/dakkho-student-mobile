package com.dakkho.android.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
        _loginState.value = LoginState.Idle
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = null
        _loginState.value = LoginState.Idle
    }

    fun onPasswordVisibilityToggle() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun login() {
        if (!validateInputs()) return

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = authRepository.login(
                email = _email.value.trim(),
                password = _password.value
            )

            result.onSuccess { authResponse ->
                _loginState.value = LoginState.Success(
                    user = User(
                        id = authResponse.user.id,
                        email = authResponse.user.email,
                        fullName = authResponse.user.fullName,
                        instituteId = authResponse.user.instituteId,
                        technology = authResponse.user.technology,
                        avatarUrl = authResponse.user.avatarUrl,
                        role = authResponse.user.role,
                        phone = authResponse.user.phone,
                        isVerified = authResponse.user.isVerified ?: false,
                        createdAt = authResponse.user.createdAt
                    )
                )
            }.onFailure { error ->
                _loginState.value = LoginState.Error(
                    message = error.localizedMessage ?: "Login failed. Please try again."
                )
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val emailValue = _email.value.trim()
        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Enter a valid email address"
            isValid = false
        }

        if (_password.value.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (_password.value.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }
}
