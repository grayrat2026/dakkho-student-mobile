package com.dakkho.android.presentation.screens.activesessions

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SettingsApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ActiveSession
import com.dakkho.android.domain.model.ActiveSessionDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ActiveSessionsUiState(
    val sessions: List<ActiveSession> = emptyList(),
    val currentDeviceFingerprint: DeviceFingerprint = DeviceFingerprint(),
    val isLoading: Boolean = false,
    val isLoggingOut: String? = null,  // session ID being logged out
    val error: String? = null,
    val showLogoutConfirmDialog: String? = null  // session ID to confirm logout
)

data class DeviceFingerprint(
    val model: String = Build.MODEL,
    val osVersion: String = Build.VERSION.RELEASE,
    val sdkInt: Int = Build.VERSION.SDK_INT,
    val manufacturer: String = Build.MANUFACTURER,
    val lastIpAddress: String = "",
    val lastActiveTimestamp: String = ""
) {
    val displayString: String get() = "$manufacturer $model (Android $osVersion)"
}

@HiltViewModel
class ActiveSessionsViewModel @Inject constructor(
    private val apiService: SettingsApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveSessionsUiState())
    val uiState: StateFlow<ActiveSessionsUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.getActiveSessions()
                if (response.isSuccessful) {
                    val sessions = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        sessions = sessions,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        sessions = getMockSessions(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load sessions, using mock data")
                _uiState.value = _uiState.value.copy(
                    sessions = getMockSessions(),
                    isLoading = false
                )
            }
        }
    }

    fun showLogoutConfirm(sessionId: String) {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = sessionId)
    }

    fun dismissLogoutConfirm() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = null)
    }

    fun logoutSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggingOut = sessionId)
            try {
                val response = apiService.logoutSession(sessionId)
                if (response.isSuccessful) {
                    val updated = _uiState.value.sessions.filter { it.id != sessionId }
                    _uiState.value = _uiState.value.copy(
                        sessions = updated,
                        isLoggingOut = null,
                        showLogoutConfirmDialog = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoggingOut = null,
                        showLogoutConfirmDialog = null,
                        error = "লগআউট ব্যর্থ"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to logout session")
                _uiState.value = _uiState.value.copy(
                    isLoggingOut = null,
                    showLogoutConfirmDialog = null,
                    error = "নেটওয়ার্ক ত্রুটি"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun getMockSessions(): List<ActiveSession> {
        return listOf(
            ActiveSession(
                id = "current",
                deviceModel = Build.MODEL,
                osVersion = Build.VERSION.RELEASE,
                appVersion = "1.0.0",
                lastActiveTime = "এইমাত্র",
                lastIpAddress = "192.168.1.1",
                isActive = true,
                isCurrentDevice = true
            ),
            ActiveSession(
                id = "prev_1",
                deviceModel = "Samsung Galaxy A12",
                osVersion = "12",
                appVersion = "0.9.5",
                lastActiveTime = "২ ঘন্টা আগে",
                lastIpAddress = "103.45.67.89",
                isActive = false,
                isCurrentDevice = false
            )
        )
    }
}
