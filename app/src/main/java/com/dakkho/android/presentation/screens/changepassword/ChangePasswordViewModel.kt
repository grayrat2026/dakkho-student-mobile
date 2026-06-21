package com.dakkho.android.presentation.screens.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isChanging: Boolean = false,
    val isChanged: Boolean = false,
    val error: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChanged(password: String) {
        _uiState.update { it.copy(currentPassword = password, currentPasswordError = null) }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, newPasswordError = null, confirmPasswordError = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, confirmPasswordError = null) }
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) }
    }

    fun toggleNewPasswordVisibility() {
        _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun changePassword() {
        val state = _uiState.value
        var hasError = false

        if (state.currentPassword.isBlank()) {
            _uiState.update { it.copy(currentPasswordError = "বর্তমান পাসওয়ার্ড আবশ্যক") }
            hasError = true
        }

        if (state.newPassword.length < 8) {
            _uiState.update { it.copy(newPasswordError = "পাসওয়ার্ড কমপক্ষে ৮ অক্ষর হতে হবে") }
            hasError = true
        }

        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "পাসওয়ার্ড মিলছে না") }
            hasError = true
        }

        if (state.currentPassword == state.newPassword) {
            _uiState.update { it.copy(newPasswordError = "নতুন পাসওয়ার্ড পুরানোটির থেকে ভিন্ন হতে হবে") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isChanging = true, error = null) }

            profileRepository.changePassword(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword,
                confirmPassword = state.confirmPassword
            ).onSuccess {
                _uiState.update { it.copy(isChanging = false, isChanged = true) }
            }.onFailure { e ->
                Timber.e(e, "Change password failed")
                _uiState.update {
                    it.copy(
                        isChanging = false,
                        error = e.message ?: "পাসওয়ার্ড পরিবর্তন ব্যর্থ হয়েছে"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
