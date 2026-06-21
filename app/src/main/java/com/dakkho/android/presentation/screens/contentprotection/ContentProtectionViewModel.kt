package com.dakkho.android.presentation.screens.contentprotection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SettingsApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.ContentProtectionConfig
import com.dakkho.android.domain.model.ContentProtectionConfigDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ContentProtectionUiState(
    val config: ContentProtectionConfig = ContentProtectionConfig(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showDeviceAdminWarning: Boolean = false
)

@HiltViewModel
class ContentProtectionViewModel @Inject constructor(
    private val apiService: SettingsApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentProtectionUiState())
    val uiState: StateFlow<ContentProtectionUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val localConfig = loadFromPrefs()
                _uiState.value = _uiState.value.copy(config = localConfig, isLoading = false)
                val response = apiService.getContentProtectionConfig()
                if (response.isSuccessful) {
                    response.body()?.data?.let { dto ->
                        val apiConfig = dto.toDomain()
                        _uiState.value = _uiState.value.copy(config = apiConfig)
                        saveToPrefs(apiConfig)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load content protection config")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setScreenshotBlock(enabled: Boolean) {
        val updated = _uiState.value.config.copy(isScreenshotBlockEnabled = enabled)
        _uiState.value = _uiState.value.copy(config = updated)
        saveToPrefs(updated)
        pushToApi(updated)
        // When screenshot block is toggled, FLAG_SECURE should follow
        if (enabled) {
            val flagUpdated = updated.copy(isFlagSecureEnabled = true)
            _uiState.value = _uiState.value.copy(config = flagUpdated)
            saveToPrefs(flagUpdated)
            pushToApi(flagUpdated)
        }
    }

    fun setDownloadRestriction(enabled: Boolean) {
        val updated = _uiState.value.config.copy(isDownloadRestrictionEnabled = enabled)
        _uiState.value = _uiState.value.copy(config = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setSecurePlayerMode(enabled: Boolean) {
        val updated = _uiState.value.config.copy(isSecurePlayerModeEnabled = enabled)
        _uiState.value = _uiState.value.copy(config = updated)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setFlagSecure(enabled: Boolean) {
        if (enabled) {
            // Show device admin warning before enabling
            _uiState.value = _uiState.value.copy(showDeviceAdminWarning = true)
        } else {
            val updated = _uiState.value.config.copy(isFlagSecureEnabled = false)
            _uiState.value = _uiState.value.copy(config = updated)
            saveToPrefs(updated)
            pushToApi(updated)
        }
    }

    fun confirmFlagSecure() {
        val updated = _uiState.value.config.copy(isFlagSecureEnabled = true, hasDeviceAdminWarning = true)
        _uiState.value = _uiState.value.copy(config = updated, showDeviceAdminWarning = false)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun dismissDeviceAdminWarning() {
        _uiState.value = _uiState.value.copy(showDeviceAdminWarning = false)
    }

    private fun pushToApi(config: ContentProtectionConfig) {
        viewModelScope.launch {
            try {
                val dto = ContentProtectionConfigDto(
                    isScreenshotBlockEnabled = config.isScreenshotBlockEnabled,
                    isDownloadRestrictionEnabled = config.isDownloadRestrictionEnabled,
                    isSecurePlayerModeEnabled = config.isSecurePlayerModeEnabled,
                    isFlagSecureEnabled = config.isFlagSecureEnabled,
                    hasDeviceAdminWarning = config.hasDeviceAdminWarning
                )
                apiService.updateContentProtectionConfig(dto)
            } catch (e: Exception) {
                Timber.e(e, "Failed to push content protection config")
            }
        }
    }

    private fun saveToPrefs(config: ContentProtectionConfig) {
        prefsHelper.saveBoolean("screenshot_block", config.isScreenshotBlockEnabled)
        prefsHelper.saveBoolean("download_restriction", config.isDownloadRestrictionEnabled)
        prefsHelper.saveBoolean("secure_player_mode", config.isSecurePlayerModeEnabled)
        prefsHelper.saveBoolean("flag_secure", config.isFlagSecureEnabled)
    }

    private fun loadFromPrefs(): ContentProtectionConfig {
        val screenshotBlock = prefsHelper.getBoolean("screenshot_block", true)
        val downloadRestriction = prefsHelper.getBoolean("download_restriction", true)
        val securePlayer = prefsHelper.getBoolean("secure_player_mode", true)
        val flagSecure = prefsHelper.getBoolean("flag_secure", true)
        return ContentProtectionConfig(
            isScreenshotBlockEnabled = screenshotBlock,
            isDownloadRestrictionEnabled = downloadRestriction,
            isSecurePlayerModeEnabled = securePlayer,
            isFlagSecureEnabled = flagSecure
        )
    }
}
