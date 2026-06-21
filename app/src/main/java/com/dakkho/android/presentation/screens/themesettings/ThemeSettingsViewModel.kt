package com.dakkho.android.presentation.screens.themesettings

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.data.api.SettingsApiService
import com.dakkho.android.data.db.EncryptedPrefsHelper
import com.dakkho.android.domain.model.AccentColor
import com.dakkho.android.domain.model.ThemeMode
import com.dakkho.android.domain.model.ThemeSettings
import com.dakkho.android.domain.model.ThemeSettingsDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ThemeSettingsUiState(
    val settings: ThemeSettings = ThemeSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ThemeSettingsViewModel @Inject constructor(
    private val apiService: SettingsApiService,
    private val prefsHelper: EncryptedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeSettingsUiState())
    val uiState: StateFlow<ThemeSettingsUiState> = _uiState.asStateFlow()

    val isDynamicColorAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val localSettings = loadFromPrefs()
                _uiState.value = _uiState.value.copy(settings = localSettings, isLoading = false)
                val response = apiService.getThemeSettings()
                if (response.isSuccessful) {
                    response.body()?.data?.let { dto ->
                        val apiSettings = dto.toDomain()
                        _uiState.value = _uiState.value.copy(settings = apiSettings)
                        saveToPrefs(apiSettings)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load theme settings")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        val updated = _uiState.value.settings.copy(themeMode = mode)
        _uiState.value = _uiState.value.copy(settings = updated, saveSuccess = false)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setAccentColor(color: AccentColor) {
        val updated = _uiState.value.settings.copy(accentColor = color)
        _uiState.value = _uiState.value.copy(settings = updated, saveSuccess = false)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(isDynamicColorEnabled = enabled)
        _uiState.value = _uiState.value.copy(settings = updated, saveSuccess = false)
        saveToPrefs(updated)
        pushToApi(updated)
    }

    private fun pushToApi(settings: ThemeSettings) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val dto = ThemeSettingsDto(
                    themeMode = settings.themeMode.value,
                    accentColor = settings.accentColor.value,
                    isDynamicColorEnabled = settings.isDynamicColorEnabled
                )
                val response = apiService.updateThemeSettings(dto)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                } else {
                    _uiState.value = _uiState.value.copy(isSaving = false, error = "সেভ ব্যর্থ")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save theme settings")
                _uiState.value = _uiState.value.copy(isSaving = false, error = "নেটওয়ার্ক ত্রুটি")
            }
        }
    }

    private fun saveToPrefs(settings: ThemeSettings) {
        prefsHelper.saveString("theme_mode", settings.themeMode.value)
        prefsHelper.saveString("accent_color", settings.accentColor.value)
        prefsHelper.saveBoolean("dynamic_color_enabled", settings.isDynamicColorEnabled)
    }

    private fun loadFromPrefs(): ThemeSettings {
        val themeModeValue = prefsHelper.getString("theme_mode") ?: "system"
        val accentColorValue = prefsHelper.getString("accent_color") ?: "sky_blue"
        val dynamicColor = prefsHelper.getBoolean("dynamic_color_enabled", false)
        return ThemeSettings(
            themeMode = ThemeMode.fromValue(themeModeValue),
            accentColor = AccentColor.fromValue(accentColorValue),
            isDynamicColorEnabled = dynamicColor
        )
    }
}
