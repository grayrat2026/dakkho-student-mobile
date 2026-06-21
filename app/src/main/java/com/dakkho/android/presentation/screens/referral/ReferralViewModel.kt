package com.dakkho.android.presentation.screens.referral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.ReferralData
import com.dakkho.android.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ReferralUiState(
    val referralData: ReferralData? = null,
    val isLoading: Boolean = true,
    val isSharing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferralUiState())
    val uiState: StateFlow<ReferralUiState> = _uiState.asStateFlow()

    init {
        loadReferralData()
    }

    fun loadReferralData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            profileRepository.getReferralData()
                .onSuccess { data ->
                    _uiState.update { it.copy(referralData = data, isLoading = false) }
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to load referral data")
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun generateNewCode() {
        viewModelScope.launch {
            profileRepository.generateReferralCode()
                .onSuccess { data ->
                    _uiState.update { it.copy(referralData = data) }
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to generate referral code")
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
