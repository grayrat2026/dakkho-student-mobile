package com.dakkho.android.presentation.screens.contactsupport

import androidx.lifecycle.ViewModel
import com.dakkho.android.data.api.SupportApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ContactSupportUiState(
    val isLoading: Boolean = false,
    val operatingDays: String = "শনিবার-বৃহস্পতিবার",
    val operatingHours: String = "সকাল ৯টা - সন্ধ্যা ৬টা",
    val supportEmail: String = "support@dakkho.com.bd",
    val supportPhone: String = "+880 1234-567890",
    val isLiveChatAvailable: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ContactSupportViewModel @Inject constructor(
    private val apiService: SupportApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactSupportUiState())
    val uiState: StateFlow<ContactSupportUiState> = _uiState.asStateFlow()

    fun updateLiveChatAvailability(isAvailable: Boolean) {
        _uiState.update { it.copy(isLiveChatAvailable = isAvailable) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
