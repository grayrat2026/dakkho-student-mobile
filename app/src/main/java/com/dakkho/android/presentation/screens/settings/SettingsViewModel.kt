package com.dakkho.android.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.AppSettings
import com.dakkho.android.domain.model.AppLanguage
import com.dakkho.android.domain.model.FontSize
import com.dakkho.android.domain.model.VideoQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val showLanguageDialog: Boolean = false,
    val showVideoQualityDialog: Boolean = false,
    val showFontSizeDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val settings = AppSettings(
            isDarkMode = encryptedPrefsHelper.getBoolean("dark_mode", false),
            isNotificationsEnabled = encryptedPrefsHelper.getBoolean("notifications", true),
            isEmailNotificationsEnabled = encryptedPrefsHelper.getBoolean("email_notifications", true),
            isDownloadWifiOnly = encryptedPrefsHelper.getBoolean("wifi_only_download", true),
            videoQuality = VideoQuality.entries.find {
                it.value == encryptedPrefsHelper.getString("video_quality", "auto")
            } ?: VideoQuality.AUTO,
            language = if (encryptedPrefsHelper.getString("language", "bn") == "en")
                AppLanguage.EN else AppLanguage.BN,
            isAutoPlayNext = encryptedPrefsHelper.getBoolean("auto_play_next", true),
            isPictureInPictureEnabled = encryptedPrefsHelper.getBoolean("pip_enabled", true),
            isAnalyticsEnabled = encryptedPrefsHelper.getBoolean("analytics", true),
            fontSize = FontSize.entries.find {
                it.name == encryptedPrefsHelper.getString("font_size", "MEDIUM")
            } ?: FontSize.MEDIUM
        )
        _uiState.update { it.copy(settings = settings) }
    }

    fun setDarkMode(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("dark_mode", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isDarkMode = enabled)) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("notifications", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isNotificationsEnabled = enabled)) }
    }

    fun setEmailNotificationsEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("email_notifications", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isEmailNotificationsEnabled = enabled)) }
    }

    fun setDownloadWifiOnly(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("wifi_only_download", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isDownloadWifiOnly = enabled)) }
    }

    fun setVideoQuality(quality: VideoQuality) {
        encryptedPrefsHelper.saveString("video_quality", quality.value)
        _uiState.update {
            it.copy(
                settings = it.settings.copy(videoQuality = quality),
                showVideoQualityDialog = false
            )
        }
    }

    fun setLanguage(language: AppLanguage) {
        encryptedPrefsHelper.saveString("language", language.code)
        _uiState.update {
            it.copy(
                settings = it.settings.copy(language = language),
                showLanguageDialog = false
            )
        }
    }

    fun setAutoPlayNext(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("auto_play_next", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isAutoPlayNext = enabled)) }
    }

    fun setPictureInPicture(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("pip_enabled", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isPictureInPictureEnabled = enabled)) }
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        encryptedPrefsHelper.saveBoolean("analytics", enabled)
        _uiState.update { it.copy(settings = it.settings.copy(isAnalyticsEnabled = enabled)) }
    }

    fun setFontSize(fontSize: FontSize) {
        encryptedPrefsHelper.saveString("font_size", fontSize.name)
        _uiState.update {
            it.copy(
                settings = it.settings.copy(fontSize = fontSize),
                showFontSizeDialog = false
            )
        }
    }

    fun showLanguageDialog() { _uiState.update { it.copy(showLanguageDialog = true) } }
    fun dismissLanguageDialog() { _uiState.update { it.copy(showLanguageDialog = false) } }
    fun showVideoQualityDialog() { _uiState.update { it.copy(showVideoQualityDialog = true) } }
    fun dismissVideoQualityDialog() { _uiState.update { it.copy(showVideoQualityDialog = false) } }
    fun showFontSizeDialog() { _uiState.update { it.copy(showFontSizeDialog = true) } }
    fun dismissFontSizeDialog() { _uiState.update { it.copy(showFontSizeDialog = false) } }
    fun showDeleteAccountDialog() { _uiState.update { it.copy(showDeleteAccountDialog = true) } }
    fun dismissDeleteAccountDialog() { _uiState.update { it.copy(showDeleteAccountDialog = false) } }
}
