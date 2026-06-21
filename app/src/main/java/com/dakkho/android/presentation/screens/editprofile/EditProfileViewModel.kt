package com.dakkho.android.presentation.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.repository.AuthRepository
import com.dakkho.android.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class EditProfileUiState(
    val initialUser: User? = null,
    val fullName: String = "",
    val phone: String = "",
    val technology: String = "",
    val avatarUrl: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val fullNameError: String? = null,
    val phoneError: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            authRepository.getProfile()
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            initialUser = user,
                            fullName = user.fullName,
                            phone = user.phone ?: "",
                            technology = user.technology ?: "",
                            avatarUrl = user.avatarUrl ?: ""
                        )
                    }
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load profile for editing")
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun onTechnologyChanged(technology: String) {
        _uiState.update { it.copy(technology = technology) }
    }

    fun onAvatarChanged(url: String) {
        _uiState.update { it.copy(avatarUrl = url) }
    }

    fun saveProfile() {
        val state = _uiState.value

        // Validate
        var hasError = false
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(fullNameError = "নাম আবশ্যক") }
            hasError = true
        }
        if (state.phone.isNotBlank() && !isValidPhone(state.phone)) {
            _uiState.update { it.copy(phoneError = "সঠিক ফোন নম্বর দিন") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            profileRepository.updateProfile(
                fullName = state.fullName.trim(),
                phone = state.phone.trim().ifBlank { null },
                avatarUrl = state.avatarUrl.trim().ifBlank { null },
                instituteId = state.initialUser?.instituteId,
                technology = state.technology.trim().ifBlank { null }
            ).onSuccess { updatedUser ->
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }.onFailure { e ->
                Timber.e(e, "Failed to save profile")
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "প্রোফাইল আপডেট ব্যর্থ হয়েছে") }
            }
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val bdPhone = phone.replace(Regex("[\\s\\-]"), "")
        return bdPhone.matches(Regex("^(\\+880|880|0)1[3-9]\\d{8}$"))
    }

    fun dismissSaved() {
        _uiState.update { it.copy(isSaved = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
